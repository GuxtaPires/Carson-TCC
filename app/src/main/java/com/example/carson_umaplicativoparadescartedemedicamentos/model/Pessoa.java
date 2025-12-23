package com.example.carson_umaplicativoparadescartedemedicamentos.model;

public class Pessoa {
    private String nome;
    private String dataNascimento;
    private String email;
    private String genero;
    private String cpf;
    private String cep;
    private String endereco;
    private String numero;
    private String cidade;
    private String uf;
    private String bairro;
    private String complemento;
    private String senha;
    private String dataCadastro;
    private String tipoUsuario; // Ex: "comum", "moderador"
    private Double latitude;
    private Double longitude;

    // Construtor vazio (necessário pro Firebase)
    public Pessoa() {}

    // Construtor completo
    public Pessoa(String nome, String dataNascimento, String email, String genero,
                  String cpf, String cep, String endereco, String numero,
                  String cidade, String uf, String bairro, String complemento,
                  String senha, String dataCadastro, String tipoUsuario,
                  Double latitude, Double longitude) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.genero = genero;
        this.cpf = cpf;
        this.cep = cep;
        this.endereco = endereco;
        this.numero = numero;
        this.cidade = cidade;
        this.uf = uf;
        this.bairro = bairro;
        this.complemento = complemento;
        this.senha = senha;
        this.dataCadastro = dataCadastro;
        this.tipoUsuario = tipoUsuario;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters e setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(String dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
