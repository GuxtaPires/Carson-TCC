package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class activity_recuperar_senha extends AppCompatActivity {

    private EditText emailInput;
    private Button btnRecuperar;
    private ImageButton btnBack;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        auth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.emailInput);
        btnRecuperar = findViewById(R.id.btnRecuperar);
        btnBack = findViewById(R.id.btnVoltar);

        // 🔹 Voltar pra tela de login
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, tela_de_login.class);
            startActivity(intent);
            finish();
        });

        // 🔹 Recuperar senha
        btnRecuperar.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Informe o e-mail cadastrado!", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "E-mail de redefinição enviado! Verifique sua caixa de entrada.", Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao enviar e-mail: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });
    }
}
