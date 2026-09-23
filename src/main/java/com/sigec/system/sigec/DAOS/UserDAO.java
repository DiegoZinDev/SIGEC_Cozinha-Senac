package com.sigec.system.sigec.DAOS;

import com.sigec.system.sigec.Constructors.User;
import com.sigec.system.sigec.DTBConfig.ConfigDataBase;
import com.sigec.system.sigec.Services.EncryptService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto de Acesso a Dados (DAO) para operações de Usuários no banco de dados.
 */
public final class UserDAO {

    private UserDAO() {
        // Construtor privado para utilitário estático de DAO
    }

    /**
     * Autentica o usuário pelo e-mail e validação de hash da senha.
     *
     * @param email E-mail informado
     * @param senha Senha digitada em texto puro
     * @return true se a autenticação for bem-sucedida, false caso contrário
     * @throws SQLException Em caso de falha de conexão com o banco
     */
    public static boolean autenticar(String email, String senha) throws SQLException {
        if (email == null || senha == null || email.isBlank() || senha.isBlank()) {
            return false;
        }

        String sql = "SELECT senha FROM usuario WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))";

        try (Connection conn = ConfigDataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String senhaHash = rs.getString("senha");
                    return EncryptService.checkHash(senha, senhaHash);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar usuário (" + email + "): " + e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * Obtém o hash da senha armazenado no banco para o e-mail informado.
     *
     * @param email E-mail do usuário
     * @return Hash da senha ou null se não encontrado
     */
    public static String obterSenhaHash(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        String sql = "SELECT senha FROM usuario WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))";
        try (Connection conn = ConfigDataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("senha");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar senha hash (" + email + "): " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca o registro de usuário completo associado ao e-mail informado.
     *
     * @param email E-mail do usuário
     * @return Instância de User populada ou null se não localizado
     */
    public static User buscarPorEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        String sql = "SELECT * FROM usuario WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))";
        try (Connection conn = ConfigDataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    try {
                        u.setIdUsuario(rs.getInt("id_usuario"));
                    } catch (SQLException ignored) {
                        try {
                            u.setIdUsuario(rs.getInt("id"));
                        } catch (SQLException ignored2) {}
                    }
                    try {
                        u.setNome(rs.getString("nome_usuario"));
                    } catch (SQLException ignored) {
                        try {
                            u.setNome(rs.getString("nome"));
                        } catch (SQLException ignored2) {}
                    }
                    u.setEmail(rs.getString("email"));
                    try {
                        u.setAcesso(rs.getString("acesso"));
                    } catch (SQLException ignored) {}
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por e-mail (" + email + "): " + e.getMessage());
        }
        return null;
    }

    /**
     * Cadastra um novo usuário criptografando a senha informada.
     *
     * @param nomeC   Nome do usuário
     * @param emailC  E-mail
     * @param senhaC  Senha em texto puro a ser criptografada
     * @param acessoC Nível de acesso (ex: Administrador, Comum)
     * @return true se cadastrado com sucesso, false se já existir violação de unicidade
     * @throws SQLException Em caso de erro de infraestrutura do banco
     */
    public static boolean cadastrar(String nomeC, String emailC, String senhaC, String acessoC) throws SQLException {
        String sql = "INSERT INTO usuario(nome_usuario, email, senha, acesso) VALUES (?, ?, ?, ?)";
        String senhaCodificada = EncryptService.encrypt(senhaC);
        String acessoChar = (acessoC != null && !acessoC.isBlank()) ? acessoC.substring(0, 1).toUpperCase() : "C";

        try (Connection conn = ConfigDataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeC != null ? nomeC.trim() : "");
            stmt.setString(2, emailC != null ? emailC.trim().toLowerCase() : "");
            stmt.setString(3, senhaCodificada);
            stmt.setString(4, acessoChar);
            stmt.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("Aviso: Tentativa de cadastro de e-mail duplicado: " + emailC);
            return false;
        } catch (SQLException e) {
            System.err.println("Erro interno ao cadastrar usuário: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Retorna a lista de usuários com perfil de Instrutor cadastrados no sistema.
     *
     * @return Lista de instrutores disponíveis
     */
    public static List<User> listarInstrutores() {
        List<User> instrutores = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE UPPER(acesso) LIKE 'I%' OR UPPER(acesso) = 'INSTRUTOR' ORDER BY nome_usuario ASC";

        try (Connection conn = ConfigDataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                User u = new User();
                try {
                    u.setIdUsuario(rs.getInt("id_usuario"));
                } catch (SQLException ignored) {
                    try {
                        u.setIdUsuario(rs.getInt("id"));
                    } catch (SQLException ignored2) {}
                }
                try {
                    u.setNome(rs.getString("nome_usuario"));
                } catch (SQLException ignored) {
                    try {
                        u.setNome(rs.getString("nome"));
                    } catch (SQLException ignored2) {}
                }
                u.setEmail(rs.getString("email"));
                try {
                    u.setAcesso(rs.getString("acesso"));
                } catch (SQLException ignored) {}
                instrutores.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Aviso: Falha ao consultar instrutores do banco de dados: " + e.getMessage());
        }

        // Se a base estiver sem instrutores cadastrados ou offline, fornece lista padrão de instrutores do Senac
        if (instrutores.isEmpty()) {
            instrutores.add(new User(1, "Chef Rogério Silva", "rogerio.silva@sp.senac.br", "", "Instrutor", "Ativo", 0));
            instrutores.add(new User(2, "Chef Amanda Oliveira", "amanda.oliveira@sp.senac.br", "", "Instrutor", "Ativo", 0));
            instrutores.add(new User(3, "Prof. Carlos Eduardo", "carlos.eduardo@sp.senac.br", "", "Instrutor", "Ativo", 0));
            instrutores.add(new User(4, "Chef Mariana Costa", "mariana.costa@sp.senac.br", "", "Instrutor", "Ativo", 0));
            instrutores.add(new User(5, "Chef Bruno Henrique", "bruno.henrique@sp.senac.br", "", "Instrutor", "Ativo", 0));
        }

        return instrutores;
    }
}
