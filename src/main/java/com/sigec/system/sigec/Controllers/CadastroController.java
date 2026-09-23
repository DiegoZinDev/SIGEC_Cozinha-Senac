package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.DAOS.UserDAO;
import com.sigec.system.sigec.MainApplication;
import com.sigec.system.sigec.Services.SessaoService;
import com.sigec.system.sigec.Utils.BackgroundAnimator;
import com.sigec.system.sigec.Utils.ButtonBorderLapAnimator;
import com.sigec.system.sigec.Utils.FormNavigationUtil;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controlador responsável pelo cadastro e gerenciamento de novos usuários.
 */
public class CadastroController implements Initializable {

    @FXML
    private AnchorPane topBarPane;

    @FXML
    private Label dataLabel;

    @FXML
    private Label usuarioLabel;

    @FXML
    private Label horaLabel;

    @FXML
    private Button btnNavCadastro;

    @FXML
    private VBox submenuCadastro;

    @FXML
    private Label setaCadastro;

    @FXML
    private Button btnSubNavUsuario;

    @FXML
    private Button btnSubNavTurma;

    @FXML
    private Button botaoSair;

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtAcesso;

    @FXML
    private ChoiceBox<String> acessoSelect;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private PasswordField txtConfirmarSenha;

    @FXML
    private Button btnCadastrar;

    @FXML
    private Button btnLimpar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (topBarPane != null) {
            BackgroundAnimator.startTopBarAnimation(topBarPane);
        }

