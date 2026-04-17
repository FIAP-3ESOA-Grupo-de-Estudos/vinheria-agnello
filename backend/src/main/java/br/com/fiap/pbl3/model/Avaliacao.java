package br.com.fiap.pbl3.model;

import java.time.LocalDateTime;

public class Avaliacao {
    private Integer idAvaliacao;
    private Integer idUsuario;
    private Integer idVinho;
    private Integer nota;
    private String comentario;
    private String statusAprovacao;
    private LocalDateTime dataAvaliacao;
    private LocalDateTime dataAprovacao;

    public Avaliacao() {
        this.statusAprovacao = "pendente";
    }

    public Avaliacao(Integer idUsuario, Integer idVinho, Integer nota) {
        this();
        this.idUsuario = idUsuario;
        this.idVinho = idVinho;
        this.nota = nota;
    }

    public Integer getIdAvaliacao() { return idAvaliacao; }
    public void setIdAvaliacao(Integer idAvaliacao) { this.idAvaliacao = idAvaliacao; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdVinho() { return idVinho; }
    public void setIdVinho(Integer idVinho) { this.idVinho = idVinho; }

    public Integer getNota() { return nota; }
    public void setNota(Integer nota) { this.nota = nota; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public String getStatusAprovacao() { return statusAprovacao; }
    public void setStatusAprovacao(String statusAprovacao) { this.statusAprovacao = statusAprovacao; }

    public LocalDateTime getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDateTime dataAvaliacao) { this.dataAvaliacao = dataAvaliacao; }

    public LocalDateTime getDataAprovacao() { return dataAprovacao; }
    public void setDataAprovacao(LocalDateTime dataAprovacao) { this.dataAprovacao = dataAprovacao; }

    @Override
    public String toString() {
        return "Avaliacao{" + "idAvaliacao=" + idAvaliacao + ", nota=" + nota + 
               ", status='" + statusAprovacao + '\'' + '}';
    }
}
