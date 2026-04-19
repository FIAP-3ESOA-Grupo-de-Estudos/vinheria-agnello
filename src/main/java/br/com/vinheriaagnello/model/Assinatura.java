package br.com.vinheriaagnello.model;

import java.time.LocalDateTime;

public class Assinatura {
    private Integer idAssinatura;
    private Integer idUsuario;
    private Integer idPlano;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String statusAssinatura;
    private LocalDateTime dataProximaCobranca;
    private LocalDateTime dataCancelamento;
    private LocalDateTime dataAtualizacao;

    public Assinatura() {
        this.statusAssinatura = "ativa";
    }

    public Assinatura(Integer idUsuario, Integer idPlano) {
        this();
        this.idUsuario = idUsuario;
        this.idPlano = idPlano;
        this.dataInicio = LocalDateTime.now();
        this.dataProximaCobranca = dataInicio.plusMonths(1);
    }

    public Integer getIdAssinatura() { return idAssinatura; }
    public void setIdAssinatura(Integer idAssinatura) { this.idAssinatura = idAssinatura; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdPlano() { return idPlano; }
    public void setIdPlano(Integer idPlano) { this.idPlano = idPlano; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public String getStatusAssinatura() { return statusAssinatura; }
    public void setStatusAssinatura(String statusAssinatura) { this.statusAssinatura = statusAssinatura; }

    public LocalDateTime getDataProximaCobranca() { return dataProximaCobranca; }
    public void setDataProximaCobranca(LocalDateTime dataProximaCobranca) { this.dataProximaCobranca = dataProximaCobranca; }

    public LocalDateTime getDataCancelamento() { return dataCancelamento; }
    public void setDataCancelamento(LocalDateTime dataCancelamento) { this.dataCancelamento = dataCancelamento; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    @Override
    public String toString() {
        return "Assinatura{" + "idAssinatura=" + idAssinatura + ", idUsuario=" + idUsuario +
               ", status='" + statusAssinatura + '\'' + '}';
    }
}
