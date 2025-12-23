package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class activity_moderador_dashboard extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    // Firebase
    private DatabaseReference pontosRef;
    private DatabaseReference pessoasRef;
    private DatabaseReference estatisticasRef;

    private static final int REQUEST_LOCATION = 1;

    // UI
    BottomSheetBehavior bottomSheetBehavior;
    EditText etNome, etTipo, etCep, etRua, etNumero, etBairro, etCidade, etEstado, etComplemento;
    Button btnSalvarPonto;
    ImageButton btnSair;
    TextView tvUsuarios, tvDescarte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderador_dashboard);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        pontosRef = FirebaseDatabase.getInstance().getReference("pontosDescarte");
        pessoasRef = FirebaseDatabase.getInstance().getReference("pessoas");
        estatisticasRef = FirebaseDatabase.getInstance().getReference("estatisticas");

        inicializarComponentesUI();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapModerador);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        iniciarContadoresTempoReal();
    }

    private void inicializarComponentesUI() {

        bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottomSheet));
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Contadores
        tvUsuarios = findViewById(R.id.tvUsuarios);
        tvDescarte = findViewById(R.id.tvDescarte);

        // Campos
        etNome = findViewById(R.id.etNomePonto);
        etTipo = findViewById(R.id.etTipoPonto);
        etCep = findViewById(R.id.etCep);
        etRua = findViewById(R.id.etRua);
        etNumero = findViewById(R.id.etNumero);
        etBairro = findViewById(R.id.etBairro);
        etCidade = findViewById(R.id.etCidade);
        etEstado = findViewById(R.id.etEstado);
        etComplemento = findViewById(R.id.etComplemento);

        btnSalvarPonto = findViewById(R.id.btnSalvarPonto);
        btnSair = findViewById(R.id.btnSair);

        // Máscara de CEP
        etCep.addTextChangedListener(Mascara.insert(Mascara.FORMAT_CEP, etCep));

        // Preenchimento automático do endereço
        etCep.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(android.text.Editable s) {
                String cep = s.toString();
                if (cep.length() == 9) { // formato 00000-000
                    buscarEnderecoPorCep(cep);
                }
            }
        });

        findViewById(R.id.btnAddPonto).setOnClickListener(v ->
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

        btnSalvarPonto.setOnClickListener(v -> salvarPonto());

        btnSair.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(activity_moderador_dashboard.this, tela_de_login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    private void iniciarContadoresTempoReal() {

        Query queryUsuariosComuns =
                pessoasRef.orderByChild("tipoUsuario").equalTo("comum");

        queryUsuariosComuns.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvUsuarios.setText(String.valueOf(snapshot.getChildrenCount()));
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });


        estatisticasRef.child("totalDescartes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Long total = snapshot.getValue(Long.class);
                        tvDescarte.setText(total == null ? "0" : String.valueOf(total));
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        verificarPermissoes();
    }

    private void verificarPermissoes() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);

        } else {
            inicializarMapa();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            inicializarMapa();
        }
    }

    private void inicializarMapa() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            return;

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 14f));
                    }
                });

        carregarPontosNoMapa();
    }

    private void carregarPontosNoMapa() {

        pontosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                mMap.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {

                    Double lat = snap.child("latitude").getValue(Double.class);
                    Double lng = snap.child("longitude").getValue(Double.class);
                    String nome = snap.child("nome").getValue(String.class);

                    if (lat != null && lng != null) {
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, lng))
                                .title(nome)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                }
            }

            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private LatLng converterParaLatLng(String endereco) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> lista = geocoder.getFromLocationName(endereco, 1);
            if (lista != null && !lista.isEmpty()) {
                Address a = lista.get(0);
                return new LatLng(a.getLatitude(), a.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void salvarPonto() {

        String nome = etNome.getText().toString();
        String tipo = etTipo.getText().toString();
        String cep = etCep.getText().toString();
        String rua = etRua.getText().toString();
        String numero = etNumero.getText().toString();
        String bairro = etBairro.getText().toString();
        String cidade = etCidade.getText().toString();
        String estado = etEstado.getText().toString();
        String complemento = etComplemento.getText().toString();

        if (nome.isEmpty() || rua.isEmpty()) {
            Toast.makeText(this, "Preencha os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        String enderecoCompleto = rua + " " + numero + ", " + bairro + ", " + cidade + " - " + estado;

        LatLng pos = converterParaLatLng(enderecoCompleto);

        if (pos == null) {
            Toast.makeText(this, "Endereço não encontrado!", Toast.LENGTH_LONG).show();
            return;
        }

        String id = pontosRef.push().getKey();

        if (id != null) {

            pontosRef.child(id).child("id").setValue(id);
            pontosRef.child(id).child("nome").setValue(nome);
            pontosRef.child(id).child("tipo").setValue(tipo);
            pontosRef.child(id).child("cep").setValue(cep);
            pontosRef.child(id).child("rua").setValue(rua);
            pontosRef.child(id).child("numero").setValue(numero);
            pontosRef.child(id).child("bairro").setValue(bairro);
            pontosRef.child(id).child("cidade").setValue(cidade);
            pontosRef.child(id).child("estado").setValue(estado);
            pontosRef.child(id).child("complemento").setValue(complemento);

            // Latitude/Longitude
            pontosRef.child(id).child("latitude").setValue(pos.latitude);
            pontosRef.child(id).child("longitude").setValue(pos.longitude);

            Toast.makeText(this, "Ponto cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
        }

        limparCampos();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void limparCampos() {

        etNome.setText("");
        etTipo.setText("");
        etCep.setText("");
        etRua.setText("");
        etNumero.setText("");
        etBairro.setText("");
        etCidade.setText("");
        etEstado.setText("");
        etComplemento.setText("");
    }

    // --------------------- FUNÇÃO DE BUSCA DE CEP ---------------------
    private void buscarEnderecoPorCep(String cep) {
        if (cep == null || cep.length() != 9) return;

        String url = "https://viacep.com.br/ws/" + cep.replace("-", "") + "/json/";

        new Thread(() -> {
            try {
                java.net.URL u = new java.net.URL(url);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) u.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                java.io.InputStream is = conn.getInputStream();
                java.util.Scanner scanner = new java.util.Scanner(is);
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();
                is.close();
                conn.disconnect();

                String json = response.toString();
                JSONObject obj = new JSONObject(json);

                runOnUiThread(() -> {
                    try {
                        String rua = obj.optString("logradouro");
                        String bairro = obj.optString("bairro");
                        String cidade = obj.optString("localidade");
                        String estado = obj.optString("uf");

                        if (!rua.isEmpty()) etRua.setText(rua);
                        if (!bairro.isEmpty()) etBairro.setText(bairro);
                        if (!cidade.isEmpty()) etCidade.setText(cidade);
                        if (!estado.isEmpty()) etEstado.setText(estado);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
