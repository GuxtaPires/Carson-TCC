package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Random;

public class GuiaActivity extends BaseActivity {

    // Componentes da UI
    private TextView txtTitle, txtContent;
    // CardView cardGuia; // Não está sendo usado diretamente no clique, mas mantive a declaração se precisar

    // Bottom Navigation
    private ImageButton btnLoc, btnMed, btnHome, btnNews, btnSearch;

    // Arrays para armazenar o conteúdo carregado do XML
    private String[] titulos;
    private String[] conteudos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Configura o layout (Garanta que o nome do XML está correto, ex: activity_guia)
        setupDrawer(R.layout.activity_educacional);

        // Inicializa componentes
        txtTitle = findViewById(R.id.txtGuiaTitle);
        txtContent = findViewById(R.id.txtGuiaContent);

        // Carrega os textos do strings.xml (Isso traduz automaticamente!)
        titulos = getResources().getStringArray(R.array.dicas_titulos);
        conteudos = getResources().getStringArray(R.array.dicas_conteudos);

        // Configura cliques
        txtContent.setOnClickListener(v -> mostrarDicaAleatoria());
        txtTitle.setOnClickListener(v -> mostrarDicaAleatoria());

        // Carrega uma dica assim que abre
        mostrarDicaAleatoria();

        // Configura navegação inferior
        configurarBottomNav();

        // Mostra uma dica de uso na primeira vez (Texto traduzido)
        Toast.makeText(this, getString(R.string.toast_tip_hint), Toast.LENGTH_LONG).show();
    }

    // =========================================================================
    // 🎲 LÓGICA DA DICA ALEATÓRIA
    // =========================================================================
    private void mostrarDicaAleatoria() {
        if (titulos == null || titulos.length == 0) return;

        int index = new Random().nextInt(titulos.length);

        txtTitle.setText(titulos[index]);

        // Html.fromHtml permite usar negrito e quebra de linha
        txtContent.setText(Html.fromHtml(conteudos[index], Html.FROM_HTML_MODE_LEGACY));
    }

    // =========================================================================
    // 🧭 NAVEGAÇÃO INFERIOR PADRÃO
    // =========================================================================
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

        // Botão Notícias (Guia)
        btnNews.setOnClickListener(v -> {
            mostrarSnackBar(getString(R.string.snack_already_on_guide));
            mostrarDicaAleatoria(); // Troca a dica ao clicar de novo
        });

        btnSearch.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificacoesActivity.class)); // Verifique se é a tela correta
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void mostrarSnackBar(String mensagem) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(0xFF2B7A2B); // Verde Carson
        snackbar.setTextColor(0xFFFFFFFF);
        snackbar.show();
    }
}