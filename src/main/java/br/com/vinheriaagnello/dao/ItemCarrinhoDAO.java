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
import br.com.vinheriaagnello.model.ItemCarrinho;

public class ItemCarrinhoDAO implements DAO<ItemCarrinho, Integer> {

    @Override
    public void inserir(ItemCarrinho item) throws Exception {
        String sql = "INSERT INTO item_carrinho (id_carrinho, id_vinho, quantidade, preco_unitario) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getIdCarrinho());
            stmt.setInt(2, item.getIdVinho());
            stmt.setInt(3, item.getQuantidade());
            stmt.setBigDecimal(4, item.getPrecoUnitario());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setIdItemCarrinho(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public ItemCarrinho buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM item_carrinho WHERE id_item_carrinho = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearItemCarrinho(rs);
            }
        }
        return null;
    }

    public List<ItemCarrinho> listarPorCarrinho(Integer idCarrinho) throws Exception {
        String sql = "SELECT * FROM item_carrinho WHERE id_carrinho = ? ORDER BY data_adicao";
        List<ItemCarrinho> itens = new ArrayList<>();

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idCarrinho);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                itens.add(mapearItemCarrinho(rs));
            }
        }
        return itens;
    }

    @Override
    public List<ItemCarrinho> listarTodos() throws Exception {
        String sql = "SELECT * FROM item_carrinho ORDER BY data_adicao DESC";
        List<ItemCarrinho> itens = new ArrayList<>();

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                itens.add(mapearItemCarrinho(rs));
            }
        }
        return itens;
    }

    @Override
    public void atualizar(ItemCarrinho item) throws Exception {
        String sql = "UPDATE item_carrinho SET quantidade = ?, preco_unitario = ? WHERE id_item_carrinho = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, item.getQuantidade());
            stmt.setBigDecimal(2, item.getPrecoUnitario());
            stmt.setInt(3, item.getIdItemCarrinho());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Integer id) throws Exception {
        String sql = "DELETE FROM item_carrinho WHERE id_item_carrinho = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void deletarPorCarrinho(Integer idCarrinho) throws Exception {
        String sql = "DELETE FROM item_carrinho WHERE id_carrinho = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idCarrinho);
            stmt.executeUpdate();
        }
    }

    private ItemCarrinho mapearItemCarrinho(ResultSet rs) throws SQLException {
        ItemCarrinho item = new ItemCarrinho();
        item.setIdItemCarrinho(rs.getInt("id_item_carrinho"));
        item.setIdCarrinho(rs.getInt("id_carrinho"));
        item.setIdVinho(rs.getInt("id_vinho"));
        item.setQuantidade(rs.getInt("quantidade"));
        item.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));

        Timestamp dataAdicao = rs.getTimestamp("data_adicao");
        if (dataAdicao != null) {
            item.setDataAdicao(dataAdicao.toLocalDateTime());
        }

        return item;
    }
}
