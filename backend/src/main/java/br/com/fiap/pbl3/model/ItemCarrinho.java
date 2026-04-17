package br.com.fiap.pbl3.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ItemCarrinho {
    private Integer idItemCarrinho;
    private Integer idCarrinho;
    private Integer idVinho;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private LocalDateTime dataAdicao;

    public ItemCarrinho() {}

    public ItemCarrinho(Integer idCarrinho, Integer idVinho, Integer quantidade, BigDecimal precoUnitario) {
        this.idCarrinho = idCarrinho;
        this.idVinho = idVinho;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal calcularSubtotal() {
        return precoUnitario.multiply(new BigDecimal(quantidade));
    }

    public Integer getIdItemCarrinho() { return idItemCarrinho; }
    public void setIdItemCarrinho(Integer idItemCarrinho) { this.idItemCarrinho = idItemCarrinho; }

    public Integer getIdCarrinho() { return idCarrinho; }
    public void setIdCarrinho(Integer idCarrinho) { this.idCarrinho = idCarrinho; }

    public Integer getIdVinho() { return idVinho; }
    public void setIdVinho(Integer idVinho) { this.idVinho = idVinho; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public LocalDateTime getDataAdicao() { return dataAdicao; }
    public void setDataAdicao(LocalDateTime dataAdicao) { this.dataAdicao = dataAdicao; }

    @Override
    public String toString() {
        return "ItemCarrinho{" + "idItemCarrinho=" + idItemCarrinho + ", quantidade=" + quantidade + 
               ", subtotal=" + calcularSubtotal() + '}';
    }
}
