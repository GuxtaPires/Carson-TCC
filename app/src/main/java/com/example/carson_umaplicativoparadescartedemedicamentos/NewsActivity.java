package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class NewsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_news); // layout sem Drawer
        // Inicializa RecyclerView, adapter e API de notícias aqui
    }
}


