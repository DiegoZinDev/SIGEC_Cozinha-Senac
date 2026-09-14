package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.DAOS.UserDAO;
import com.sigec.system.sigec.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
    private Hyperlink lbesquecisenha;

    @FXML
    public void onButtonLoginClick(ActionEvent event) {
        String email = txtemail.getText();
        String senha = pswsenha.getText();

        if (email == null || email.trim().isEmpty() || senha == null || senha.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText(null);
            alert.setContentText("Todos os campos devem estar preenchidos!");
            alert.showAndWait();
            return;
        }

        try {
            boolean autenticado = UserDAO.autenticar(email.trim(), senha);
            if (autenticado) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("Login realizado com sucesso!");
                alert.showAndWait();

                MainApplication.trocadorDeTelas("home.fxml");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Usuário ou senha incorretos.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Banco de Dados");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao conectar ao banco de dados: " + e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Navegação");
            alert.setHeaderText(null);
            alert.setContentText("Não foi possível carregar a tela principal: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void EsqueciSenha(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("esqueceu-senha.fxml");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Navegação");
            alert.setHeaderText(null);
            alert.setContentText("Não foi possível carregar a tela de recuperação de senha: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void Cadastrarme(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("cadastro.fxml");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Navegação");
            alert.setHeaderText(null);
            alert.setContentText("Não foi possível carregar a tela de cadastro: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
