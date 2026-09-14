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
        // Pegando os dados estritamente do ambiente ou do arquivo .env
        String url = getEnvVar("DB_URL", "");
        String user = getEnvVar("DB_USER", "");
        String pass = getEnvVar("DB_PASS", "");

        if (url.isBlank() || user.isBlank() || pass.isBlank()) {
            throw new SQLException("Credenciais do banco de dados não encontradas no arquivo .env");
        }

        return DriverManager.getConnection(url, user, pass);
    }
}
