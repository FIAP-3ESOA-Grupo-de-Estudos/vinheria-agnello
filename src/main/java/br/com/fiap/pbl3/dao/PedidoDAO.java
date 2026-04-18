package br.com.fiap.pbl3.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.com.fiap.pbl3.factory.ConnectionFactory;
import br.com.fiap.pbl3.model.Pedido;

public class PedidoDAO implements DAO<Pedido, Integer> {
    
    private Connection conexao;

    public PedidoDAO() {
        try {
            this.conexao = ConnectionFactory.getConexao();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter conexão", e);
        }
    }

    @Override
    public void inserir(Pedido pedido) throws Exception {
        String sql = "INSERT INTO pedido (id_usuario, id_endereco, numero_pedido, valor_subtotal, " +
                     "valor_desconto, valor_frete, valor_total, status_pedido) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, pedido.getIdUsuario());
            stmt.setInt(2, pedido.getIdEndereco());
            stmt.setString(3, pedido.getNumeroPedido());
            stmt.setBigDecimal(4, pedido.getValorSubtotal());
            stmt.setBigDecimal(5, pedido.getValorDesconto() != null ? pedido.getValorDesconto() : BigDecimal.ZERO);
            stmt.setBigDecimal(6, pedido.getValorFrete() != null ? pedido.getValorFrete() : BigDecimal.ZERO);
            stmt.setBigDecimal(7, pedido.getValorTotal());
            stmt.setString(8, pedido.getStatusPedido());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    pedido.setIdPedido(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Pedido buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM pedido WHERE id_pedido = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearPedido(rs);
            }
        }
        return null;
    }

    public Pedido buscarPorNumeroPedido(String numeroPedido) throws Exception {
        String sql = "SELECT * FROM pedido WHERE numero_pedido = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, numeroPedido);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearPedido(rs);
            }
        }
        return null;
    }

    public List<Pedido> listarPorUsuario(Integer idUsuario) throws Exception {
        String sql = "SELECT * FROM pedido WHERE id_usuario = ? ORDER BY data_pedido DESC";
        List<Pedido> pedidos = new ArrayList<>();
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }
        }
        return pedidos;
    }

    @Override
    public List<Pedido> listarTodos() throws Exception {
        String sql = "SELECT * FROM pedido ORDER BY data_pedido DESC";
        List<Pedido> pedidos = new ArrayList<>();
        
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }
        }
        return pedidos;
    }

    @Override
    public void atualizar(Pedido pedido) throws Exception {
        String sql = "UPDATE pedido SET id_usuario = ?, id_endereco = ?, valor_subtotal = ?, " +
                     "valor_desconto = ?, valor_frete = ?, valor_total = ?, status_pedido = ? " +
                     "WHERE id_pedido = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, pedido.getIdUsuario());
            stmt.setInt(2, pedido.getIdEndereco());
            stmt.setBigDecimal(3, pedido.getValorSubtotal());
            stmt.setBigDecimal(4, pedido.getValorDesconto() != null ? pedido.getValorDesconto() : BigDecimal.ZERO);
            stmt.setBigDecimal(5, pedido.getValorFrete() != null ? pedido.getValorFrete() : BigDecimal.ZERO);
            stmt.setBigDecimal(6, pedido.getValorTotal());
            stmt.setString(7, pedido.getStatusPedido());
            stmt.setInt(8, pedido.getIdPedido());
            
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Integer id) throws Exception {
        String sql = "UPDATE pedido SET status_pedido = 'cancelado' WHERE id_pedido = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(rs.getInt("id_pedido"));
        pedido.setIdUsuario(rs.getInt("id_usuario"));
        pedido.setIdEndereco(rs.getInt("id_endereco"));
        pedido.setNumeroPedido(rs.getString("numero_pedido"));
        
        Timestamp dataPedido = rs.getTimestamp("data_pedido");
        if (dataPedido != null) {
            pedido.setDataPedido(dataPedido.toLocalDateTime());
        }
        
        pedido.setValorSubtotal(rs.getBigDecimal("valor_subtotal"));
        pedido.setValorDesconto(rs.getBigDecimal("valor_desconto"));
        pedido.setValorFrete(rs.getBigDecimal("valor_frete"));
        pedido.setValorTotal(rs.getBigDecimal("valor_total"));
        pedido.setStatusPedido(rs.getString("status_pedido"));
        
        Timestamp dataAtualizacao = rs.getTimestamp("data_atualizacao");
        if (dataAtualizacao != null) {
            pedido.setDataAtualizacao(dataAtualizacao.toLocalDateTime());
        }
        
        return pedido;
    }
}
