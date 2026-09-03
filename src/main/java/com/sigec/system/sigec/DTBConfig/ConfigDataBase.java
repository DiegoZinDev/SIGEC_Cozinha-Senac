package com.sigec.system.sigec.DTBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConfigDataBase {
    public static Connection getConnection() throws SQLException{
        String url = "jdbc:mysql://localhost:3307/db_sigec";
        String user = "root";
        String pass = "senac";

        return DriverManager.getConnection(url, user, pass);
    }
}
