package com.example.carson_umaplicativoparadescartedemedicamentos;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

// 🚨 Não precisamos dos imports de conexão do Firebase aqui

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referência do botão (Assumindo ID 'startButton')
        Button startButton = findViewById(R.id.startButton);

        // Ação ao clicar no botão "Começar!"
        if (startButton != null) {
            startButton.setOnClickListener(v -> {
                // Redireciona para a tela de login
                Intent intent = new Intent(MainActivity.this, tela_de_login.class);
                startActivity(intent);

                // Fecha a tela inicial
                finish();
            });
        }

    }
}