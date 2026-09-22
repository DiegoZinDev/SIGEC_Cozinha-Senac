package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.DAOS.UserDAO;
import com.sigec.system.sigec.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import com.sigec.system.sigec.Utils.BackgroundAnimator;

import java.io.IOException;
import java.sql.SQLException;

public class CadastroController {

    @FXML
    private AnchorPane topBarPane;

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtAcesso;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private PasswordField txtConfirmarSenha;

    @FXML
    private Button btnCadastrar;

    @FXML
    private ChoiceBox<String> acessoSelect;

    @FXML
    public void initialize() {
        if (topBarPane != null) {
            BackgroundAnimator.startTopBarAnimation(topBarPane);
        }

        // Enter navega ordenadamente pelos campos e no último campo aciona o cadastro
        if (txtAcesso != null) {
            com.sigec.system.sigec.Utils.FormNavigationUtil.encadearCampos(btnCadastrar, txtNome, txtEmail, txtAcesso, txtSenha, txtConfirmarSenha);
        } else {
            com.sigec.system.sigec.Utils.FormNavigationUtil.encadearCampos(btnCadastrar, txtNome, txtEmail, txtSenha, txtConfirmarSenha);
        }

        if (acessoSelect != null) {
            acessoSelect.getItems().clear();
            acessoSelect.getItems().addAll("Instrutor", "Gestor");
            acessoSelect.setValue("Instrutor");

            // Sincroniza a largura do menu suspenso (caixa de opções) com a largura do botão onde o usuário clica
            acessoSelect.showingProperty().addListener((obs, wasShowing, isShowing) -> {
                if (isShowing) {
                    javafx.application.Platform.runLater(() -> {
                        double buttonWidth = acessoSelect.getWidth();
                        if (buttonWidth <= 0) {
                            buttonWidth = acessoSelect.getPrefWidth();
                        }
                        if (buttonWidth > 0) {
                            for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                                if (window instanceof javafx.stage.PopupWindow popupWindow) {
                                    if (popupWindow.getScene() != null && popupWindow.getScene().getRoot() != null) {
                                        javafx.scene.Node root = popupWindow.getScene().getRoot();
                                        if (root.getStyleClass().contains("context-menu")) {
                                            if (root instanceof javafx.scene.layout.Region region) {
                                                region.setMinWidth(buttonWidth);
                                                region.setPrefWidth(buttonWidth);
                                                region.setMaxWidth(buttonWidth);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    @FXML
    public void onCadastrar(ActionEvent event) {
        String nome = txtNome.getText();
        String email = txtEmail.getText();
        String senha = txtSenha.getText();
        String confirmarSenha = txtConfirmarSenha.getText();

        if (nome == null || nome.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                senha == null || senha.isEmpty() ||
                confirmarSenha == null || confirmarSenha.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos Obrigatórios");
            alert.setHeaderText(null);
            alert.setContentText("Preencha todos os campos para continuar.");
            alert.showAndWait();
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Senhas não conferem");
            alert.setHeaderText(null);
            alert.setContentText("A senha e a confirmação de senha devem ser idênticas.");
            alert.showAndWait();
            return;
        }

        try {
            String nivelAcesso = (acessoSelect != null && acessoSelect.getValue() != null && !acessoSelect.getValue().trim().isEmpty())
                    ? acessoSelect.getValue().trim()
                    : ((txtAcesso != null && !txtAcesso.getText().trim().isEmpty()) ? txtAcesso.getText().trim() : "Instrutor");
            boolean cadastrado = UserDAO.cadastrar(nome.trim(), email.trim(), senha, nivelAcesso);
            if (cadastrado) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("Usuário cadastrado com sucesso!");
                alert.showAndWait();

                MainApplication.trocadorDeTelas("login.fxml");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro no Cadastro");
                alert.setHeaderText(null);
                alert.setContentText("Não foi possível cadastrar. O e-mail informado pode já estar em uso.");
                alert.showAndWait();
            }
        } catch (SQLException | RuntimeException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Banco de Dados");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao salvar cadastro: " + e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Navegação");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao redirecionar para a tela de login: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void voltarTela(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("home.fxml");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Navegação");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao voltar para o login: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
