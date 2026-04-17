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
import br.com.fiap.pbl3.model.Avaliacao;

public class AvaliacaoDAO implements DAO<Avaliacao, Integer> {
    
    private Connection conexao;

    public AvaliacaoDAO() {
        try {
            this.conexao = ConnectionFactory.getConexao();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter conexão", e);
        }
    }

    @Override
    public void inserir(Avaliacao avaliacao) throws Exception {
        String sql = "INSERT INTO avaliacao (id_usuario, id_vinho, nota, comentario, status_aprovacao) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, avaliacao.getIdUsuario());
            stmt.setInt(2, avaliacao.getIdVinho());
            stmt.setInt(3, avaliacao.getNota());
            stmt.setString(4, avaliacao.getComentario());
            stmt.setString(5, avaliacao.getStatusAprovacao());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    avaliacao.setIdAvaliacao(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Avaliacao buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM avaliacao WHERE id_avaliacao = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearAvaliacao(rs);
            }
        }
        return null;
    }

    public List<Avaliacao> listarPorVinho(Integer idVinho) throws Exception {
        String sql = "SELECT * FROM avaliacao WHERE id_vinho = ? AND status_aprovacao = 'aprovado' " +
                     "ORDER BY data_avaliacao DESC";
        List<Avaliacao> avaliacoes = new ArrayList<>();
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idVinho);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                avaliacoes.add(mapearAvaliacao(rs));
            }
        }
        return avaliacoes;
    }

    public List<Avaliacao> listarPorUsuario(Integer idUsuario) throws Exception {
        String sql = "SELECT * FROM avaliacao WHERE id_usuario = ? ORDER BY data_avaliacao DESC";
        List<Avaliacao> avaliacoes = new ArrayList<>();
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                avaliacoes.add(mapearAvaliacao(rs));
            }
        }
        return avaliacoes;
    }

    public List<Avaliacao> listarPendentes() throws Exception {
        String sql = "SELECT * FROM avaliacao WHERE status_aprovacao = 'pendente' ORDER BY data_avaliacao ASC";
        List<Avaliacao> avaliacoes = new ArrayList<>();
        
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                avaliacoes.add(mapearAvaliacao(rs));
            }
        }
        return avaliacoes;
    }

    @Override
    public List<Avaliacao> listarTodos() throws Exception {
        String sql = "SELECT * FROM avaliacao ORDER BY data_avaliacao DESC";
        List<Avaliacao> avaliacoes = new ArrayList<>();
        
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                avaliacoes.add(mapearAvaliacao(rs));
            }
        }
        return avaliacoes;
    }

    @Override
    public void atualizar(Avaliacao avaliacao) throws Exception {
        String sql = "UPDATE avaliacao SET status_aprovacao = ?, data_aprovacao = ? WHERE id_avaliacao = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, avaliacao.getStatusAprovacao());
            stmt.setTimestamp(2, avaliacao.getDataAprovacao() != null ? 
                             Timestamp.valueOf(avaliacao.getDataAprovacao()) : null);
            stmt.setInt(3, avaliacao.getIdAvaliacao());
            
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Integer id) throws Exception {
        String sql = "DELETE FROM avaliacao WHERE id_avaliacao = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Avaliacao mapearAvaliacao(ResultSet rs) throws SQLException {
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setIdAvaliacao(rs.getInt("id_avaliacao"));
        avaliacao.setIdUsuario(rs.getInt("id_usuario"));
        avaliacao.setIdVinho(rs.getInt("id_vinho"));
        avaliacao.setNota(rs.getInt("nota"));
        avaliacao.setComentario(rs.getString("comentario"));
        avaliacao.setStatusAprovacao(rs.getString("status_aprovacao"));
        
        Timestamp dataAvaliacao = rs.getTimestamp("data_avaliacao");
        if (dataAvaliacao != null) {
            avaliacao.setDataAvaliacao(dataAvaliacao.toLocalDateTime());
        }
        
        Timestamp dataAprovacao = rs.getTimestamp("data_aprovacao");
        if (dataAprovacao != null) {
            avaliacao.setDataAprovacao(dataAprovacao.toLocalDateTime());
        }
        
        return avaliacao;
    }
}
