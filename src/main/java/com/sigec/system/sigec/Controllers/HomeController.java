package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.MainApplication;
import javafx.animation.Animation;
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
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private Label dataLabel;

    @FXML
    private Label usuarioLabel;

    @FXML
    private Label horaLabel;

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
    private Button botaoSair;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarDataHora();
        if (usuarioLabel != null) {
            usuarioLabel.setText("Administrador");
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
    public void botaoSairAction(ActionEvent event) {
        try {
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
