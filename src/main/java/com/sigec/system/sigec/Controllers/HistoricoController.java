package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.MainApplication;
import com.sigec.system.sigec.Services.SessaoService;
import com.sigec.system.sigec.Utils.BackgroundAnimator;
import com.sigec.system.sigec.Utils.ButtonBorderLapAnimator;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
 * Controlador da tela de Histórico e Auditoria de Movimentações.
 */
public class HistoricoController implements Initializable {

    @FXML
    private AnchorPane topBarPane;

    @FXML
    private Label dataLabel;

    @FXML
    private Label usuarioLabel;

    @FXML
    private Label horaLabel;

    @FXML
    private TextField txtpesquisa;

    @FXML
    private Button btnbuscardata;

    @FXML
    private DatePicker dataDE;

    @FXML
    private DatePicker dataATE;

    @FXML
    private ChoiceBox<String> filtro;

    @FXML
    private TableView<?> tableHistorico;

    @FXML
    private TableColumn<?, ?> data_hora;

    @FXML
    private TableColumn<?, ?> nome_produto;

    @FXML
    private TableColumn<?, ?> tipo_estoque;

    @FXML
    private TableColumn<?, ?> quantidade;

    @FXML
    private TableColumn<?, ?> tipo_movimentacao;

    @FXML
    private TableColumn<?, ?> nome_usuario;

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
        configurarFiltroCategorias();

        if (txtpesquisa != null) {
            txtpesquisa.setOnAction(this::filtrarHistorico);
        }
    }

    private void configurarUsuario() {
        if (usuarioLabel != null) {
            usuarioLabel.setText(SessaoService.getNomeUsuarioLogado());
        }
    }

    private void configurarFiltroCategorias() {
        if (filtro != null) {
            filtro.setItems(FXCollections.observableArrayList(
                    "Todos",
                    "Entrada",
                    "Saída",
                    "Ajuste",
                    "Descarte"
            ));
            filtro.getSelectionModel().selectFirst();
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
    // NAVEGAÇÃO
    // =========================================================================

    @FXML
    public void onClickHome(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("home.fxml");
        } catch (IOException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao navegar para a tela inicial: " + e.getMessage());
        }
    }

    @FXML
    public void onClickEstoque(ActionEvent event) {
        navegarParaEstoque();
    }

    @FXML
    public void botaoEstoqueAction(ActionEvent event) {
        navegarParaEstoque();
    }

    private void navegarParaEstoque() {
        try {
            MainApplication.trocadorDeTelas("lista-estoque.fxml");
        } catch (IOException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao navegar para a tela de estoque: " + e.getMessage());
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
        try {
            MainApplication.trocadorDeTelas("cadastro-turma.fxml");
        } catch (IOException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível carregar a tela de Cadastro de Turma: " + e.getMessage());
        }
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
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao navegar para a tela de cadastro: " + e.getMessage());
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
    // CONSULTA DO HISTÓRICO
    // =========================================================================

    @FXML
    public void filtrarHistorico(ActionEvent event) {
        String termo = txtpesquisa != null ? txtpesquisa.getText() : "";
        String categoria = filtro != null ? filtro.getValue() : "Todos";
        LocalDate de = dataDE != null ? dataDE.getValue() : null;
        LocalDate ate = dataATE != null ? dataATE.getValue() : null;

        System.out.println("Filtrando histórico: termo=" + termo + ", categoria=" + categoria + ", de=" + de + ", ate=" + ate);
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
