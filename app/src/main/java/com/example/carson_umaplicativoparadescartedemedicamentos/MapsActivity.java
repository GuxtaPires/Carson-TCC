package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.carson_umaplicativoparadescartedemedicamentos.model.PontoDescarte;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends BaseActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    // Componentes da UI
    private ImageButton btnAdd;
    private TextView txtSelectedAddress;
    private TextView lblSelectedAddress;

    // Menu inferior
    private ImageButton btnLocal, btnMedicamentos, btnHome, btnNoticias, btnBuscar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configura o Drawer (Menu Lateral) herdado de BaseActivity
        // Verifique se o nome do layout XML aqui está exato (R.layout.activity_mapa ou R.layout.acticity_mapa como estava antes)
        setupDrawer(R.layout.acticity_mapa);

        // Inicializa o cliente de localização (GPS)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Inicializa componentes da tela
        inicializarComponentes();

        // Inicializa o Mapa
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void inicializarComponentes() {
        // Campos de texto do endereço
        txtSelectedAddress = findViewById(R.id.txtSelectedAddress);
        lblSelectedAddress = findViewById(R.id.lblSelectedAddress);

        // Menu inferior
        setupBottomMenu();
    }

    // =====================================================================
    // MAPA E LOCALIZAÇÃO
    // =====================================================================
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Configura o clique no marcador (Pino)
        mMap.setOnMarkerClickListener(marker -> {
            String endereco = marker.getSnippet(); // Pegamos o endereço salvo no snippet
            String nomeLocal = marker.getTitle();

            if (endereco != null) {
                // Atualiza o TextView do layout XML com o endereço do ponto clicado
                txtSelectedAddress.setText(endereco);
                lblSelectedAddress.setText("Local selecionado: " + nomeLocal);
            }
            return false; // Retorna false para manter o comportamento padrão (abrir info window e centralizar)
        });

        // Verifica permissões de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Ativa o botão nativo de "Minha Localização" (bolinha azul)
            mMap.setMyLocationEnabled(true);

            // Busca a posição atual para centralizar e filtrar os pontos
            pegarLocalizacaoUsuario();

        } else {
            // Solicita permissão se não tiver
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void pegarLocalizacaoUsuario() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // 1. Move a câmera para onde o usuário está
                        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 14)); // Zoom 14 = nível de bairro

                        // 2. Busca os pontos no Firebase e aplica o filtro de 5km
                        carregarPontosProximos(location);
                    } else {
                        mostrarSnackBar("Não foi possível obter sua localização exata. Verifique o GPS.");
                        // Fallback: Carrega pontos sem filtro ou em uma posição padrão se quiser
                    }
                });
    }

    // =====================================================================
    // FIREBASE E LÓGICA DE DISTÂNCIA
    // =====================================================================
    private void carregarPontosProximos(Location userLocation) {
        DatabaseReference pontosRef = FirebaseDatabase.getInstance().getReference("pontosDescarte");

        pontosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                mMap.clear(); // Limpa marcadores antigos para não duplicar

                for (DataSnapshot data : snapshot.getChildren()) {
                    try {
                        // Converte o JSON do Firebase para o objeto Java usando seu Model
                        PontoDescarte ponto = data.getValue(PontoDescarte.class);

                        if (ponto != null) {
                            // Cria um objeto Location para o ponto do banco
                            Location pontoLocation = new Location("provider");
                            pontoLocation.setLatitude(ponto.getLatitude()); // Usa o Getter do Model
                            pontoLocation.setLongitude(ponto.getLongitude()); // Usa o Getter do Model

                            // CALCULA A DISTÂNCIA (em metros) ENTRE O USUÁRIO E O PONTO
                            float distanciaEmMetros = userLocation.distanceTo(pontoLocation);

                            // Se for menor ou igual a 5km (5000 metros)
                            if (distanciaEmMetros <= 5000) {
                                LatLng posPonto = new LatLng(ponto.getLatitude(), ponto.getLongitude());

                                // Monta o endereço formatado usando os Getters
                                String enderecoFormatado = ponto.getRua() + ", " + ponto.getNumero() + " - " + ponto.getBairro();

                                mMap.addMarker(new MarkerOptions()
                                        .position(posPonto)
                                        .title(ponto.getNome())
                                        .snippet(enderecoFormatado) // Guarda o endereço no snippet para usar no clique
                                        // Usa ícone verde (HUE_GREEN) para combinar com o app
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            }
                        }
                    } catch (Exception e) {
                        Log.e("MapsActivity", "Erro ao converter ponto: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mostrarSnackBar("Erro ao carregar pontos: " + error.getMessage());
            }
        });
    }

    // =====================================================================
    // PERMISSÕES
    // =====================================================================
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 44 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permissão concedida, recarrega o mapa
            onMapReady(mMap);
        } else {
            mostrarSnackBar("Precisamos da sua localização para mostrar pontos próximos 😕");
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // =====================================================================
    // BOTTOM NAV E UTILITÁRIOS
    // =====================================================================
    private void setupBottomMenu() {
        btnLocal = findViewById(R.id.btnLoc);
        btnMedicamentos = findViewById(R.id.btnMed);
        btnHome = findViewById(R.id.btnHome);
        btnNoticias = findViewById(R.id.btnNews);
        btnBuscar = findViewById(R.id.btnSearch);

        btnLocal.setOnClickListener(v -> mostrarSnackBar("Você já está aqui!"));

        btnMedicamentos.setOnClickListener(v -> {
            startActivity(new Intent(this, MedicamentosActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        btnHome.setOnClickListener(v -> {
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        btnNoticias.setOnClickListener(v -> {
            startActivity(new Intent(this, GuiaActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });

        btnBuscar.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificacoesActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    private void mostrarSnackBar(String mensagem) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensagem, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(0xFF2B7A2B); // Verde do Carson
        snackbar.setTextColor(0xFFFFFFFF);
        snackbar.show();
    }
}