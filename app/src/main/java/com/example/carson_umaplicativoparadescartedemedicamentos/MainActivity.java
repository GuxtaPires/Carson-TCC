package com.example.carson_umaplicativoparadescartedemedicamentos;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Usa o XML que você mandou

        // Referência do botão
        Button startButton = findViewById(R.id.startButton);

        // Ação ao clicar no botão "Começar!"
        startButton.setOnClickListener(v -> {
            // Cria uma intent pra abrir a tela de login
            Intent intent = new Intent(MainActivity.this, tela_de_login.class);
            startActivity(intent);

            // (opcional) fecha a tela inicial pra não voltar pra ela com o "voltar"
            finish();
        });
    }
}
