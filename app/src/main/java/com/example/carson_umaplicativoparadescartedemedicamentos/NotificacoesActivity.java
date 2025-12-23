package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificacoesActivity extends BaseActivity {

    private DatabaseReference notificacoesRef;
    private LinearLayout notificacoesContainer;
    private FirebaseAuth auth;

    // BOTTOM NAV
    private ImageButton btnLoc, btnMed, btnHome, btnNews, btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_notifications);

        auth = FirebaseAuth.getInstance();
        notificacoesContainer = findViewById(R.id.notificacoes_container);

        configurarBottomNav();

        // LÓGICA DAS NOTIFICAÇÕES
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Referência para notificações do usuário
        notificacoesRef = FirebaseDatabase.getInstance().getReference("notificacoes").child(userId);
        Query queryNotif = notificacoesRef.orderByChild("data");

        queryNotif.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                carregarNotificacoes(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 🔹 Verifica se o usuário ainda está logado antes de mostrar toast
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Toast.makeText(NotificacoesActivity.this, "Erro ao carregar notificações.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void carregarNotificacoes(DataSnapshot snapshot) {
        notificacoesContainer.removeAllViews();

        if (snapshot.exists()) {
            List<DataSnapshot> lista = new ArrayList<>();
            for (DataSnapshot s : snapshot.getChildren()) lista.add(s);

            // O Firebase não ordena por data de criação. Invertemos para o mais novo vir primeiro.
            Collections.reverse(lista);

            for (DataSnapshot notifSnapshot : lista) {
                final String notifId = notifSnapshot.getKey();
                String titulo = notifSnapshot.child("titulo").getValue(String.class);
                String mensagem = notifSnapshot.child("mensagem").getValue(String.class);
                String data = notifSnapshot.child("data").getValue(String.class);

                View cardView = LayoutInflater.from(this).inflate(R.layout.activity_item_notificacao, notificacoesContainer, false);

                TextView txtTitulo = cardView.findViewById(R.id.txtTituloNotif);
                TextView txtMsg = cardView.findViewById(R.id.txtMensagemNotif);
                TextView txtData = cardView.findViewById(R.id.txtDataNotif);
                ImageButton btnExcluir = cardView.findViewById(R.id.btnExcluirNotif);

                txtTitulo.setText(titulo != null ? titulo : "Aviso");
                txtMsg.setText(mensagem);
                txtData.setText(data);

                btnExcluir.setOnClickListener(v -> excluirNotificacao(notifId));

                notificacoesContainer.addView(cardView);
            }
        } else {
            mostrarMensagemVazia();
        }
    }

    private void excluirNotificacao(String notifId) {
        notificacoesRef.child(notifId).removeValue()
                .addOnSuccessListener(aVoid -> mostrarSnackBar("Notificação excluída!"))
                .addOnFailureListener(e -> Toast.makeText(this, "Falha ao excluir.", Toast.LENGTH_SHORT).show());
    }

    private void mostrarMensagemVazia() {
        TextView vazio = new TextView(this);
        vazio.setText("Nenhuma notificação recebida ainda! 🍃");
        vazio.setTextSize(16);
        vazio.setTextColor(getResources().getColor(android.R.color.darker_gray));
        vazio.setPadding(0, 50, 0, 50);
        vazio.setGravity(android.view.Gravity.CENTER);
        notificacoesContainer.addView(vazio);
    }

    private void configurarBottomNav() {
        btnLoc = findViewById(R.id.btnLoc);
        btnMed = findViewById(R.id.btnMed);
        btnHome = findViewById(R.id.btnHome);
        btnNews = findViewById(R.id.btnNews);
        btnSearch = findViewById(R.id.btnSearch);

        btnLoc.setOnClickListener(v -> {
            startActivity(new Intent(this, MapsActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        btnMed.setOnClickListener(v -> {
            startActivity(new Intent(this, MedicamentosActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        btnNews.setOnClickListener(v -> {
            startActivity(new Intent(this, GuiaActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        btnSearch.setOnClickListener(v -> mostrarSnackBar("Você já está aqui!"));
    }

    private void mostrarSnackBar(String mensagem) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(0xFF2B7A2B);
        snackbar.setTextColor(0xFFFFFFFF);
        snackbar.show();
    }
}
