package br.com.fiap.pbl3.model;

import java.math.BigDecimal;

public class ItemPedido {
    private Integer idItemPedido;
    private Integer idPedido;
    private Integer idVinho;
    private Integer quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal subtotal;

    public ItemPedido() {}

    public ItemPedido(Integer idPedido, Integer idVinho, Integer quantidade, BigDecimal precoUnitario) {
        this.idPedido = idPedido;
        this.idVinho = idVinho;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.subtotal = precoUnitario.multiply(new BigDecimal(quantidade));
    }

    public Integer getIdItemPedido() { return idItemPedido; }
    public void setIdItemPedido(Integer idItemPedido) { this.idItemPedido = idItemPedido; }

    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }

    public Integer getIdVinho() { return idVinho; }
    public void setIdVinho(Integer idVinho) { this.idVinho = idVinho; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    @Override
    public String toString() {
        return "ItemPedido{" + "idItemPedido=" + idItemPedido + ", quantidade=" + quantidade + 
               ", subtotal=" + subtotal + '}';
    }
}
