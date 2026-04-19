package br.com.vinheriaagnello.model;

import java.time.LocalDateTime;

public class Carrinho {
    private Integer idCarrinho;
    private Integer idUsuario;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Carrinho() {
        this.status = "ativo";
    }

    public Carrinho(Integer idUsuario) {
        this();
        this.idUsuario = idUsuario;
    }

    public Carrinho(Integer idCarrinho, Integer idUsuario, String status,
                    LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.idCarrinho = idCarrinho;
        this.idUsuario = idUsuario;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public Integer getIdCarrinho() { return idCarrinho; }
    public void setIdCarrinho(Integer idCarrinho) { this.idCarrinho = idCarrinho; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    @Override
    public String toString() {
        return "Carrinho{" + "idCarrinho=" + idCarrinho + ", idUsuario=" + idUsuario + ", status='" + status + '\'' + '}';
    }
}
