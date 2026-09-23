package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.MainApplication;
import com.sigec.system.sigec.Services.SessaoService;
import com.sigec.system.sigec.Utils.BackgroundAnimator;
import com.sigec.system.sigec.Utils.ButtonBorderLapAnimator;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controlador da tela Principal (Home/Dashboard) do SIGEC.
 */
public class HomeController implements Initializable {

    @FXML
    private AnchorPane topBarPane;

    @FXML
    private Label dataLabel;

    @FXML
    private Label usuarioLabel;

    @FXML
    private Label horaLabel;

    @FXML
    private ImageView logoSenac;

    @FXML
    private Label logoSigec;

    @FXML
    private TableView<?> tabelaAlertas;

    @FXML
    private TableColumn<?, ?> colProduto;

    @FXML
    private TableColumn<?, ?> colValidade;

    @FXML
    private TableColumn<?, ?> colStatus;

    @FXML
    private TableColumn<?, ?> colQuantidade;

    @FXML
    private Button btnNavCadastro;

    @FXML
    private VBox submenuCadastro;

    @FXML
    private Label setaCadastro;

    @FXML
    private Button botaoSair;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (topBarPane != null) {
            BackgroundAnimator.startTopBarAnimation(topBarPane);
        }
        configurarDataHora();
        configurarUsuario();
    }

    private void configurarUsuario() {
        if (usuarioLabel != null) {
            usuarioLabel.setText(SessaoService.getNomeUsuarioLogado());
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

    public void prepararAnimacao() {
        if (logoSenac != null && logoSigec != null) {
            logoSenac.setOpacity(0);
            logoSigec.setOpacity(0);
        }
    }

    public void iniciarAnimacaoLogos() {
        if (logoSenac != null && logoSigec != null) {
            FadeTransition ft1 = new FadeTransition(Duration.millis(800), logoSenac);
            ft1.setToValue(1);

            FadeTransition ft2 = new FadeTransition(Duration.millis(800), logoSigec);
            ft2.setToValue(1);

            ft1.play();
            ft2.play();
        }
    }

    // =========================================================================
    // NAVEGAÇÃO
    // =========================================================================

    @FXML
    public void botaoEstoqueAction(ActionEvent event) {
        navegarParaEstoque();
    }

    @FXML
    public void onClickEstoque(ActionEvent event) {
        navegarParaEstoque();
    }

    private void navegarParaEstoque() {
        try {
            MainApplication.trocadorDeTelas("lista-estoque.fxml");
        } catch (IOException e) {
            exibirErroNavegacao("Estoque", e);
        }
    }

    @FXML
    public void botaoRelatorio(ActionEvent event) {
        navegarParaRelatorio();
    }

    @FXML
    public void onClickRelatorio(ActionEvent event) {
        navegarParaRelatorio();
    }

    private void navegarParaRelatorio() {
        try {
            MainApplication.trocadorDeTelas("historico.fxml");
        } catch (IOException e) {
            exibirErroNavegacao("Relatório", e);
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
        navegarParaCadastro();
    }

    @FXML
    public void onClickCadastroTurma(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cadastro de Turma");
        alert.setHeaderText("Módulo em Desenvolvimento");
        alert.setContentText("A funcionalidade de Cadastro de Turma será disponibilizada em breve.");
        alert.showAndWait();
    }

    @FXML
    public void onClickCadastro(ActionEvent event) {
        toggleDropdownCadastro(event);
    }

    @FXML
    public void botaoCadastroAction(ActionEvent event) {
        toggleDropdownCadastro(event);
    }

    private void navegarParaCadastro() {
        try {
            MainApplication.trocadorDeTelas("cadastro.fxml");
        } catch (IOException e) {
            exibirErroNavegacao("Cadastro de Usuário", e);
        }
    }

    @FXML
    public void botaoSairAction(ActionEvent event) {
        try {
            SessaoService.encerrarSessao();
            MainApplication.trocadorDeTelas("login.fxml");
        } catch (IOException e) {
            exibirErroNavegacao("Login", e);
        }
    }

    private void exibirErroNavegacao(String destino, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro de Navegação");
        alert.setHeaderText(null);
        alert.setContentText("Não foi possível carregar a tela de " + destino + ": " + e.getMessage());
        alert.showAndWait();
    }
}
