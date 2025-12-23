package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.example.carson_umaplicativoparadescartedemedicamentos.utils.LocaleHelper;
import com.example.carson_umaplicativoparadescartedemedicamentos.workers.ValidadeWorker;


public class Activity_settings extends AppCompatActivity {

    private SwitchCompat switchNotifications, switchTheme;
    private Spinner spinnerLanguage;
    // O botão btnTestar foi removido desta classe, pois não existe mais no XML.

    private SharedPreferences preferences;
    private static final String PREF_NAME = "CarsonPrefs";
    private static final String CHAVE_NOTIFICACOES_VALIDADE = "notificacoes_validade";
    private static final String CHAVE_TEMA = "app_tema";

    private final String[] LANGUAGE_CODES = {"pt", "en", "es"};


    @Override
    protected void attachBaseContext(Context newBase) {
        // Isso é essencial para manter o idioma do aplicativo
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // A BaseActivity já aplica o tema, mas chamamos de novo por segurança
        // caso esta Activity seja a launcher (o que não é o caso aqui).

        setContentView(R.layout.activity_settings);

        // Inicializa os componentes
        ImageButton btnBack = findViewById(R.id.btnBack);
        switchNotifications = findViewById(R.id.switchNotifications);
        switchTheme = findViewById(R.id.switchTheme);
        spinnerLanguage = findViewById(R.id.spinnerLanguage);
        // btnTestar = findViewById(R.id.btnTestarNotificacao); <-- LINHA REMOVIDA
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);


        configurarSpinner();
        loadSettings();

        btnBack.setOnClickListener(v -> finish());

        // 🔔 Listener do switch de Alertas de Validade
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSetting(CHAVE_NOTIFICACOES_VALIDADE, isChecked);
            Toast.makeText(this, isChecked ? "Alertas de validade ativados! 🔔" : "Alertas desativados.", Toast.LENGTH_SHORT).show();
        });

        // 🌓 Listener do switch de Tema (DARK MODE)
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveSetting(CHAVE_TEMA, isChecked);

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            Toast.makeText(this, isChecked ? "Tema Escuro Ativado" : "Tema Claro Ativado", Toast.LENGTH_SHORT).show();
        });


        // 🔄 Listener do Idioma
        spinnerLanguage.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String novoCodigo = LANGUAGE_CODES[position];
                String codigoAtual = LocaleHelper.getPersistedData(Activity_settings.this, "pt");

                if (!novoCodigo.equals(codigoAtual)) {
                    LocaleHelper.setLocale(Activity_settings.this, novoCodigo);

                    // Reinicia o aplicativo para aplicar as mudanças em todos os componentes
                    Intent refresh = new Intent(Activity_settings.this, Activity_settings.class);
                    refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(refresh);
                    finish();
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }


    private void configurarSpinner() {
        String[] languages = {"Português (BR)", "Inglês (EN)", "Espanhol (ES)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLanguage.setAdapter(adapter);
    }

    private void loadSettings() {
        boolean notificacoes = preferences.getBoolean(CHAVE_NOTIFICACOES_VALIDADE, true);
        boolean isDarkMode = preferences.getBoolean(CHAVE_TEMA, false);

        switchNotifications.setChecked(notificacoes);
        switchTheme.setChecked(isDarkMode);

        String codigoAtual = LocaleHelper.getPersistedData(this, "pt");

        // Pega o adapter para setar a seleção
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerLanguage.getAdapter();
        if(adapter != null) {
            for (int i = 0; i < LANGUAGE_CODES.length; i++) {
                if (LANGUAGE_CODES[i].equals(codigoAtual)) {
                    spinnerLanguage.setSelection(i);
                    break;
                }
            }
        }
    }

    private void saveSetting(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }
}