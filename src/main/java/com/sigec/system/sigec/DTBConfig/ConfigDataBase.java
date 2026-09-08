package com.sigec.system.sigec.DTBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConfigDataBase {
    public static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL");
        if (url == null || url.isBlank()) {
            url = "jdbc:mysql://localhost:3307/db_sigec?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo";
        }

        String user = System.getenv("DB_USER");
        if (user == null || user.isBlank()) {
            user = "root";
        }

        String pass = System.getenv("DB_PASS");
        if (pass == null) {
            pass = "senac";
        }

        return DriverManager.getConnection(url, user, pass);
    }
}
