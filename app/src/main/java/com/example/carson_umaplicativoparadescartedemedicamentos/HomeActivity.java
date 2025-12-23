package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carson_umaplicativoparadescartedemedicamentos.adapters.NewsAdapter;
import com.example.carson_umaplicativoparadescartedemedicamentos.api.NewsApiService;
import com.example.carson_umaplicativoparadescartedemedicamentos.models.NewsResponse;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends BaseActivity {

    private RecyclerView recyclerNews;
    private ProgressBar progressBar;
    private TextView txtDataColeta;

    private ImageButton btnLoc, btnMed, btnHome, btnNews, btnSearch;

    private static final String API_KEY = "91d1aa7f35a14351bc83fd71c7693639";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Carrega layout via BaseActivity
        setupDrawer(R.layout.activity_atualizacoes);

// Só troca o menu DEPOIS que setupDrawer já criou a navigationView
        new Handler(Looper.getMainLooper()).post(() ->
                setDrawerMenu(R.menu.menu_home)
        );


        recyclerNews = findViewById(R.id.recyclerNews);
        progressBar = findViewById(R.id.progressBar);
        txtDataColeta = findViewById(R.id.txtDataColeta);

        recyclerNews.setLayoutManager(new LinearLayoutManager(this));

        new Handler(Looper.getMainLooper()).postDelayed(this::loadNews, 100);

        configurarBottomNav();
    }

    private void loadNews() {
        progressBar.setVisibility(View.VISIBLE);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String inicioSemana = dateFormat.format(cal.getTime());

        SimpleDateFormat formatBR = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        txtDataColeta.setText("Última atualização: " + formatBR.format(Calendar.getInstance().getTime()));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApiService api = retrofit.create(NewsApiService.class);

        String sites = "globo.com,uol.com.br,cnnbrasil.com.br,bbc.com,estadao.com.br,folha.uol.com.br,gov.br,metropoles.com";

        Call<NewsResponse> call = api.getNews("medicamentos OR anvisa", sites, inicioSemana, "pt", API_KEY);

        call.enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(Call<NewsResponse> call, Response<NewsResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getArticles().isEmpty()) {
                        mostrarSnackBar("Nenhuma notícia desta semana.");
                    } else {
                        recyclerNews.setAdapter(new NewsAdapter(HomeActivity.this, response.body().getArticles()));
                    }
                } else {
                    mostrarSnackBar("Erro ao carregar notícias.");
                }
            }

            @Override
            public void onFailure(Call<NewsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                mostrarSnackBar("Falha de conexão.");
            }
        });
    }

    private void configurarBottomNav() {
        btnLoc = findViewById(R.id.btnLoc);
        btnMed = findViewById(R.id.btnMed);
        btnHome = findViewById(R.id.btnHome);
        btnNews = findViewById(R.id.btnNews);
        btnSearch = findViewById(R.id.btnSearch);

        btnHome.setOnClickListener(v -> mostrarSnackBar("Você já está na Home!"));

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

        btnNews.setOnClickListener(v -> {
            startActivity(new Intent(this, GuiaActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        btnSearch.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificacoesActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void mostrarSnackBar(String mensagem) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(0xFF2B7A2B);
        snackbar.setTextColor(0xFFFFFFFF);
        snackbar.show();
    }
}
