package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.app.Application;
import android.util.Log; // Adicionado
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Garante que o Firebase está inicializado (você já tinha isso)
        FirebaseApp.initializeApp(this);

        // 🚨 BLOCO TRY-CATCH PARA ATIVAR A PERSISTÊNCIA DE FORMA SEGURA
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            Log.d("CarsonApp", "Persistência Firebase ativada com sucesso!");
        } catch (Exception e) {
            // Se já foi ativado, ele cai aqui. Não fazemos nada.
            Log.w("CarsonApp", "Persistência Firebase já foi ativada ou falhou: " + e.getMessage());
        }
    }
}