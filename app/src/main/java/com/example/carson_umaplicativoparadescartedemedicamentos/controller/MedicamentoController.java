package com.example.carson_umaplicativoparadescartedemedicamentos.controller;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.carson_umaplicativoparadescartedemedicamentos.model.Medicamento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MedicamentoController {

    private final DatabaseReference databaseReference;
    private final Context context;

    public MedicamentoController(Context context) {
        this.context = context;
        databaseReference = FirebaseDatabase.getInstance().getReference("medicamentos");
    }

    // 🟢 CREATE
    public void cadastrarMedicamento(Medicamento medicamento) {

        if (!validarCampos(medicamento)) return;

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) {
            Toast.makeText(context, "Usuário não autenticado!", Toast.LENGTH_SHORT).show();
            return;
        }

        medicamento.setUserId(uid);

        String id = databaseReference.child(uid).push().getKey();
        medicamento.setId(id);

        databaseReference.child(uid).child(id)
                .setValue(medicamento)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Medicamento cadastrado com sucesso!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Erro ao cadastrar medicamento: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }



    // 🔵 READ (listar todos os medicamentos)
    public void listarMedicamentos(FirebaseCallback callback) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Medicamento> lista = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Medicamento medicamento = item.getValue(Medicamento.class);
                    lista.add(medicamento);
                }
                callback.onCallback(lista);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    // 🟡 UPDATE
    public void atualizarMedicamento(Medicamento medicamento) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null || medicamento.getId() == null) return;

        databaseReference.child(uid).child(medicamento.getId()).setValue(medicamento)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Medicamento atualizado com sucesso!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Erro ao atualizar medicamento: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }


    // 🔴 DELETE
    public void excluirMedicamento(String userId, String medicamentoId) {
        if (userId == null || medicamentoId == null) {
            Toast.makeText(context, "Erro ao excluir: parâmetros nulos", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("medicamentos")
                .child(userId)
                .child(medicamentoId);

        ref.removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Medicamento excluído!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Erro ao excluir: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }




    // ⚙️ Validação básica dos campos obrigatórios
    private boolean validarCampos(Medicamento med) {
        if (med.getNome() == null || med.getNome().isEmpty() ||
                med.getValidade() == null || med.getValidade().isEmpty() ||
                med.getSituacao() == null || med.getSituacao().isEmpty()) {

            Toast.makeText(context, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // 🧠 Interface para callback do Firebase (usada na leitura)
    public interface FirebaseCallback {
        void onCallback(List<Medicamento> listaMedicamentos);
    }
}
