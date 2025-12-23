package com.example.carson_umaplicativoparadescartedemedicamentos.controller;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.carson_umaplicativoparadescartedemedicamentos.model.Pessoa;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PessoasController {

    private final DatabaseReference databaseReference;
    private final Context context;

    // 🔧 Construtor: inicializa a referência do Firebase
    public PessoasController(Context context) {
        this.context = context;
        // Caminho do banco -> nó principal "pessoas"
        databaseReference = FirebaseDatabase.getInstance().getReference("pessoas");
    }

    /**
     * 🚀 Salva uma pessoa no Firebase Realtime Database
     * @param userId UID do usuário autenticado
     * @param pessoa Objeto Pessoa a ser salvo
     * @param onSuccess Callback executado quando o salvamento for bem-sucedido
     */
    public void salvarPessoaNoDatabase(String userId, Pessoa pessoa, Runnable onSuccess) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(context, "Erro: ID do usuário inválido.", Toast.LENGTH_LONG).show();
            return;
        }

        databaseReference.child(userId).setValue(pessoa)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Dados pessoais salvos com sucesso!", Toast.LENGTH_SHORT).show();
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Erro ao salvar dados: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    /**
     * ✏️ Atualiza dados de uma pessoa já cadastrada
     */
    public void atualizarPessoa(@NonNull String userId, @NonNull Pessoa pessoa) {
        databaseReference.child(userId).setValue(pessoa)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Erro ao atualizar dados: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    /**
     * ❌ Remove uma pessoa do banco (caso precise futuramente)
     */
    public void deletarPessoa(@NonNull String userId) {
        databaseReference.child(userId).removeValue()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(context, "Usuário removido com sucesso!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Erro ao remover usuário: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
