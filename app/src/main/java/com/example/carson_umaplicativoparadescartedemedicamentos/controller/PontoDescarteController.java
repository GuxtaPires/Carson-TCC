package com.example.carson_umaplicativoparadescartedemedicamentos.controller;


import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.carson_umaplicativoparadescartedemedicamentos.model.PontoDescarte;

public class PontoDescarteController {

    private DatabaseReference referencia;

    public PontoDescarteController() {
        // Instancia a referência principal do Firebase
        referencia = FirebaseDatabase.getInstance().getReference("pontosDescarte");
    }

    // 🔹 CREATE - Cadastrar um novo ponto de descarte
    public void cadastrarPonto(PontoDescarte ponto) {
        if (ponto.getId() == null || ponto.getId().isEmpty()) {
            String idGerado = referencia.push().getKey(); // gera um ID único
            ponto.setId(idGerado);
        }
        referencia.child(ponto.getId()).setValue(ponto);
    }

    // 🔹 READ - Ler todos os pontos de descarte
    public DatabaseReference listarPontos() {
        return referencia; // retorna a referência para ser observada via listener no Activity
    }

    // 🔹 UPDATE - Atualizar dados de um ponto existente
    public void atualizarPonto(PontoDescarte pontoAtualizado) {
        if (pontoAtualizado.getId() != null) {
            referencia.child(pontoAtualizado.getId()).setValue(pontoAtualizado);
        }
    }

    // 🔹 DELETE - Excluir um ponto de descarte
    public void excluirPonto(@NonNull String idPonto) {
        referencia.child(idPonto).removeValue();
    }

    // 🔹 GET POR ID - Buscar um ponto específico
    public DatabaseReference buscarPontoPorId(String idPonto) {
        return referencia.child(idPonto);
    }
}
