package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL      = "jdbc:mysql://localhost:3306/taco_express_db"
                                         + "?useSSL=false&serverTimezone=America/Sao_Paulo"
                                         + "&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = ""; // colocar senha caso tenha

    private ConnectionFactory() {}

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao conectar ao banco: " + e.getMessage(), e);
        }
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }
}
