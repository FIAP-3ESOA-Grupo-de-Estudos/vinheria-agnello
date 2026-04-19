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
import br.com.vinheriaagnello.model.Carrinho;

public class CarrinhoDAO implements DAO<Carrinho, Integer> {

    @Override
    public void inserir(Carrinho carrinho) throws Exception {
        String sql = "INSERT INTO carrinho (id_usuario, status) VALUES (?, ?)";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, carrinho.getIdUsuario());
            stmt.setString(2, carrinho.getStatus());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    carrinho.setIdCarrinho(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Carrinho buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM carrinho WHERE id_carrinho = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearCarrinho(rs);
            }
        }
        return null;
    }

    public Carrinho buscarCarrinhoAbertoPorUsuario(Integer idUsuario) throws Exception {
        String sql = "SELECT * FROM carrinho WHERE id_usuario = ? AND status = 'ativo' LIMIT 1";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearCarrinho(rs);
            }
        }
        return null;
    }

    @Override
    public List<Carrinho> listarTodos() throws Exception {
        String sql = "SELECT * FROM carrinho ORDER BY data_criacao DESC";
        List<Carrinho> carrinhos = new ArrayList<>();

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                carrinhos.add(mapearCarrinho(rs));
            }
        }
        return carrinhos;
    }

    @Override
    public void atualizar(Carrinho carrinho) throws Exception {
        String sql = "UPDATE carrinho SET id_usuario = ?, status = ? WHERE id_carrinho = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, carrinho.getIdUsuario());
            stmt.setString(2, carrinho.getStatus());
            stmt.setInt(3, carrinho.getIdCarrinho());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Integer id) throws Exception {
        String sql = "UPDATE carrinho SET status = 'abandonado' WHERE id_carrinho = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Carrinho mapearCarrinho(ResultSet rs) throws SQLException {
        Carrinho carrinho = new Carrinho();
        carrinho.setIdCarrinho(rs.getInt("id_carrinho"));
        carrinho.setIdUsuario(rs.getInt("id_usuario"));
        carrinho.setStatus(rs.getString("status"));

        Timestamp dataCriacao = rs.getTimestamp("data_criacao");
        if (dataCriacao != null) {
            carrinho.setDataCriacao(dataCriacao.toLocalDateTime());
        }

        Timestamp dataAtualizacao = rs.getTimestamp("data_atualizacao");
        if (dataAtualizacao != null) {
            carrinho.setDataAtualizacao(dataAtualizacao.toLocalDateTime());
        }

        return carrinho;
    }
}
