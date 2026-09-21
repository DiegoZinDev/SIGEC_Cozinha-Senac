package com.sigec.system.sigec.DTBConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConfigDataBase {
    private static final Map<String, String> ENV_FILE_CACHE = new HashMap<>();
    private static boolean envLoaded = false;

    private static synchronized void loadEnv() {
        if (envLoaded) return;
        envLoaded = true;

        String[] possiblePaths = {
            ".env",
            ".antigravity/.env",
            "../.env"
        };

        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) {
                            continue;
                        }
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String value = line.substring(eqIdx + 1).trim();
                            ENV_FILE_CACHE.putIfAbsent(key, value);
                        }
                    }
                    break;
                } catch (IOException e) {
                    System.err.println("Aviso: Falha ao ler arquivo de ambiente (" + path + "): " + e.getMessage());
                }
            }
        }
    }

    private static String getEnvVar(String key, String defaultValue) {
        String val = System.getenv(key);
        if (val != null && !val.isBlank()) {
            return val;
        }
        loadEnv();
        val = ENV_FILE_CACHE.get(key);
        if (val != null && !val.isBlank()) {
            return val;
        }
        return defaultValue;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
        }

        String url = getEnvVar("DB_URL", "");
        String user = getEnvVar("DB_USER", "");
        String pass = getEnvVar("DB_PASS", "");

        String host = getEnvVar("DB_HOST", "");
        String port = getEnvVar("DB_PORT", "");
        String dbName = getEnvVar("DB_NAME", "");

        if (url != null) {
            url = url.trim();
            // Corrige possíveis erros de digitação como mmysql://
            if (url.startsWith("mmysql://")) {
                url = url.substring(1);
            }
            // Adiciona o prefixo jdbc: caso o usuário tenha colado mysql://
            if (url.startsWith("mysql://")) {
                url = "jdbc:" + url;
            }

            // Se o usuário passou formato de URI (jdbc:mysql://user:pass@host:port/db)
            if (url.startsWith("jdbc:mysql://") && url.contains("@")) {
                int atIdx = url.indexOf('@');
                String userInfo = url.substring("jdbc:mysql://".length(), atIdx);
                String rest = url.substring(atIdx + 1);
                if (userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    if (user.isBlank()) user = parts[0];
                    if (pass.isBlank()) pass = parts[1];
                } else if (user.isBlank()) {
                    user = userInfo;
                }
                url = "jdbc:mysql://" + rest;
            }

            // Normaliza parâmetros de SSL
            url = url.replace("ssl-mode=", "sslMode=");

            // Se estiver apontando para defaultdb e houver DB_NAME configurado, ajusta o banco
            if (url.contains("/defaultdb") && !dbName.isBlank() && !"defaultdb".equals(dbName)) {
                url = url.replace("/defaultdb", "/" + dbName);
            }
        }

        // Se a URL estiver vazia, monta com base nas variáveis individuais
        if (url == null || url.isBlank()) {
            if (host.isBlank() || port.isBlank() || dbName.isBlank()) {
                throw new SQLException("Configurações do banco de dados (DB_URL ou DB_HOST/DB_PORT/DB_NAME) não encontradas no arquivo .env");
            }
            url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?sslMode=REQUIRED&serverTimezone=America/Sao_Paulo";
        }

        if (user.isBlank() || pass.isBlank()) {
            throw new SQLException("Credenciais do banco de dados (DB_USER, DB_PASS) não encontradas no arquivo .env");
        }

        return DriverManager.getConnection(url, user, pass);
    }
}
