package br.com.fiap.pbl3.model;

import java.time.LocalDateTime;

public class Usuario {
    private Integer idUsuario;
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private String telefone;
    private String tipoUsuario;
    private String status;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    public Usuario() {
        this.tipoUsuario = "cliente";
        this.status = "ativo";
    }

    public Usuario(String nome, String email, String senha) {
        this();
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public Usuario(Integer idUsuario, String nome, String email, String senha, String telefone, 
                   String tipoUsuario, String status, LocalDateTime dataCadastro, LocalDateTime dataAtualizacao) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.tipoUsuario = tipoUsuario;
        this.status = status;
        this.dataCadastro = dataCadastro;
        this.dataAtualizacao = dataAtualizacao;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    @Override
    public String toString() {
        return "Usuario{" + "idUsuario=" + idUsuario + ", nome='" + nome + '\'' + ", email='" + email + '\'' + '}';
    }
}
