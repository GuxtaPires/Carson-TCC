package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Cadastro_dados_pessoais extends AppCompatActivity {

    EditText etNome, etData, etEmail, etCpf, etCep, etEndereco, etNumero, etCidade,
            etUf, etBairro, etComplemento, etSenha, etConfirmSenha;
    Spinner spinnerGenero;
    Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_dados_pessoais);

        // Vinculando os campos do XML
        etNome = findViewById(R.id.etNome);
        etData = findViewById(R.id.etData);
        etEmail = findViewById(R.id.etEmail);
        etCpf = findViewById(R.id.etCpf);
        etCep = findViewById(R.id.etCep);
        etEndereco = findViewById(R.id.etEndereco);
        etNumero = findViewById(R.id.etNumero);
        etCidade = findViewById(R.id.etCidade);
        etUf = findViewById(R.id.etUf);
        etBairro = findViewById(R.id.etBairro);
        etComplemento = findViewById(R.id.etComplemento);
        etSenha = findViewById(R.id.etSenha);
        etConfirmSenha = findViewById(R.id.etConfirmSenha);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        // Ação do botão de cadastro
        btnCadastrar.setOnClickListener(v -> {
            String nome = etNome.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String senha = etSenha.getText().toString();
            String confirmSenha = etConfirmSenha.getText().toString();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmSenha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!senha.equals(confirmSenha)) {
                Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Aqui você pode salvar os dados (banco de dados, Firebase, etc.)
            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

            // Retorna pra tela de login após cadastrar
            Intent intent = new Intent(this, tela_de_login.class);
            startActivity(intent);
            finish(); // Fecha a tela de cadastro
        });
    }
}
