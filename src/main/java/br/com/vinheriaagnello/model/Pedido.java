package br.com.vinheriaagnello.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private Integer idPedido;
    private Integer idUsuario;
    private Integer idEndereco;
    private String numeroPedido;
    private LocalDateTime dataPedido;
    private BigDecimal valorSubtotal;
    private BigDecimal valorDesconto;
    private BigDecimal valorFrete;
    private BigDecimal valorTotal;
    private String statusPedido;
    private LocalDateTime dataAtualizacao;
    private List<ItemPedido> itens = new ArrayList<>();

    public Pedido() {
        this.statusPedido = "pendente";
        this.valorDesconto = BigDecimal.ZERO;
        this.valorFrete = BigDecimal.ZERO;
    }

    public Pedido(Integer idUsuario, Integer idEndereco, String numeroPedido) {
        this();
        this.idUsuario = idUsuario;
        this.idEndereco = idEndereco;
        this.numeroPedido = numeroPedido;
        this.dataPedido = LocalDateTime.now();
    }

    public BigDecimal calcularTotal() {
        BigDecimal total = valorSubtotal
            .subtract(valorDesconto != null ? valorDesconto : BigDecimal.ZERO)
            .add(valorFrete != null ? valorFrete : BigDecimal.ZERO);
        setValorTotal(total);
        return total;
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
    }

    public Integer getIdPedido() { return idPedido; }
    public void setIdPedido(Integer idPedido) { this.idPedido = idPedido; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdEndereco() { return idEndereco; }
    public void setIdEndereco(Integer idEndereco) { this.idEndereco = idEndereco; }

    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }

    public LocalDateTime getDataPedido() { return dataPedido; }
    public void setDataPedido(LocalDateTime dataPedido) { this.dataPedido = dataPedido; }

    public BigDecimal getValorSubtotal() { return valorSubtotal; }
    public void setValorSubtotal(BigDecimal valorSubtotal) { this.valorSubtotal = valorSubtotal; }

    public BigDecimal getValorDesconto() { return valorDesconto; }
    public void setValorDesconto(BigDecimal valorDesconto) { this.valorDesconto = valorDesconto; }

    public BigDecimal getValorFrete() { return valorFrete; }
    public void setValorFrete(BigDecimal valorFrete) { this.valorFrete = valorFrete; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public String getStatusPedido() { return statusPedido; }
    public void setStatusPedido(String statusPedido) { this.statusPedido = statusPedido; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    public List<ItemPedido> getItens() { return itens; }
    public void setItens(List<ItemPedido> itens) { this.itens = itens; }

    @Override
    public String toString() {
        return "Pedido{" + "idPedido=" + idPedido + ", numeroPedido='" + numeroPedido + '\'' +
               ", valorTotal=" + valorTotal + ", status='" + statusPedido + '\'' + '}';
    }
}
