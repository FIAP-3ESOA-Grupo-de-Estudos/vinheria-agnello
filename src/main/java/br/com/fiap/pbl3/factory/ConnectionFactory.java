package br.com.fiap.pbl3.factory;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL  = System.getenv().getOrDefault("DB_URL",  "jdbc:mysql://localhost:3306/vinheria_agnello");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASS = resolveDbPass();

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL não encontrado.", e);
        }
    }

    private static String resolveDbPass() {
        String projectId = System.getenv("GOOGLE_CLOUD_PROJECT");
        if (projectId != null && !projectId.isBlank()) {
            try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
                SecretVersionName name = SecretVersionName.of(projectId, "db-pass", "latest");
                AccessSecretVersionResponse response = client.accessSecretVersion(name);
                return response.getPayload().getData().toStringUtf8();
            } catch (Exception e) {
                System.err.println("Secret Manager indisponível, usando DB_PASS env var: " + e.getMessage());
            }
        }
        return System.getenv().getOrDefault("DB_PASS", "");
    }

    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
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
