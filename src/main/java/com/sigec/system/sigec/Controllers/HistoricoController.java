package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.MainApplication;
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
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HistoricoController implements Initializable {

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
    private Button botaoSair;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarDataHora();
        if (usuarioLabel != null) {
            usuarioLabel.setText("Administrador");
        }
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
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
                horaLabel.setText(LocalTime.now().format(horaFormatter));
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
        }
    }

    @FXML
    public void onClickHome(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("home.fxml");
        } catch (IOException e) {
            exibirErro("Erro ao navegar para a tela inicial: " + e.getMessage());
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
            exibirErro("Erro ao navegar para a tela de estoque: " + e.getMessage());
        }
    }

    @FXML
    public void botaoSairAction(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("login.fxml");
        } catch (IOException e) {
            exibirErro("Erro ao sair: " + e.getMessage());
        }
    }

    @FXML
    public void filtrarHistorico(ActionEvent event) {
        String termo = txtpesquisa != null ? txtpesquisa.getText() : "";
        String categoria = filtro != null ? filtro.getValue() : "Todos";
        LocalDate de = dataDE != null ? dataDE.getValue() : null;
        LocalDate ate = dataATE != null ? dataATE.getValue() : null;

        System.out.println("Filtrando histórico: termo=" + termo + ", categoria=" + categoria + ", de=" + de + ", ate=" + ate);
    }

    private void exibirErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
