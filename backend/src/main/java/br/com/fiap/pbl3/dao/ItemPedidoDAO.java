package br.com.fiap.pbl3.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.com.fiap.pbl3.factory.ConnectionFactory;
import br.com.fiap.pbl3.model.ItemPedido;

public class ItemPedidoDAO implements DAO<ItemPedido, Integer> {
    
    private Connection conexao;

    public ItemPedidoDAO() {
        try {
            this.conexao = ConnectionFactory.getConexao();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter conexão", e);
        }
    }

    @Override
    public void inserir(ItemPedido item) throws Exception {
        String sql = "INSERT INTO item_pedido (id_pedido, id_vinho, quantidade, preco_unitario, subtotal) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getIdPedido());
            stmt.setInt(2, item.getIdVinho());
            stmt.setInt(3, item.getQuantidade());
            stmt.setBigDecimal(4, item.getPrecoUnitario());
            stmt.setBigDecimal(5, item.getSubtotal());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setIdItemPedido(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public ItemPedido buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM item_pedido WHERE id_item_pedido = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearItemPedido(rs);
            }
        }
        return null;
    }

    public List<ItemPedido> listarPorPedido(Integer idPedido) throws Exception {
        String sql = "SELECT * FROM item_pedido WHERE id_pedido = ?";
        List<ItemPedido> itens = new ArrayList<>();
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                itens.add(mapearItemPedido(rs));
            }
        }
        return itens;
    }

    @Override
    public List<ItemPedido> listarTodos() throws Exception {
        String sql = "SELECT * FROM item_pedido";
        List<ItemPedido> itens = new ArrayList<>();
        
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                itens.add(mapearItemPedido(rs));
            }
        }
        return itens;
    }

    @Override
    public void atualizar(ItemPedido item) throws Exception {
        String sql = "UPDATE item_pedido SET quantidade = ?, preco_unitario = ?, subtotal = ? " +
                     "WHERE id_item_pedido = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, item.getQuantidade());
            stmt.setBigDecimal(2, item.getPrecoUnitario());
            stmt.setBigDecimal(3, item.getSubtotal());
            stmt.setInt(4, item.getIdItemPedido());
            
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Integer id) throws Exception {
        String sql = "DELETE FROM item_pedido WHERE id_item_pedido = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private ItemPedido mapearItemPedido(ResultSet rs) throws SQLException {
        ItemPedido item = new ItemPedido();
        item.setIdItemPedido(rs.getInt("id_item_pedido"));
        item.setIdPedido(rs.getInt("id_pedido"));
        item.setIdVinho(rs.getInt("id_vinho"));
        item.setQuantidade(rs.getInt("quantidade"));
        item.setPrecoUnitario(rs.getBigDecimal("preco_unitario"));
        item.setSubtotal(rs.getBigDecimal("subtotal"));
        return item;
    }
}
