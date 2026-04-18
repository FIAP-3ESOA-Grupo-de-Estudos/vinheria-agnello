package br.com.fiap.pbl3.model;

public class Categoria {
    private Integer idCategoria;
    private String nome;
    private String descricao;

    public Categoria() {}

    public Categoria(String nome) {
        this.nome = nome;
    }

    public Categoria(Integer idCategoria, String nome, String descricao) {
        this.idCategoria = idCategoria;
        this.nome = nome;
        this.descricao = descricao;
    }

    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() {
        return "Categoria{" + "idCategoria=" + idCategoria + ", nome='" + nome + '\'' + '}';
    }
}
