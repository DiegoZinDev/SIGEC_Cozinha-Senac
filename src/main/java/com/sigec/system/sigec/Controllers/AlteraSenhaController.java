package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.MainApplication;
import com.sigec.system.sigec.Utils.BackgroundAnimator;
import com.sigec.system.sigec.Utils.FormNavigationUtil;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * Controlador do fluxo de redefinição de senha do usuário.
 */
public class AlteraSenhaController {

    @FXML
    private PasswordField novaSenha;

    @FXML
    private PasswordField confirmaSenha;

    @FXML
    private Button confirmaTroca;

    @FXML
    private StackPane rootPane;

    @FXML
    private AnchorPane animatedBackground;

    @FXML
    public void initialize() {
        if (animatedBackground != null && rootPane != null) {
            BackgroundAnimator.startAnimation(animatedBackground, rootPane);
        }

        // Navegação sequencial por Enter
        FormNavigationUtil.encadearCampos(confirmaTroca, novaSenha, confirmaSenha);
    }

    @FXML
    public void onConfirmaTrocaClick(ActionEvent event) {
        String nova = novaSenha != null ? novaSenha.getText() : null;
        String confirma = confirmaSenha != null ? confirmaSenha.getText() : null;

        if (nova == null || nova.isEmpty() || confirma == null || confirma.isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Atenção", "Preencha todos os campos para alterar a senha.");
            return;
        }

        if (!nova.equals(confirma)) {
            exibirAlerta(Alert.AlertType.ERROR, "Senhas Diferentes", "A nova senha e a confirmação devem ser iguais.");
            return;
        }

        exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Senha alterada com sucesso! Faça login com sua nova credencial.");

        try {
            MainApplication.trocadorDeTelas("login.fxml");
        } catch (IOException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao redirecionar para a tela de login: " + e.getMessage());
        }
    }

    @FXML
    public void onVoltarClick(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("login.fxml");
        } catch (IOException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao voltar para a tela de login: " + e.getMessage());
        }
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
