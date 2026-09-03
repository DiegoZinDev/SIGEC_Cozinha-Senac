package com.sigec.system.sigec.DAOS;

import com.sigec.system.sigec.DTBConfig.ConfigDataBase;
import com.sigec.system.sigec.Services.EncryptService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
