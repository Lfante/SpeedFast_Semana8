package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centraliza la conexión a MySQL.
 */
public class ConexionDB {

    private static final String URL = System.getenv().getOrDefault(
            "SPEEDFAST_DB_URL",
            "jdbc:mysql://localhost:3306/speedfast_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    );

    private static final String USER = System.getenv().getOrDefault(
            "SPEEDFAST_DB_USER",
            "root"
    );

    private static final String PASSWORD = System.getenv().getOrDefault(
            "SPEEDFAST_DB_PASSWORD",
            ""
    );

    private ConexionDB() {
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
