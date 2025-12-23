package com.example.carson_umaplicativoparadescartedemedicamentos;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
// 👇 IMPORTS DO WORKMANAGER (IMPORTANTE)
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.carson_umaplicativoparadescartedemedicamentos.controller.MedicamentoController;
import com.example.carson_umaplicativoparadescartedemedicamentos.model.Medicamento;
import com.example.carson_umaplicativoparadescartedemedicamentos.workers.ValidadeWorker; // 👈 Certifique-se de importar seu Worker
import com.google.firebase.auth.FirebaseAuth;

public class Activity_cadastrar_medicamento extends AppCompatActivity {

    private EditText edtNome, edtValidade;
    private Spinner spinnerSituacao;
    private Button btnSalvar;
    private ImageButton btnVoltar;
    private MedicamentoController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_medicamento);

        // Vinculando elementos do layout
        edtNome = findViewById(R.id.edtNomeMedicamento);
        edtValidade = findViewById(R.id.edtValidade);
        spinnerSituacao = findViewById(R.id.spinnerSituacao);
        btnSalvar = findViewById(R.id.btnSalvarMedicamento);
        btnVoltar = findViewById(R.id.btnMenu);

        controller = new MedicamentoController(this);

        // ============================================================
        // 🟢 APLICANDO A MÁSCARA DE VALIDADE (MM/AAAA)
        // ============================================================
        edtValidade.addTextChangedListener(Mascara.insert(Mascara.FORMAT_VALIDADE, edtValidade));

        // 🌀 Configurar o spinner com as opções do XML
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.situacoes_medicamento,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSituacao.setAdapter(adapter);

        // 🔙 Botão de voltar
        btnVoltar.setOnClickListener(v -> finish());

        // 💾 Botão de salvar
        btnSalvar.setOnClickListener(v -> salvarMedicamento());
    }

    private void salvarMedicamento() {
        String nome = edtNome.getText().toString().trim();
        String validade = edtValidade.getText().toString().trim();
        String situacao = spinnerSituacao.getSelectedItem().toString();

        if (nome.isEmpty() || validade.isEmpty() || situacao.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (validade.length() < 7) {
            Toast.makeText(this, "Data de validade incompleta!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar objeto
        Medicamento medicamento = new Medicamento();
        medicamento.setNome(nome);
        medicamento.setValidade(validade);
        medicamento.setSituacao(situacao);

        // SALVA (userId será setado no controller)
        controller.cadastrarMedicamento(medicamento);

        // Força verificação
        OneTimeWorkRequest verificacaoImediata =
                new OneTimeWorkRequest.Builder(ValidadeWorker.class).build();
        WorkManager.getInstance(this).enqueue(verificacaoImediata);

        // NÃO MOSTRA SUCESSO AQUI
        // NÃO FECHA A TELA AQUI
    }

}
