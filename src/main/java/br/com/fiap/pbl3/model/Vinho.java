package br.com.fiap.pbl3.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Vinho {
    private Integer idVinho;
    private Integer idCategoria;
    private String nome;
    private String descricao;
    private String tipo;
    private String regiao;
    private String pais;
    private Integer safra;
    private BigDecimal preco;
    private BigDecimal teorAlcoolico;
    private Integer volumeMl;
    private String harmonizacao;
    private String notaDegustacao;
    private Integer estoque;
    private String imagemUrl;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public Vinho() {
        this.ativo = true;
        this.volumeMl = 750;
        this.estoque = 0;
    }

    public Vinho(Integer idCategoria, String nome, String tipo, String regiao, String pais, Integer safra, BigDecimal preco) {
        this();
        this.idCategoria = idCategoria;
        this.nome = nome;
        this.tipo = tipo;
        this.regiao = regiao;
        this.pais = pais;
        this.safra = safra;
        this.preco = preco;
    }

    public Vinho(Integer idVinho, Integer idCategoria, String nome, String descricao, String tipo,
                 String regiao, String pais, Integer safra, BigDecimal preco, BigDecimal teorAlcoolico,
                 Integer volumeMl, String harmonizacao, String notaDegustacao, Integer estoque,
                 String imagemUrl, Boolean ativo, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.idVinho = idVinho;
        this.idCategoria = idCategoria;
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.regiao = regiao;
        this.pais = pais;
        this.safra = safra;
        this.preco = preco;
        this.teorAlcoolico = teorAlcoolico;
        this.volumeMl = volumeMl;
        this.harmonizacao = harmonizacao;
        this.notaDegustacao = notaDegustacao;
        this.estoque = estoque;
        this.imagemUrl = imagemUrl;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public Integer getIdVinho() { return idVinho; }
    public void setIdVinho(Integer idVinho) { this.idVinho = idVinho; }

    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getRegiao() { return regiao; }
    public void setRegiao(String regiao) { this.regiao = regiao; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Integer getSafra() { return safra; }
    public void setSafra(Integer safra) { this.safra = safra; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public BigDecimal getTeorAlcoolico() { return teorAlcoolico; }
    public void setTeorAlcoolico(BigDecimal teorAlcoolico) { this.teorAlcoolico = teorAlcoolico; }

    public Integer getVolumeMl() { return volumeMl; }
    public void setVolumeMl(Integer volumeMl) { this.volumeMl = volumeMl; }

    public String getHarmonizacao() { return harmonizacao; }
    public void setHarmonizacao(String harmonizacao) { this.harmonizacao = harmonizacao; }

    public String getNotaDegustacao() { return notaDegustacao; }
    public void setNotaDegustacao(String notaDegustacao) { this.notaDegustacao = notaDegustacao; }

    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

    @Override
    public String toString() {
        return "Vinho{" + "idVinho=" + idVinho + ", nome='" + nome + '\'' + ", preco=" + preco + '}';
    }
}
