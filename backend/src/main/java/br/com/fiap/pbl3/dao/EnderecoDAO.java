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
import br.com.fiap.pbl3.model.Endereco;

public class EnderecoDAO implements DAO<Endereco, Integer> {
    
    private Connection conexao;

    public EnderecoDAO() {
        try {
            this.conexao = ConnectionFactory.getConexao();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao obter conexão", e);
        }
    }

    @Override
    public void inserir(Endereco endereco) throws Exception {
        String sql = "INSERT INTO endereco (id_usuario, cep, logradouro, numero, complemento, bairro, cidade, estado, principal) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, endereco.getIdUsuario());
            stmt.setString(2, endereco.getCep());
            stmt.setString(3, endereco.getLogradouro());
            stmt.setString(4, endereco.getNumero());
            stmt.setString(5, endereco.getComplemento());
            stmt.setString(6, endereco.getBairro());
            stmt.setString(7, endereco.getCidade());
            stmt.setString(8, endereco.getEstado());
            stmt.setBoolean(9, endereco.getPrincipal());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    endereco.setIdEndereco(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Endereco buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM endereco WHERE id_endereco = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearEndereco(rs);
            }
        }
        return null;
    }

    public List<Endereco> listarPorUsuario(Integer idUsuario) throws Exception {
        String sql = "SELECT * FROM endereco WHERE id_usuario = ? ORDER BY principal DESC, data_cadastro DESC";
        List<Endereco> enderecos = new ArrayList<>();
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                enderecos.add(mapearEndereco(rs));
            }
        }
        return enderecos;
    }

    public Endereco buscarEnderecoPrincipal(Integer idUsuario) throws Exception {
        String sql = "SELECT * FROM endereco WHERE id_usuario = ? AND principal = true LIMIT 1";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearEndereco(rs);
            }
        }
        return null;
    }

    @Override
    public List<Endereco> listarTodos() throws Exception {
        String sql = "SELECT * FROM endereco ORDER BY data_cadastro DESC";
        List<Endereco> enderecos = new ArrayList<>();
        
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                enderecos.add(mapearEndereco(rs));
            }
        }
        return enderecos;
    }

    @Override
    public void atualizar(Endereco endereco) throws Exception {
        String sql = "UPDATE endereco SET cep = ?, logradouro = ?, numero = ?, complemento = ?, " +
                     "bairro = ?, cidade = ?, estado = ?, principal = ? WHERE id_endereco = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, endereco.getCep());
            stmt.setString(2, endereco.getLogradouro());
            stmt.setString(3, endereco.getNumero());
            stmt.setString(4, endereco.getComplemento());
            stmt.setString(5, endereco.getBairro());
            stmt.setString(6, endereco.getCidade());
            stmt.setString(7, endereco.getEstado());
            stmt.setBoolean(8, endereco.getPrincipal());
            stmt.setInt(9, endereco.getIdEndereco());
            
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Integer id) throws Exception {
        String sql = "DELETE FROM endereco WHERE id_endereco = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Endereco mapearEndereco(ResultSet rs) throws SQLException {
        Endereco endereco = new Endereco();
        endereco.setIdEndereco(rs.getInt("id_endereco"));
        endereco.setIdUsuario(rs.getInt("id_usuario"));
        endereco.setCep(rs.getString("cep"));
        endereco.setLogradouro(rs.getString("logradouro"));
        endereco.setNumero(rs.getString("numero"));
        endereco.setComplemento(rs.getString("complemento"));
        endereco.setBairro(rs.getString("bairro"));
        endereco.setCidade(rs.getString("cidade"));
        endereco.setEstado(rs.getString("estado"));
        endereco.setPrincipal(rs.getBoolean("principal"));
        
        Timestamp dataCadastro = rs.getTimestamp("data_cadastro");
        if (dataCadastro != null) {
            endereco.setDataCadastro(dataCadastro.toLocalDateTime());
        }
        
        return endereco;
    }
}
