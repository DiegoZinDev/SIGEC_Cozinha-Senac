package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.DAOS.UserDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField txtemail;
    @FXML
    private PasswordField pswsenha;
    @FXML
    private Button btnlogin;
    @FXML
    private Label lbesquecisenha;

    @FXML
   public void onButtonLoginClick() throws SQLException {
        UserDAO loginDAO = new UserDAO();
        String email = txtemail.getText();
        String senha = pswsenha.getText();

            if(email.trim().isEmpty() || senha == null || senha.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR 422");
                alert.setHeaderText(null);
                alert.setContentText("Todos os campos devem estar preenchidos");
            }
        boolean autenticado = UserDAO.autenticar(email.trim(), senha);
        if (autenticado) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("SUCESSO!");
            alert.setHeaderText(null);
            alert.setContentText("Sucesso, Login realizado com sucesso!");
            // Aqui você carrega a próxima tela (ex: home.fxml)
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR 404");
            alert.setHeaderText(null);
            alert.setContentText("Usuário não encontrado");
        }

    }
}
