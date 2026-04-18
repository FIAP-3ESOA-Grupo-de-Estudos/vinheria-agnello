package br.com.fiap.pbl3.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    
    private static final String URL  = System.getenv().getOrDefault("DB_URL",  "jdbc:mysql://localhost:3306/vinheria_agnello");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASS = System.getenv().getOrDefault("DB_PASS", "");

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL não encontrado.", e);
        }
    }

    public static Connection getConexao() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            throw new SQLException("Erro ao conectar ao banco. Verifique se MySQL está rodando e as credenciais estão corretas.", e);
        }
    }

    public static void fecharConexao(Connection conexao) {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
