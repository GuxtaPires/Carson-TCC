package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityChangePassword extends BaseActivity {

    private EditText edtCurrentPass, edtNewPass, edtConfirmNewPass;
    private Button btnChangePassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDrawer(R.layout.activity_change_password);

        auth = FirebaseAuth.getInstance();

        edtCurrentPass = findViewById(R.id.edtCurrentPassword);
        edtNewPass = findViewById(R.id.edtNewPassword);
        edtConfirmNewPass = findViewById(R.id.edtConfirmNewPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {
        String current = edtCurrentPass.getText().toString();
        String newPass = edtNewPass.getText().toString();
        String confirm = edtConfirmNewPass.getText().toString();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirm)) {
            Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "A senha precisa ter pelo menos 6 caracteres!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth.getCurrentUser() == null) return;

        String email = auth.getCurrentUser().getEmail();
        if (email == null) return;

        AuthCredential credential = EmailAuthProvider.getCredential(email, current);

        auth.getCurrentUser().reauthenticate(credential).addOnSuccessListener(aVoid -> {
            auth.getCurrentUser().updatePassword(newPass).addOnSuccessListener(v -> {
                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_LONG).show();
                finish();
            }).addOnFailureListener(e ->
                    Toast.makeText(this, "Erro ao atualizar senha: " + e.getMessage(), Toast.LENGTH_LONG).show()
            );
        }).addOnFailureListener(e ->
                Toast.makeText(this, "Senha atual incorreta!", Toast.LENGTH_SHORT).show()
        );
    }
}
