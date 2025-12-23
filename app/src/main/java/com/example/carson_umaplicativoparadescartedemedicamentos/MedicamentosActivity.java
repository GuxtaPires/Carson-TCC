package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carson_umaplicativoparadescartedemedicamentos.controller.MedicamentoController;
import com.example.carson_umaplicativoparadescartedemedicamentos.model.Medicamento;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MedicamentosActivity extends BaseActivity {

    private ImageButton btnAdd;
    private EditText edtSearch;
    private RecyclerView recyclerMedicamentos;

    // Bottom Nav
    private ImageButton btnLoc, btnMed, btnHome, btnNews, btnSearch;

    private MedicamentoController controller;
    private MedicamentoAdapter adapter;
    private List<Medicamento> listaCompleta = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 AQUI: insere o layout dentro do drawer geral
        setupDrawer(R.layout.activity_listar_medicamentos);

        // ======== Ligações de layout ========
        btnAdd = findViewById(R.id.btnAdd);
        edtSearch = findViewById(R.id.edtSearch);
        recyclerMedicamentos = findViewById(R.id.recyclerMedicamentos);

        // Drawer já vem configurado pela BaseActivity com btnMenu + navView 😉

        // ======== Firebase e controller ========
        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception ignore) {}

        controller = new MedicamentoController(this);

        // ======== Configuração do RecyclerView ========
        recyclerMedicamentos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicamentoAdapter(listaCompleta, this, controller);
        recyclerMedicamentos.setAdapter(adapter);

        // ======== Botão adicionar medicamento ========
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, Activity_cadastrar_medicamento.class);
            startActivity(intent);
        });

        // ======== Filtro de busca ========
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarMedicamentos(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        configurarBottomNav();
    }

    // Atualiza a lista sempre que a tela volta
    @Override
    protected void onResume() {
        super.onResume();
        carregarMedicamentos();
    }

    // ======== Configuração dos botões inferiores ========
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
            mostrarSnackBar("Você já está aqui!");
        }); // já está na tela

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

        btnSearch.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificacoesActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    // ======== Carrega medicamentos do Firebase (somente usuário logado) ========
    private void carregarMedicamentos() {
        controller.listarMedicamentos(lista -> {

            String uidAtual = FirebaseAuth.getInstance().getUid();

            listaCompleta.clear();

            for (Medicamento m : lista) {
                if (m.getUserId() != null && m.getUserId().equals(uidAtual)) {
                    listaCompleta.add(m);
                }
            }

            adapter.atualizarLista(listaCompleta);
        });
    }

    // ======== Filtro de busca ========
    private void filtrarMedicamentos(String termo) {
        List<Medicamento> filtrada = new ArrayList<>();

        for (Medicamento m : listaCompleta) {
            if (m.getNome().toLowerCase().contains(termo.toLowerCase())) {
                filtrada.add(m);
            }
        }

        adapter.atualizarLista(filtrada);
    }
    private void mostrarSnackBar(String mensagem) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(0xFF2B7A2B); // verde do Carson
        snackbar.setTextColor(0xFFFFFFFF);
        snackbar.show();
    }
}
