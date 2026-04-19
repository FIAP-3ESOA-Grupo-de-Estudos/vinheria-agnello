package br.com.vinheriaagnello.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.com.vinheriaagnello.config.DatabaseConfig;
import br.com.vinheriaagnello.model.Vinho;

public class VinhoDAO implements DAO<Vinho, Integer> {

    @Override
    public void inserir(Vinho vinho) throws Exception {
        String sql = "INSERT INTO vinho (id_categoria, nome, descricao, tipo, regiao, pais, safra, " +
                     "preco, teor_alcoolico, volume_ml, harmonizacao, nota_degustacao, estoque, imagem_url, ativo) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, vinho.getIdCategoria());
            stmt.setString(2, vinho.getNome());
            stmt.setString(3, vinho.getDescricao());
            stmt.setString(4, vinho.getTipo());
            stmt.setString(5, vinho.getRegiao());
            stmt.setString(6, vinho.getPais());
            stmt.setInt(7, vinho.getSafra());
            stmt.setBigDecimal(8, vinho.getPreco());
            stmt.setBigDecimal(9, vinho.getTeorAlcoolico());
            stmt.setInt(10, vinho.getVolumeMl());
            stmt.setString(11, vinho.getHarmonizacao());
            stmt.setString(12, vinho.getNotaDegustacao());
            stmt.setInt(13, vinho.getEstoque());
            stmt.setString(14, vinho.getImagemUrl());
            stmt.setBoolean(15, vinho.getAtivo());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    vinho.setIdVinho(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Vinho buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM vinho WHERE id_vinho = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearVinho(rs);
            }
        }
        return null;
    }

    @Override
    public List<Vinho> listarTodos() throws Exception {
        return listarAtivos();
    }

    public List<Vinho> listarAtivos() throws Exception {
        String sql = "SELECT * FROM vinho WHERE ativo = true ORDER BY nome";
        List<Vinho> vinhos = new ArrayList<>();

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vinhos.add(mapearVinho(rs));
            }
        }
        return vinhos;
    }

    public List<Vinho> listarPorCategoria(Integer idCategoria) throws Exception {
        String sql = "SELECT * FROM vinho WHERE id_categoria = ? AND ativo = true ORDER BY nome";
        List<Vinho> vinhos = new ArrayList<>();

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idCategoria);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                vinhos.add(mapearVinho(rs));
            }
        }
        return vinhos;
    }

    public List<Vinho> listarPorTipo(String tipo) throws Exception {
        String sql = "SELECT * FROM vinho WHERE tipo = ? AND ativo = true ORDER BY nome";
        List<Vinho> vinhos = new ArrayList<>();

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, tipo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                vinhos.add(mapearVinho(rs));
            }
        }
        return vinhos;
    }

    public List<Vinho> buscarPorNome(String nome) throws Exception {
        String sql = "SELECT * FROM vinho WHERE nome LIKE ? AND ativo = true ORDER BY nome";
        List<Vinho> vinhos = new ArrayList<>();

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                vinhos.add(mapearVinho(rs));
            }
        }
        return vinhos;
    }

    @Override
    public void atualizar(Vinho vinho) throws Exception {
        String sql = "UPDATE vinho SET id_categoria = ?, nome = ?, descricao = ?, tipo = ?, " +
                     "regiao = ?, pais = ?, safra = ?, preco = ?, teor_alcoolico = ?, volume_ml = ?, " +
                     "harmonizacao = ?, nota_degustacao = ?, estoque = ?, imagem_url = ?, ativo = ? " +
                     "WHERE id_vinho = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, vinho.getIdCategoria());
            stmt.setString(2, vinho.getNome());
            stmt.setString(3, vinho.getDescricao());
            stmt.setString(4, vinho.getTipo());
            stmt.setString(5, vinho.getRegiao());
            stmt.setString(6, vinho.getPais());
            stmt.setInt(7, vinho.getSafra());
            stmt.setBigDecimal(8, vinho.getPreco());
            stmt.setBigDecimal(9, vinho.getTeorAlcoolico());
            stmt.setInt(10, vinho.getVolumeMl());
            stmt.setString(11, vinho.getHarmonizacao());
            stmt.setString(12, vinho.getNotaDegustacao());
            stmt.setInt(13, vinho.getEstoque());
            stmt.setString(14, vinho.getImagemUrl());
            stmt.setBoolean(15, vinho.getAtivo());
            stmt.setInt(16, vinho.getIdVinho());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Integer id) throws Exception {
        String sql = "UPDATE vinho SET ativo = false WHERE id_vinho = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Vinho mapearVinho(ResultSet rs) throws SQLException {
        Vinho vinho = new Vinho();
        vinho.setIdVinho(rs.getInt("id_vinho"));
        vinho.setIdCategoria(rs.getInt("id_categoria"));
        vinho.setNome(rs.getString("nome"));
        vinho.setDescricao(rs.getString("descricao"));
        vinho.setTipo(rs.getString("tipo"));
        vinho.setRegiao(rs.getString("regiao"));
        vinho.setPais(rs.getString("pais"));
        vinho.setSafra(rs.getInt("safra"));
        vinho.setPreco(rs.getBigDecimal("preco"));
        vinho.setTeorAlcoolico(rs.getBigDecimal("teor_alcoolico"));
        vinho.setVolumeMl(rs.getInt("volume_ml"));
        vinho.setHarmonizacao(rs.getString("harmonizacao"));
        vinho.setNotaDegustacao(rs.getString("nota_degustacao"));
        vinho.setEstoque(rs.getInt("estoque"));
        vinho.setImagemUrl(rs.getString("imagem_url"));
        vinho.setAtivo(rs.getBoolean("ativo"));

        Timestamp dataCriacao = rs.getTimestamp("data_criacao");
        if (dataCriacao != null) {
            vinho.setDataCriacao(dataCriacao.toLocalDateTime());
        }

        Timestamp dataAtualizacao = rs.getTimestamp("data_atualizacao");
        if (dataAtualizacao != null) {
            vinho.setDataAtualizacao(dataAtualizacao.toLocalDateTime());
        }

        return vinho;
    }
}
