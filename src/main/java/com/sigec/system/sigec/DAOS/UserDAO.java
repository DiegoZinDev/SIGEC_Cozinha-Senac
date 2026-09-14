package com.sigec.system.sigec.DAOS;

import com.sigec.system.sigec.DTBConfig.ConfigDataBase;
import com.sigec.system.sigec.Services.EncryptService;

import java.sql.*;

public class UserDAO {

    public static boolean autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT senha FROM usuario WHERE email = ?";

        try(Connection conn = ConfigDataBase.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try(ResultSet rs = stmt.executeQuery()) {
                if(rs.next()){
                    String senhaHash = rs.getString("senha");

                    return EncryptService.checkHash(senha,senhaHash); //faz a comparação das senhas diretamente pela conexao do banco...
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro 101: " + e.getMessage());
        }
    return false;
    }
    public String obterSenhaHash(String email) {
        String sql = "SELECT senha FROM usuario WHERE email = ?";
        try (Connection conn = ConfigDataBase.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("senha"); // Retorna o hash (ex: $2a$10$...)
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Usuário não encontrado
    }

    public static boolean cadastrar(String nomeC, String emailC, String senhaC, String acessoC) throws SQLException {
        String sql = "INSERT INTO usuario(nome, email, senha, acesso) VALUES (?, ?, ?, ?)";
        String senhaCodificada = EncryptService.encrypt(senhaC);
        try (Connection conn = ConfigDataBase.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nomeC);
            stmt.setString(2, emailC);
            stmt.setString(3, senhaCodificada);
            stmt.setString(4, acessoC);
            stmt.executeUpdate();
            return true;
        }catch (SQLIntegrityConstraintViolationException e){
            return false;
        }catch (SQLException e) {
            // Se for um erro de conexão ou outro problema no banco, estoura a exceção
            throw new RuntimeException("Erro interno no banco de dados ao criar usuário", e);
        }

    }

}
