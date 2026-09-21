package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ConfirmaEmailController {

    @FXML
    private TextField emailDigitar;

    @FXML
    private TextField codigoDigitar;

    @FXML
    private javafx.scene.layout.StackPane rootPane;

    @FXML
    private javafx.scene.layout.AnchorPane animatedBackground;

    @FXML
    public void initialize() {
        com.sigec.system.sigec.Utils.BackgroundAnimator.startAnimation(animatedBackground, rootPane);
    }

    @FXML
    public void onConfirmaEmailClick(ActionEvent event) {
        String email = emailDigitar.getText();

        if (email == null || email.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText(null);
            alert.setContentText("Informe o seu e-mail cadastrado.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Código Enviado");
        alert.setHeaderText(null);
        alert.setContentText("Se o e-mail estiver cadastrado, um código de verificação foi enviado. Por favor, verifique sua caixa de entrada.");
        alert.showAndWait();
    }

    @FXML
    public void onConfirmaToken(ActionEvent event) {
        String codigo = codigoDigitar.getText();

        if (codigo == null || codigo.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText(null);
            alert.setContentText("Digite o código recebido no seu e-mail.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Código Confirmado");
        alert.setHeaderText(null);
        alert.setContentText("Código validado com sucesso! Você será redirecionado para a alteração de senha.");
        alert.showAndWait();

        try {
            MainApplication.trocadorDeTelas("alterar-senha.fxml");
        } catch (IOException e) {
            Alert erro = new Alert(Alert.AlertType.ERROR);
            erro.setTitle("Erro");
            erro.setHeaderText(null);
            erro.setContentText("Não foi possível carregar a tela de alteração de senha: " + e.getMessage());
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
            erro.setContentText("Erro ao voltar para o login: " + e.getMessage());
            erro.showAndWait();
        }
    }
}
