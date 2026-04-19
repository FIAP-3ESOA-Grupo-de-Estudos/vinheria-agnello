package br.com.vinheriaagnello.model;

import java.math.BigDecimal;

public class PlanoAssinatura {
    private Integer idPlano;
    private String nome;
    private String descricao;
    private BigDecimal valorMensal;
    private Integer quantidadeGarrafas;
    private Boolean premiumOnly;
    private Boolean ativo;

    public PlanoAssinatura() {
        this.ativo = true;
        this.premiumOnly = false;
    }

    public PlanoAssinatura(String nome, BigDecimal valorMensal, Integer quantidadeGarrafas) {
        this();
        this.nome = nome;
        this.valorMensal = valorMensal;
        this.quantidadeGarrafas = quantidadeGarrafas;
    }

    public Integer getIdPlano() { return idPlano; }
    public void setIdPlano(Integer idPlano) { this.idPlano = idPlano; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValorMensal() { return valorMensal; }
    public void setValorMensal(BigDecimal valorMensal) { this.valorMensal = valorMensal; }

    public Integer getQuantidadeGarrafas() { return quantidadeGarrafas; }
    public void setQuantidadeGarrafas(Integer quantidadeGarrafas) { this.quantidadeGarrafas = quantidadeGarrafas; }

    public Boolean getPremiumOnly() { return premiumOnly; }
    public void setPremiumOnly(Boolean premiumOnly) { this.premiumOnly = premiumOnly; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return "PlanoAssinatura{" + "idPlano=" + idPlano + ", nome='" + nome + '\'' +
               ", valorMensal=" + valorMensal + '}';
    }
}
