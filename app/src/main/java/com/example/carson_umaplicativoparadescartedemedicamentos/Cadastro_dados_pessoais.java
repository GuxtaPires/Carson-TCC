package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carson_umaplicativoparadescartedemedicamentos.controller.PessoasController;
import com.example.carson_umaplicativoparadescartedemedicamentos.model.Pessoa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Cadastro_dados_pessoais extends AppCompatActivity {

    EditText etNome, etData, etEmail, etCpf, etCep, etEndereco, etNumero, etCidade,
            etUf, etBairro, etComplemento, etSenha, etConfirmSenha;
    Spinner spinnerGenero;
    Button btnCadastrar;
    ImageButton btnVoltar;

    PessoasController pessoasController;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_dados_pessoais);

        auth = FirebaseAuth.getInstance();
        pessoasController = new PessoasController(this);

        // Inicialização dos campos
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
        btnVoltar = findViewById(R.id.btnVoltar);

        // Máscaras
        etData.addTextChangedListener(Mascara.insert(Mascara.FORMAT_DATA, etData));
        etCpf.addTextChangedListener(Mascara.insert(Mascara.FORMAT_CPF, etCpf));
        etCep.addTextChangedListener(Mascara.insert(Mascara.FORMAT_CEP, etCep));

        // =============================================================
        // 🔵 AUTO COMPLETAR ENDEREÇO PELO CEP
        // =============================================================
        etCep.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String cep = s.toString().replace("-", "");

                if (cep.length() == 8) {
                    buscarCep(cep);
                }
            }
        });

        // Botão voltar
        btnVoltar.setOnClickListener(v -> {
            startActivity(new Intent(this, tela_de_login.class));
            finish();
        });

        // Botão cadastrar
        btnCadastrar.setOnClickListener(v -> cadastrarUsuario());
    }

    // =============================================================
    // 🔵 MÉTODO PARA BUSCAR CEP NO VIACEP
    // =============================================================
    private void buscarCep(String cep) {

        new Thread(() -> {
            try {
                URL url = new URL("https://viacep.com.br/ws/" + cep + "/json/");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "UTF-8")
                );

                StringBuilder json = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    json.append(line);
                }

                br.close();
                connection.disconnect();

                JSONObject obj = new JSONObject(json.toString());

                if (obj.has("erro")) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "CEP não encontrado!", Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                String rua = obj.optString("logradouro", "");
                String bairro = obj.optString("bairro", "");
                String cidade = obj.optString("localidade", "");
                String uf = obj.optString("uf", "");

                runOnUiThread(() -> {
                    etEndereco.setText(rua);
                    etBairro.setText(bairro);
                    etCidade.setText(cidade);
                    etUf.setText(uf);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Erro ao consultar CEP!", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }
    private String gerarHashSHA256(String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(senha.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b)); // converte byte pra hex
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return senha; // fallback se algo der errado
        }
    }
    // =============================================================
    // 🔵 CADASTRAR USUÁRIO
    // =============================================================
    private void cadastrarUsuario() {
        String nome = etNome.getText().toString().trim();
        String dataNascimento = etData.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String genero = spinnerGenero.getSelectedItem() != null ?
                spinnerGenero.getSelectedItem().toString() : "";
        String cpf = etCpf.getText().toString().trim();
        String cep = etCep.getText().toString().trim();
        String endereco = etEndereco.getText().toString().trim();
        String numero = etNumero.getText().toString().trim();
        String cidade = etCidade.getText().toString().trim();
        String uf = etUf.getText().toString().trim();
        String bairro = etBairro.getText().toString().trim();
        String complemento = etComplemento.getText().toString().trim();
        String senha = etSenha.getText().toString();
        String confirmSenha = etConfirmSenha.getText().toString();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!senha.equals(confirmSenha)) {
            Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dataNascimento.length() < 10) {
            Toast.makeText(this, "Data de nascimento inválida!", Toast.LENGTH_SHORT).show();
            return;
        }

        Pessoa pessoa = new Pessoa(
                nome, dataNascimento, email, genero, cpf, cep, endereco, numero,
                cidade, uf, bairro, complemento, null, null, "comum",
                null, null
        );

        auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();

                            pessoa.setDataCadastro(
                                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date())
                            );

                            pessoasController.salvarPessoaNoDatabase(userId, pessoa, () -> {
                                Toast.makeText(this, "Cadastro realizado com sucesso! 🎉", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, tela_de_login.class));
                                finish();
                            });
                        }
                    } else {
                        String erro = task.getException() != null ? task.getException().getMessage() : "Erro no cadastro.";
                        Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
                    }
                });


    }
}
