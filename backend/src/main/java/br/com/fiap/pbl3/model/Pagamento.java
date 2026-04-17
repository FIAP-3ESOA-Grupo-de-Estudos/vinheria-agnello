package br.com.fiap.pbl3.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pagamento {
    private Integer idPagamento;
    private Integer idPedido;
    private String tipoPagamento;
    private String statusPagamento;
    private BigDecimal valor;
    private Integer parcelas;
    private String codigoTransacao;
    private LocalDateTime dataPagamento;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Pagamento() {
        this.statusPagamento = "pendente";
        this.parcelas = 1;
    }

    public Pagamento(Integer idPedido, String tipoPagamento, BigDecimal valor) {
        this();
        this.idPedido = idPedido;
        this.tipoPagamento = tipoPagamento;
        this.valor = valor;
    }

    public Integer getIdPagamento() { return idPagamento; }
    public void setIdPagamento(Integer idPagamento) { this.idPagamento = idPagamento; }

    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }

    public String getTipoPagamento() { return tipoPagamento; }
    public void setTipoPagamento(String tipoPagamento) { this.tipoPagamento = tipoPagamento; }

    public String getStatusPagamento() { return statusPagamento; }
    public void setStatusPagamento(String statusPagamento) { this.statusPagamento = statusPagamento; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public Integer getParcelas() { return parcelas; }
    public void setParcelas(Integer parcelas) { this.parcelas = parcelas; }

    public String getCodigoTransacao() { return codigoTransacao; }
    public void setCodigoTransacao(String codigoTransacao) { this.codigoTransacao = codigoTransacao; }

    public LocalDateTime getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDateTime dataPagamento) { this.dataPagamento = dataPagamento; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    @Override
    public String toString() {
        return "Pagamento{" + "idPagamento=" + idPagamento + ", idPedido=" + idPedido + 
               ", valor=" + valor + ", status='" + statusPagamento + '\'' + '}';
    }
}
