package com.example.carson_umaplicativoparadescartedemedicamentos.workers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.carson_umaplicativoparadescartedemedicamentos.HomeActivity;
import com.example.carson_umaplicativoparadescartedemedicamentos.R;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ValidadeWorker extends Worker {

    private static final String CHANNEL_ID = "canal_validade_medicamentos";

    public ValidadeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        SharedPreferences prefs = context.getSharedPreferences("CarsonPrefs", Context.MODE_PRIVATE);
        boolean notificacoesAtivas = prefs.getBoolean("notificacoes_validade", true);

        if (!notificacoesAtivas) {
            return Result.success();
        }

        String userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            // Se o usuário não está logado (ou foi deslogado), o worker falha
            return Result.failure();
        }

        try {
            // 🚨 MUDANÇA CRUCIAL: Acessamos DIRETAMENTE a pasta do usuário (medicamentos/{USER_ID})
            DatabaseReference userMedsRef = FirebaseDatabase.getInstance()
                    .getReference("medicamentos")
                    .child(userId);

            DataSnapshot snapshot = Tasks.await(userMedsRef.get());

            Log.d("ValidadeWorker", "Lendo medicamentos para o usuário: " + userId + " - Total: " + snapshot.getChildrenCount());

            for (DataSnapshot data : snapshot.getChildren()) {
                String nome = data.child("nome").getValue(String.class);
                String validadeStr = data.child("validade").getValue(String.class);
                String situacao = data.child("situacao").getValue(String.class);

                if (nome != null && validadeStr != null && !"Descartado".equals(situacao)) {
                    verificarValidadePorMes(context, nome, validadeStr);
                }
            }

            return Result.success();

        } catch (ExecutionException | InterruptedException e) {
            Log.e("ValidadeWorker", "Erro ao acessar Firebase: " + e.getMessage());
            return Result.retry();
        }
    }

    // 🟢 LÓGICA MATEMÁTICA (POR MESES)
    private void verificarValidadePorMes(Context context, String nomeMed, String validadeStr) {
        try {
            String[] partes = validadeStr.split("/");
            if (partes.length != 2) return;

            int mesValidade = Integer.parseInt(partes[0]);
            int anoValidade = Integer.parseInt(partes[1]);

            Calendar hoje = Calendar.getInstance();
            int mesAtual = hoje.get(Calendar.MONTH) + 1;
            int anoAtual = hoje.get(Calendar.YEAR);

            long totalMesesValidade = (anoValidade * 12L) + mesValidade;
            long totalMesesHoje = (anoAtual * 12L) + mesAtual;

            long diferencaMeses = totalMesesValidade - totalMesesHoje;

            String titulo;
            String mensagem;

            if (diferencaMeses == 3) {
                titulo = "Atenção 📅";
                mensagem = "O medicamento " + nomeMed + " vence daqui a 3 meses.";
            } else if (diferencaMeses == 2) {
                titulo = "Fique ligado ⏳";
                mensagem = "O medicamento " + nomeMed + " vence em 2 meses.";
            } else if (diferencaMeses == 1) {
                titulo = "Vence mês que vem! 🚨";
                mensagem = "O medicamento " + nomeMed + " vence no próximo mês.";
            } else if (diferencaMeses == 0) {
                titulo = "Vence este mês ⚠️";
                mensagem = "O medicamento " + nomeMed + " vence agora em " + mesValidade + "/" + anoValidade + ".";
            } else if (diferencaMeses < 0) {
                titulo = "Medicamento Vencido 🚫";
                mensagem = nomeMed + " já venceu! Faça o descarte correto.";
            } else {
                return; // Não envia notificação se a diferença for maior que 3 meses
            }

            enviarNotificacao(context, titulo, mensagem);

        } catch (NumberFormatException e) {
            Log.e("Worker", "Erro ao ler data: " + validadeStr);
        }
    }

    private void enviarNotificacao(Context context, String titulo, String mensagem) {
        // 1. Cria o Canal
        criarCanalNotificacao(context);

        // 2. Configura o clique (Abrir o App)
        Intent intent = new Intent(context, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // 3. Monta a notificação visual
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_med)
                .setContentTitle(titulo)
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // 4. Exibe no celular
        try {
            // ID único para cada notificação baseada no hash do texto
            notificationManager.notify((titulo + mensagem).hashCode(), builder.build());
        } catch (SecurityException e) {
            // Permissão negada (Android 13+)
        }

        // 5. SALVAR NO FIREBASE (PARA APARECER NA TELA DE NOTIFICAÇÕES)
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("notificacoes").child(userId).push();

            String dataHoje = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

            ref.child("titulo").setValue(titulo);
            ref.child("mensagem").setValue(mensagem);
            ref.child("data").setValue(dataHoje);
        }
    }

    private void criarCanalNotificacao(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Validade Medicamentos";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}