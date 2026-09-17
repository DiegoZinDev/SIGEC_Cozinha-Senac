package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.io.IOException;

public class AlteraSenhaController {

    @FXML
    private PasswordField novaSenha;

    @FXML
    private PasswordField confirmaSenha;

    @FXML
    private Button confirmaTroca;

    @FXML
    private javafx.scene.layout.StackPane rootPane;

    @FXML
    private javafx.scene.layout.AnchorPane animatedBackground;

    @FXML
    public void initialize() {
        com.sigec.system.sigec.Utils.BackgroundAnimator.startAnimation(animatedBackground, rootPane);
    }

    @FXML
    public void onConfirmaTrocaClick(ActionEvent event) {
        String nova = novaSenha.getText();
        String confirma = confirmaSenha.getText();

        if (nova == null || nova.isEmpty() || confirma == null || confirma.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText(null);
            alert.setContentText("Preencha todos os campos para alterar a senha.");
            alert.showAndWait();
            return;
        }

        if (!nova.equals(confirma)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Senhas Diferentes");
            alert.setHeaderText(null);
            alert.setContentText("A nova senha e a confirmação devem ser iguais.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText("Senha alterada com sucesso! Faça login com sua nova credencial.");
        alert.showAndWait();

        try {
            MainApplication.trocadorDeTelas("login.fxml");
        } catch (IOException e) {
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro");
            erro.setHeaderText(null);
            erro.setContentText("Erro ao redirecionar para a tela de login: " + e.getMessage());
            erro.showAndWait();
        }
    }

    @FXML
    public void onVoltarClick(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("login.fxml");
        } catch (IOException e) {
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro");
            erro.setHeaderText(null);
            erro.setContentText("Erro ao voltar para a tela de login: " + e.getMessage());
            erro.showAndWait();
        }
    }
}
