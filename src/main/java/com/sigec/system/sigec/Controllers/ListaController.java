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
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
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
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.scene.layout.AnchorPane;
import com.sigec.system.sigec.Utils.BackgroundAnimator;

public class ListaController implements Initializable {

    @FXML
    private AnchorPane topBarPane;

    @FXML
    private Label dataLabel;

    @FXML
    private Label usuarioLabel;

    @FXML
    private Label horaLabel;

    @FXML
    private TextField FiltrarProdutos;

    @FXML
    private CheckBox chkPereciveis;

    @FXML
    private CheckBox chkNaoPereciveis;

    @FXML
    private CheckBox chkUtensilios;

    @FXML
    private TableView<?> ListaEstoque;

    @FXML
    private TableColumn<?, ?> nomeProduto;

    @FXML
    private TableColumn<?, ?> tipoProduto;

    @FXML
    private TableColumn<?, ?> quantidadeProduto;

    @FXML
    private TableColumn<?, ?> unidadeProduto;

    @FXML
    private Button editar;

    @FXML
    private Button remover;

    @FXML
    private Button botaoSair;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (topBarPane != null) {
            BackgroundAnimator.startTopBarAnimation(topBarPane);
        }
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
    public void onClickHome(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("home.fxml");
        } catch (IOException e) {
            exibirErro("Erro ao navegar para a tela inicial: " + e.getMessage());
        }
    }

    @FXML
    public void onClickRelatorio(ActionEvent event) {
        navegarParaRelatorio();
    }

    @FXML
    public void botaoRelatorio(ActionEvent event) {
        navegarParaRelatorio();
    }

    private void navegarParaRelatorio() {
        try {
            MainApplication.trocadorDeTelas("historico.fxml");
        } catch (IOException e) {
            exibirErro("Erro ao navegar para a tela de relatórios: " + e.getMessage());
        }
    }

    @FXML
    public void onClickCadastro(ActionEvent event) {
        navegarParaCadastro();
    }

    @FXML
    public void botaoCadastroAction(ActionEvent event) {
        navegarParaCadastro();
    }

    private void navegarParaCadastro() {
        try {
            MainApplication.trocadorDeTelas("cadastro.fxml");
        } catch (IOException e) {
            exibirErro("Erro ao navegar para a tela de cadastro: " + e.getMessage());
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
    public void onPesquisarClick(ActionEvent event) {
        String termo = FiltrarProdutos != null ? FiltrarProdutos.getText() : "";
        System.out.println("Pesquisando produtos com termo: " + termo);
    }

    @FXML
    public void onHelloButtonClick(ActionEvent event) {
        onPesquisarClick(event);
    }

    @FXML
    public void onClickAddProduto(ActionEvent event) {
        try {
            MainApplication.abrirPopUp("CadastroProduto.fxml");
        } catch (IOException e) {
            exibirErro("Erro ao abrir modal de cadastro de produto: " + e.getMessage());
        }
    }

    @FXML
    public void onEditarProduto(ActionEvent event) {
        try {
            MainApplication.abrirPopUp("edicaoProdutos.fxml");
        } catch (IOException e) {
            exibirErro("Erro ao abrir modal de edição de produto: " + e.getMessage());
        }
    }

    @FXML
    public void onRemoverProdutoClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText("Exclusão de Produto");
        alert.setContentText("Deseja realmente remover o produto selecionado?");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setTitle("Sucesso");
            info.setHeaderText(null);
            info.setContentText("Produto removido com sucesso!");
            info.showAndWait();
        }
    }

    private void exibirErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
