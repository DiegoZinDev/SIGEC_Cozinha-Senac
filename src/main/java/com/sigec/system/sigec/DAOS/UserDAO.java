package com.sigec.system.sigec.DAOS;

import com.sigec.system.sigec.DTBConfig.ConfigDataBase;
import com.sigec.system.sigec.Services.EncryptService;

import java.sql.*;

public class UserDAO {

    public static boolean autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT senha FROM usuario WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))";

        try (Connection conn = ConfigDataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String senhaHash = rs.getString("senha");
                    return EncryptService.checkHash(senha, senhaHash);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro 101 ao autenticar: " + e.getMessage());
            throw e;
        }
        return false;
    }

    public String obterSenhaHash(String email) {
        String sql = "SELECT senha FROM usuario WHERE LOWER(TRIM(email)) = LOWER(TRIM(?))";
        try (Connection conn = ConfigDataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("senha");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean cadastrar(String nomeC, String emailC, String senhaC, String acessoC) throws SQLException {
        String sql = "INSERT INTO usuario(nome_usuario, email, senha, acesso) VALUES (?, ?, ?, ?)";
        String senhaCodificada = EncryptService.encrypt(senhaC);
        String acessoChar = (acessoC != null && !acessoC.isBlank()) ? acessoC.substring(0, 1).toUpperCase() : "C";
        try (Connection conn = ConfigDataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeC);
            stmt.setString(2, emailC);
            stmt.setString(3, senhaCodificada);
            stmt.setString(4, acessoChar);
            stmt.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Erro interno no banco de dados ao criar usuário", e);
        }
    }

}