        configurarDataHora();
        configurarUsuario();
        configurarMenuInicial();
        configurarNavegacaoCampos();
        configurarSeletorAcesso();
    }

    private void configurarUsuario() {
        if (usuarioLabel != null) {
            usuarioLabel.setText(SessaoService.getNomeUsuarioLogado());
        }
    }

    private void configurarMenuInicial() {
        if (submenuCadastro != null) {
            submenuCadastro.setVisible(true);
            submenuCadastro.setManaged(true);
        }
        if (setaCadastro != null) {
            setaCadastro.setText("▶");
        }
    }

    private void configurarNavegacaoCampos() {
        if (txtAcesso != null) {
            FormNavigationUtil.encadearCampos(btnCadastrar, txtNome, txtEmail, txtAcesso, txtSenha, txtConfirmarSenha);
        } else {
            FormNavigationUtil.encadearCampos(btnCadastrar, txtNome, txtEmail, txtSenha, txtConfirmarSenha);
        }
    }

    private void configurarSeletorAcesso() {
        if (acessoSelect != null) {
            acessoSelect.getItems().clear();
            acessoSelect.getItems().addAll("Instrutor", "Gestor");
            acessoSelect.setValue("Instrutor");

            // Sincroniza a largura do menu suspenso com a largura do seletor
            acessoSelect.showingProperty().addListener((obs, wasShowing, isShowing) -> {
                if (isShowing) {
                    Platform.runLater(() -> {
                        double buttonWidth = acessoSelect.getWidth();
                        if (buttonWidth <= 0) {
                            buttonWidth = acessoSelect.getPrefWidth();
                        }
                        if (buttonWidth > 0) {
                            for (Window window : Window.getWindows()) {
                                if (window instanceof PopupWindow popupWindow) {
                                    if (popupWindow.getScene() != null && popupWindow.getScene().getRoot() != null) {
                                        Node root = popupWindow.getScene().getRoot();
                                        if (root.getStyleClass().contains("context-menu") && root instanceof Region region) {
                                            region.setMinWidth(buttonWidth);
                                            region.setPrefWidth(buttonWidth);
                                            region.setMaxWidth(buttonWidth);
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

    private void configurarDataHora() {
        DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        if (dataLabel != null) {
            dataLabel.setText(LocalDate.now().format(dataFormatter));
        }

        if (horaLabel != null) {
            horaLabel.setText(LocalTime.now().format(horaFormatter));
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event ->
                    horaLabel.setText(LocalTime.now().format(horaFormatter))
            ));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }
    }

    // =========================================================================
    // NAVEGAÇÃO DA BARRA LATERAL
    // =========================================================================

    @FXML
    public void onClickHome(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("home.fxml");
        } catch (IOException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao navegar para a Página Inicial: " + e.getMessage());
        }
    }

    @FXML
    public void onClickEstoque(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("lista-estoque.fxml");
        } catch (IOException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao navegar para o Estoque: " + e.getMessage());
        }
    }

    @FXML
    public void onClickRelatorio(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("historico.fxml");
        } catch (IOException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao navegar para o Relatório: " + e.getMessage());
        }
    }

    @FXML
    public void toggleDropdownCadastro(ActionEvent event) {
        if (submenuCadastro != null) {
            boolean expandido = !submenuCadastro.isVisible();
            submenuCadastro.setVisible(expandido);
            submenuCadastro.setManaged(expandido);
            if (setaCadastro != null) {
                setaCadastro.setText(expandido ? "▶" : "▼");
            }
            if (expandido && btnNavCadastro != null) {
                ButtonBorderLapAnimator.animarDropdown(btnNavCadastro, submenuCadastro);
            } else if (!expandido && btnNavCadastro != null) {
                ButtonBorderLapAnimator.cancelarAnimacaoAtiva();
                btnNavCadastro.getStyleClass().remove("btn-dropdown-ativo");
                btnNavCadastro.setStyle(null);
                submenuCadastro.getStyleClass().remove("submenu-lateral-ativo");
                submenuCadastro.setStyle(null);
            }
        }
    }

    @FXML
    public void onClickCadastroUsuario(ActionEvent event) {
        if (txtNome != null) {
            txtNome.requestFocus();
        }
    }

    @FXML
    public void onClickCadastroTurma(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("cadastro-turma.fxml");
        } catch (IOException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar a tela de Cadastro de Turma: " + e.getMessage());
        }
    }

    @FXML
    public void botaoSairAction(ActionEvent event) {
        try {
            SessaoService.encerrarSessao();
            MainApplication.trocadorDeTelas("login.fxml");
        } catch (IOException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao sair: " + e.getMessage());
        }
    }

    // =========================================================================
    // AÇÕES DO FORMULÁRIO DE CADASTRO
    // =========================================================================

    @FXML
    public void onCadastrar(ActionEvent event) {
        String nome = txtNome != null ? txtNome.getText() : null;
        String email = txtEmail != null ? txtEmail.getText() : null;
        String senha = txtSenha != null ? txtSenha.getText() : null;
        String confirmarSenha = txtConfirmarSenha != null ? txtConfirmarSenha.getText() : null;

        if (nome == null || nome.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                senha == null || senha.isEmpty() ||
                confirmarSenha == null || confirmarSenha.isEmpty()) {

            exibirAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha todos os campos obrigatórios para continuar.");
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            exibirAlerta(Alert.AlertType.ERROR, "Senhas não conferem", "A senha e a confirmação de senha devem ser idênticas.");
            return;
        }

        try {
            String nivelAcesso = (acessoSelect != null && acessoSelect.getValue() != null && !acessoSelect.getValue().trim().isEmpty())
                    ? acessoSelect.getValue().trim()
                    : ((txtAcesso != null && !txtAcesso.getText().trim().isEmpty()) ? txtAcesso.getText().trim() : "Instrutor");

            boolean cadastrado = UserDAO.cadastrar(nome.trim(), email.trim(), senha, nivelAcesso);
            if (cadastrado) {
                exibirAlerta(Alert.AlertType.INFORMATION, "Cadastro Realizado", "Usuário \"" + nome.trim() + "\" cadastrado com sucesso no sistema!");
                limparCampos();
            } else {
                exibirAlerta(Alert.AlertType.ERROR, "Erro no Cadastro", "Não foi possível cadastrar. O e-mail informado já pode estar em uso.");
            }
        } catch (SQLException | RuntimeException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Erro ao salvar cadastro: " + e.getMessage());
        }
    }

    @FXML
    public void onLimparCampos(ActionEvent event) {
        limparCampos();
    }

    public void limparCampos() {
        if (txtNome != null) txtNome.clear();
        if (txtEmail != null) txtEmail.clear();
        if (txtSenha != null) txtSenha.clear();
        if (txtConfirmarSenha != null) txtConfirmarSenha.clear();
        if (txtAcesso != null) txtAcesso.clear();
        if (acessoSelect != null) acessoSelect.setValue("Instrutor");

        if (txtNome != null) {
            txtNome.requestFocus();
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
