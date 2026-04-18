package br.com.vinheriaagnello.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    public static void init() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql:///vinheriadb");
        config.setUsername("vinheriauser");
        config.setPassword("fiap2026");
        config.addDataSourceProperty("socketFactory",
            "com.google.cloud.sql.mysql.SocketFactory");
        config.addDataSourceProperty("cloudSqlInstance",
            "fiap-vinheria-agnello:southamerica-east1:vinheria-db");
        config.addDataSourceProperty("cloudSqlRefreshStrategy", "lazy");
        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
