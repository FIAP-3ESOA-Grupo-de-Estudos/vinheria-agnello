package br.com.fiap.pbl3.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import br.com.fiap.pbl3.factory.ConnectionFactory;
import br.com.fiap.pbl3.model.Pagamento;

public class PagamentoDAO implements DAO<Pagamento, Integer> {
    
    private Connection conexao;

    public PagamentoDAO() {
        try {
            this.conexao = ConnectionFactory.getConexao();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter conexão", e);
        }
    }

    @Override
    public void inserir(Pagamento pagamento) throws Exception {
        String sql = "INSERT INTO pagamento (id_pedido, tipo_pagamento, status_pagamento, valor, " +
                     "parcelas, codigo_transacao) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, pagamento.getIdPedido());
            stmt.setString(2, pagamento.getTipoPagamento());
            stmt.setString(3, pagamento.getStatusPagamento());
            stmt.setBigDecimal(4, pagamento.getValor());
            stmt.setInt(5, pagamento.getParcelas());
            stmt.setString(6, pagamento.getCodigoTransacao());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    pagamento.setIdPagamento(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Pagamento buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM pagamento WHERE id_pagamento = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearPagamento(rs);
            }
        }
        return null;
    }

    public Pagamento buscarPorPedido(Integer idPedido) throws Exception {
        String sql = "SELECT * FROM pagamento WHERE id_pedido = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idPedido);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearPagamento(rs);
            }
        }
        return null;
    }

    @Override
    public List<Pagamento> listarTodos() throws Exception {
        String sql = "SELECT * FROM pagamento ORDER BY data_criacao DESC";
        List<Pagamento> pagamentos = new ArrayList<>();
        
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                pagamentos.add(mapearPagamento(rs));
            }
        }
        return pagamentos;
    }

    public List<Pagamento> listarPorStatus(String status) throws Exception {
        String sql = "SELECT * FROM pagamento WHERE status_pagamento = ? ORDER BY data_criacao DESC";
        List<Pagamento> pagamentos = new ArrayList<>();
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                pagamentos.add(mapearPagamento(rs));
            }
        }
        return pagamentos;
    }

    @Override
    public void atualizar(Pagamento pagamento) throws Exception {
        String sql = "UPDATE pagamento SET status_pagamento = ?, codigo_transacao = ?, data_pagamento = ? " +
                     "WHERE id_pagamento = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, pagamento.getStatusPagamento());
            stmt.setString(2, pagamento.getCodigoTransacao());
            stmt.setTimestamp(3, pagamento.getDataPagamento() != null ? 
                             Timestamp.valueOf(pagamento.getDataPagamento()) : null);
            stmt.setInt(4, pagamento.getIdPagamento());
            
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Integer id) throws Exception {
        // Pagamentos não devem ser deletados, apenas cancelados
        String sql = "UPDATE pagamento SET status_pagamento = 'cancelado' WHERE id_pagamento = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Pagamento mapearPagamento(ResultSet rs) throws SQLException {
        Pagamento pagamento = new Pagamento();
        pagamento.setIdPagamento(rs.getInt("id_pagamento"));
        pagamento.setIdPedido(rs.getInt("id_pedido"));
        pagamento.setTipoPagamento(rs.getString("tipo_pagamento"));
        pagamento.setStatusPagamento(rs.getString("status_pagamento"));
        pagamento.setValor(rs.getBigDecimal("valor"));
        pagamento.setParcelas(rs.getInt("parcelas"));
        pagamento.setCodigoTransacao(rs.getString("codigo_transacao"));
        
        Timestamp dataPagamento = rs.getTimestamp("data_pagamento");
        if (dataPagamento != null) {
            pagamento.setDataPagamento(dataPagamento.toLocalDateTime());
        }
        
        Timestamp dataCriacao = rs.getTimestamp("data_criacao");
        if (dataCriacao != null) {
            pagamento.setDataCriacao(dataCriacao.toLocalDateTime());
        }
        
        Timestamp dataAtualizacao = rs.getTimestamp("data_atualizacao");
        if (dataAtualizacao != null) {
            pagamento.setDataAtualizacao(dataAtualizacao.toLocalDateTime());
        }
        
        return pagamento;
    }
}
