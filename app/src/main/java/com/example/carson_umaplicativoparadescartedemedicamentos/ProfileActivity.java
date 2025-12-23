package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends BaseActivity {

    private ImageButton btnBack;
    private Button btnUpdate;

    private EditText edtFullName, edtBirthDate, edtEmail, edtCpf,
            edtAddress, edtCep, edtNumber, edtCity, edtState,
            edtNeighborhood, edtComplement;

    private Spinner spinnerGender;

    private FirebaseAuth auth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.profile_activity); // Nome do arquivo XML

        // <<< ALTERAÇÃO REALIZADA AQUI >>>
        // Inicializa as Views logo após o layout ser definido.
        initViews();
        setupSpinner();

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String userId = auth.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("pessoas").child(userId);

        // A chamada 'initViews()' foi movida para cima.
        loadUserData();

        btnBack.setOnClickListener(v -> finish());

        btnUpdate.setOnClickListener(v -> {
            if (validateFields()) {
                saveUserData();
            }
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnUpdate = findViewById(R.id.btnUpdate);

        edtFullName = findViewById(R.id.edtFullName);
        edtBirthDate = findViewById(R.id.edtBirthDate);
        edtEmail = findViewById(R.id.edtEmail);
        spinnerGender = findViewById(R.id.spinnerGender);
        edtCpf = findViewById(R.id.edtCpf);
        edtAddress = findViewById(R.id.edtAddress);
        edtCep = findViewById(R.id.edtCep);
        edtNumber = findViewById(R.id.edtNumber);
        edtCity = findViewById(R.id.edtCity);
        edtState = findViewById(R.id.edtState);
        edtNeighborhood = findViewById(R.id.edtNeighborhood);
        edtComplement = findViewById(R.id.edtComplement);

        // ============================================================
        // 🟢 MÁSCARAS (RÓTULOS AUTOMÁTICOS)
        // ============================================================
        edtBirthDate.addTextChangedListener(Mascara.insert(Mascara.FORMAT_DATA, edtBirthDate));
        edtCpf.addTextChangedListener(Mascara.insert(Mascara.FORMAT_CPF, edtCpf));
        edtCep.addTextChangedListener(Mascara.insert(Mascara.FORMAT_CEP, edtCep));
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.generos_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    edtFullName.setText(snapshot.child("nome").getValue(String.class));
                    edtBirthDate.setText(snapshot.child("dataNascimento").getValue(String.class));
                    edtEmail.setText(snapshot.child("email").getValue(String.class));
                    edtCpf.setText(snapshot.child("cpf").getValue(String.class));

                    edtAddress.setText(snapshot.child("rua").getValue(String.class));
                    edtCep.setText(snapshot.child("cep").getValue(String.class));
                    edtNumber.setText(snapshot.child("numero").getValue(String.class));
                    edtCity.setText(snapshot.child("cidade").getValue(String.class));
                    edtState.setText(snapshot.child("estado").getValue(String.class));
                    edtNeighborhood.setText(snapshot.child("bairro").getValue(String.class));
                    edtComplement.setText(snapshot.child("complemento").getValue(String.class));

                    String generoSalvo = snapshot.child("genero").getValue(String.class);
                    if (generoSalvo != null) {
                        setSpinnerValue(spinnerGender, generoSalvo);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Perfil não encontrado.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Erro ao ler perfil: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerValue(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }

    private boolean validateFields() {
        if (edtFullName.getText().toString().trim().isEmpty()) {
            edtFullName.setError("Nome é obrigatório");
            return false;
        }
        if (edtBirthDate.getText().toString().length() < 10) {
            edtBirthDate.setError("Data inválida");
            return false;
        }
        if (edtCpf.getText().toString().length() < 14) {
            edtCpf.setError("CPF inválido");
            return false;
        }
        if (edtEmail.getText().toString().trim().isEmpty()) {
            edtEmail.setError("E-mail é obrigatório");
            return false;
        }
        return true;
    }

    private void saveUserData() {
        Map<String, Object> updates = new HashMap<>();

        updates.put("nome", edtFullName.getText().toString());
        updates.put("dataNascimento", edtBirthDate.getText().toString());
        updates.put("email", edtEmail.getText().toString()); // 🟢 Atualiza Email
        updates.put("cpf", edtCpf.getText().toString());     // 🟢 Atualiza CPF

        if (spinnerGender.getSelectedItem() != null) {
            updates.put("genero", spinnerGender.getSelectedItem().toString());
        }

        updates.put("rua", edtAddress.getText().toString());
        updates.put("cep", edtCep.getText().toString());
        updates.put("numero", edtNumber.getText().toString());
        updates.put("cidade", edtCity.getText().toString());
        updates.put("estado", edtState.getText().toString());
        updates.put("bairro", edtNeighborhood.getText().toString());
        updates.put("complemento", edtComplement.getText().toString());

        userRef.updateChildren(updates)
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Perfil atualizado com sucesso! ✔️", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao atualizar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
