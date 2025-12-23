package com.example.carson_umaplicativoparadescartedemedicamentos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class tela_de_login extends AppCompatActivity {

    private EditText inputEmail, inputSenha;
    private Button btnLogin;
    private TextView txtCadastreSe, txtEsqueceuSenha;
    private CheckBox chkLembreDeMim; // ✅ Novo
    private LinearLayout loadingOverlay;

    private FirebaseAuth auth;
    private DatabaseReference usuariosRef;
    private SharedPreferences sharedPreferences; // ✅ Para salvar a escolha

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_de_login);

        auth = FirebaseAuth.getInstance();
        usuariosRef = FirebaseDatabase.getInstance().getReference("pessoas");
        sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE); // ✅

        // Vincula componentes
        inputEmail = findViewById(R.id.emailInput);
        inputSenha = findViewById(R.id.passwordInput);
        btnLogin = findViewById(R.id.loginButton);
        chkLembreDeMim = findViewById(R.id.chkLembreDeMim); // ✅
        loadingOverlay = findViewById(R.id.loadingOverlay);
        txtCadastreSe = findViewById(R.id.registerLink);
        txtEsqueceuSenha = findViewById(R.id.forgotPassword);

        btnLogin.setOnClickListener(v -> realizarLogin());

        txtCadastreSe.setOnClickListener(v -> {
            Intent intent = new Intent(tela_de_login.this, Cadastro_dados_pessoais.class);
            startActivity(intent);
        });

        txtEsqueceuSenha.setOnClickListener(v -> {
            Intent intent = new Intent(tela_de_login.this, activity_recuperar_senha.class);
            startActivity(intent);
        });
    }

    // 🟢 VERIFICAÇÃO AUTOMÁTICA AO ABRIR O APP
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = auth.getCurrentUser();
        boolean lembrar = sharedPreferences.getBoolean("lembrar", false);

        // Se tem usuário logado E a opção "Lembrar" estava marcada
        if (usuarioAtual != null && lembrar) {
            loadingOverlay.setVisibility(View.VISIBLE);
            verificarTipoUsuario();
        }
    }

    private void realizarLogin() {
        String email = inputEmail.getText().toString().trim();
        String senha = inputSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingOverlay.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isChecked = chkLembreDeMim.isChecked();
                        sharedPreferences.edit().putBoolean("lembrar", isChecked).apply();

                        verificarTipoUsuario();
                    } else {
                        loadingOverlay.setVisibility(View.GONE);
                        String erro = task.getException() != null ? task.getException().getMessage() : "Erro ao logar.";
                        Toast.makeText(this, "Falha: " + erro, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verificarTipoUsuario() {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            loadingOverlay.setVisibility(View.GONE);
            return;
        }

        usuariosRef.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        loadingOverlay.setVisibility(View.GONE);

                        if (!snapshot.exists()) {
                            // Se o usuário foi deletado do banco mas ainda tá no Auth
                            auth.signOut();
                            Toast.makeText(tela_de_login.this, "Usuário não encontrado!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String tipo = snapshot.child("tipoUsuario").getValue(String.class);

                        if ("moderador".equals(tipo)) {
                            abrirTelaModerador();
                        } else {
                            abrirTelaUsuario();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loadingOverlay.setVisibility(View.GONE);
                        Toast.makeText(tela_de_login.this, "Erro de conexão!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void abrirTelaModerador() {
        Intent i = new Intent(tela_de_login.this, activity_moderador_dashboard.class);
        startActivity(i);
        finish();
    }

    private void abrirTelaUsuario() {
        Intent i = new Intent(tela_de_login.this, HomeActivity.class);
        startActivity(i);
        finish();
    }
}