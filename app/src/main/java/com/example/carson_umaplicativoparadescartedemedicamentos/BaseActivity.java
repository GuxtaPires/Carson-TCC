package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.carson_umaplicativoparadescartedemedicamentos.utils.LocaleHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;

    // Variáveis de Tema/Persistência
    private static final String PREF_NAME = "CarsonPrefs";
    private static final String CHAVE_TEMA = "app_tema";

    // Variáveis de Leitura Assíncrona do Perfil
    private ValueEventListener perfilListener;
    private DatabaseReference perfilRef;

    // Variáveis de Leitura Assíncrona de Notificações (opcional)
    protected ValueEventListener notificacoesListener;
    protected DatabaseReference notificacoesRef;

    // ============================================================
    // 🟢 1. APLICAÇÃO GLOBAL DO TEMA E IDIOMA
    // ============================================================

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        aplicarTemaSalvo();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    private void aplicarTemaSalvo() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(CHAVE_TEMA, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // ============================================================
    // 🟢 2. INÍCIO E FIM DA LEITURA DO PERFIL
    // ============================================================

    @Override
    protected void onStart() {
        super.onStart();
        iniciarLeituraDePerfil();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pararLeituraDePerfil();
        removerListenerNotificacoes();
    }

    // ============================================================
    // ⚙️ CONFIGURAÇÃO DO DRAWER
    // ============================================================

    protected void setupDrawer(int layoutResID) {

        setContentView(R.layout.activity_base);
        FrameLayout container = findViewById(R.id.base_container);
        getLayoutInflater().inflate(layoutResID, container, true);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
            pintarItemDeVermelho(R.id.nav_logout);
        }

        ImageButton btnMenu = container.findViewById(R.id.btnMenu);
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }
    }

    protected void setDrawerMenu(int menuResId) {
        if (navigationView != null) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(menuResId);
            pintarItemDeVermelho(R.id.nav_logout);
        }
    }

    // ============================================================
    // 🖱️ NAVEGAÇÃO DO MENU
    // ============================================================

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_config) {
            startActivity(new Intent(this, Activity_settings.class));
        } else if (id == R.id.nav_change_password) {
            startActivity(new Intent(this, ActivityChangePassword.class));
        } else if (id == R.id.nav_logout) {

            // 🚨 Remove listeners ativos antes do logout
            pararLeituraDePerfil();
            removerListenerNotificacoes();

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, tela_de_login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    // ============================================================
    // 🎨 MÉTODOS VISUAIS E DE PERFIL
    // ============================================================

    private void iniciarLeituraDePerfil() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || navigationView == null) return;
        if (perfilRef != null && perfilListener != null) return;

        View headerView = navigationView.getHeaderView(0);
        TextView txtNome = headerView.findViewById(R.id.txtHeaderName);
        TextView txtEmail = headerView.findViewById(R.id.txtHeaderEmail);

        if (txtNome != null) txtNome.setText("Aguarde...");

        perfilRef = FirebaseDatabase.getInstance().getReference("pessoas").child(user.getUid());

        perfilListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nome = snapshot.child("nome").getValue(String.class);
                    if (txtNome != null && nome != null) txtNome.setText(nome);
                    if (txtEmail != null) txtEmail.setText(user.getEmail());
                } else {
                    if (txtNome != null) txtNome.setText("Usuário Carson");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("BaseActivity", "Falha ao ler perfil: " + error.getMessage());
                if (txtNome != null) txtNome.setText("Erro de Conexão");
            }
        };

        perfilRef.addValueEventListener(perfilListener);
    }

    private void pararLeituraDePerfil() {
        if (perfilRef != null && perfilListener != null) {
            perfilRef.removeEventListener(perfilListener);
            perfilRef = null;
            perfilListener = null;
        }
    }

    // ============================================================
    // 🛑 MÉTODOS DE NOTIFICAÇÕES
    // ============================================================

    protected void removerListenerNotificacoes() {
        if (notificacoesRef != null && notificacoesListener != null) {
            notificacoesRef.removeEventListener(notificacoesListener);
            notificacoesRef = null;
            notificacoesListener = null;
        }
    }

    // ============================================================
    // 🖌️ UTILS VISUAIS
    // ============================================================

    private void pintarItemDeVermelho(int itemId) {
        MenuItem item = navigationView.getMenu().findItem(itemId);
        if (item != null) {
            SpannableString s = new SpannableString(item.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.parseColor("#D32F2F")), 0, s.length(), 0);
            item.setTitle(s);
        }
    }
}
