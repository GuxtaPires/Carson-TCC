package com.example.carson_umaplicativoparadescartedemedicamentos.model;

public class Medicamento {

    private String id;         // Gerado automaticamente pelo Firebase
    private String userId;     // ID do usuário logado (para rastrear)
    private String nome;
    private String validade;
    private String situacao;   // Ex: "Em uso", "Vencido", etc.
    private String fotoUrl;    // Link da foto no Firebase Storage (opcional)

    // 🔹 Construtor vazio obrigatório para o Firebase
    public Medicamento() {}

    // 🔹 Construtor completo
    public Medicamento(String id, String userId, String nome, String validade, String situacao, String fotoUrl) {
        this.id = id;
        this.userId = userId;
        this.nome = nome;
        this.validade = validade;
        this.situacao = situacao;
        this.fotoUrl = fotoUrl;
    }

    // 🔹 Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getValidade() { return validade; }
    public void setValidade(String validade) { this.validade = validade; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}
