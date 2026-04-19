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
import br.com.vinheriaagnello.model.Usuario;

public class UsuarioDAO implements DAO<Usuario, Integer> {

    @Override
    public void inserir(Usuario usuario) throws Exception {
        String sql = "INSERT INTO usuario (nome, email, senha, cpf, telefone, tipo_usuario, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getSenha());
            stmt.setString(4, usuario.getCpf());
            stmt.setString(5, usuario.getTelefone());
            stmt.setString(6, usuario.getTipoUsuario());
            stmt.setString(7, usuario.getStatus());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setIdUsuario(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Usuario buscarPorId(Integer id) throws Exception {
        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }
        }
        return null;
    }

    public Usuario buscarPorEmail(String email) throws Exception {
        String sql = "SELECT * FROM usuario WHERE email = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearUsuario(rs);
            }
        }
        return null;
    }

    @Override
    public List<Usuario> listarTodos() throws Exception {
        String sql = "SELECT * FROM usuario WHERE status = 'ativo' ORDER BY nome";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }
        return usuarios;
    }

    @Override
    public void atualizar(Usuario usuario) throws Exception {
        String sql = "UPDATE usuario SET nome = ?, email = ?, telefone = ?, status = ? " +
                     "WHERE id_usuario = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getTelefone());
            stmt.setString(4, usuario.getStatus());
            stmt.setInt(5, usuario.getIdUsuario());

            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Integer id) throws Exception {
        String sql = "UPDATE usuario SET status = 'inativo' WHERE id_usuario = ?";

        try (Connection conexao = DatabaseConfig.getDataSource().getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setCpf(rs.getString("cpf"));
        usuario.setTelefone(rs.getString("telefone"));
        usuario.setTipoUsuario(rs.getString("tipo_usuario"));
        usuario.setStatus(rs.getString("status"));

        Timestamp dataCad = rs.getTimestamp("data_cadastro");
        if (dataCad != null) {
            usuario.setDataCadastro(dataCad.toLocalDateTime());
        }

        Timestamp dataAtu = rs.getTimestamp("data_atualizacao");
        if (dataAtu != null) {
            usuario.setDataAtualizacao(dataAtu.toLocalDateTime());
        }

        return usuario;
    }
}
