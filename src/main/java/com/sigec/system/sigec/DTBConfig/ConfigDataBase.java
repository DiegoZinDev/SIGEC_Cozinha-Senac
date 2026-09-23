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

/**
 * Gerenciador de conexão com o banco de dados MySQL para o sistema SIGEC.
 * Lê credenciais e configurações de variáveis de ambiente ou arquivo .env local.
 */
public final class ConfigDataBase {

    private static final Map<String, String> ENV_FILE_CACHE = new HashMap<>();
    private static boolean envLoaded = false;

    private static final String[] POSSIBLE_ENV_PATHS = {
            ".env",
            ".antigravity/.env",
            "../.env"
    };

    private ConfigDataBase() {
        // Construtor privado para utilitário de infraestrutura
    }

    private static synchronized void loadEnv() {
        if (envLoaded) {
            return;
        }
        envLoaded = true;

        for (String path : POSSIBLE_ENV_PATHS) {
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

    /**
     * Obtém uma nova conexão ativa com o banco de dados.
     *
     * @return {@link Connection} conectada ao MySQL
     * @throws SQLException Em caso de falha de conexão ou credenciais inválidas
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignored) {
            // Driver moderno carrega automaticamente via ServiceLoader
        }

        String url = getEnvVar("DB_URL", "");
        String user = getEnvVar("DB_USER", "");
        String pass = getEnvVar("DB_PASS", "");

        String host = getEnvVar("DB_HOST", "");
        String port = getEnvVar("DB_PORT", "");
        String dbName = getEnvVar("DB_NAME", "");

        if (url != null && !url.isBlank()) {
            url = normalizarUrlConexao(url, dbName);
        }

        // Se a URL estiver vazia, monta com base nas variáveis individuais
        if (url == null || url.isBlank()) {
            if (host.isBlank() || port.isBlank() || dbName.isBlank()) {
                throw new SQLException("Configurações do banco de dados (DB_URL ou DB_HOST/DB_PORT/DB_NAME) não encontradas.");
            }
            url = "jdbc:mysql://" + host + ":" + port + "/" + dbName + "?sslMode=REQUIRED&serverTimezone=America/Sao_Paulo";
        }

        if (user.isBlank() || pass.isBlank()) {
            throw new SQLException("Credenciais do banco de dados (DB_USER, DB_PASS) não encontradas.");
        }

        return DriverManager.getConnection(url, user, pass);
    }

    private static String normalizarUrlConexao(String url, String dbName) {
        String normalizada = url.trim();

        if (normalizada.startsWith("mmysql://")) {
            normalizada = normalizada.substring(1);
        }
        if (normalizada.startsWith("mysql://")) {
            normalizada = "jdbc:" + normalizada;
        }

        // Normalização de parâmetros de segurança
        normalizada = normalizada.replace("ssl-mode=", "sslMode=");

        if (normalizada.contains("/defaultdb") && !dbName.isBlank() && !"defaultdb".equals(dbName)) {
            normalizada = normalizada.replace("/defaultdb", "/" + dbName);
        }

        return normalizada;
    }
}
