package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class tela_de_login extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_de_login); // <-- nome do teu XML

        // 🔍 Vincula os componentes
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);

        // 🎯 Quando clicar em "Entrar"
        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String senha = passwordInput.getText().toString().trim();

            // Validação básica (só pra teste)
            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            } else if (email.equals("teste@email.com") && senha.equals("1234")) {
                // ✅ Login bem-sucedido
                Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                // Ir para a tela inicial (ajusta o nome da tua Activity principal)
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish(); // Fecha a tela de login
            } else {
                // ❌ Login falhou
                Toast.makeText(this, "E-mail ou senha incorretos!", Toast.LENGTH_SHORT).show();
            }
        });

        // 📲 Quando clicar em "Cadastre-se aqui"
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, activity_cadastro_medicamento.class);
            startActivity(intent);
        });
    }
}
