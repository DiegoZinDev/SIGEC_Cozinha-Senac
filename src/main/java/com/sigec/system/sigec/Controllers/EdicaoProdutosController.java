package com.sigec.system.sigec.Controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EdicaoProdutosController implements Initializable {

    @FXML
    private TextField txtNomeProduto;

    @FXML
    private TextField txtQtdAtual;

    @FXML
    private TextField txtEstoqueMinimo;

    @FXML
    private ChoiceBox<String> txtTipoProduto;

    @FXML
    private ChoiceBox<String> txtUnidadeDeMedida;

    @FXML
    private DatePicker txtDataValidade;

    @FXML
    private Button btnVoltar;

    @FXML
    private Button btnCadastro;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (txtTipoProduto != null) {
            txtTipoProduto.setItems(FXCollections.observableArrayList(
                    "Perecível",
                    "Não Perecível",
                    "Utensílio"
            ));
            txtTipoProduto.getSelectionModel().selectFirst();
        }

        if (txtUnidadeDeMedida != null) {
            txtUnidadeDeMedida.setItems(FXCollections.observableArrayList(
                    "KG",
                    "G",
                    "L",
                    "ML",
                    "UN",
                    "PCT",
                    "CX"
            ));
            txtUnidadeDeMedida.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void onEditar(ActionEvent event) {
        String nome = txtNomeProduto.getText();
        String qtd = txtQtdAtual.getText();
        String min = txtEstoqueMinimo.getText();

        if (nome == null || nome.trim().isEmpty() ||
            qtd == null || qtd.trim().isEmpty() ||
            min == null || min.trim().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos Obrigatórios");
            alert.setHeaderText(null);
            alert.setContentText("Preencha todos os campos obrigatórios para editar.");
            alert.showAndWait();
            return;
        }

        try {
            Double.parseDouble(qtd.trim());
            Double.parseDouble(min.trim());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Formato Inválido");
            alert.setHeaderText(null);
            alert.setContentText("Quantidade e estoque mínimo devem ser valores numéricos.");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText("Produto '" + nome + "' atualizado com sucesso!");
        alert.showAndWait();

        fecharJanela();
    }

    @FXML
    public void voltarTela(ActionEvent event) {
        fecharJanela();
    }

    private void fecharJanela() {
        if (btnVoltar != null && btnVoltar.getScene() != null && btnVoltar.getScene().getWindow() != null) {
            ((Stage) btnVoltar.getScene().getWindow()).close();
        } else if (btnCadastro != null && btnCadastro.getScene() != null && btnCadastro.getScene().getWindow() != null) {
            ((Stage) btnCadastro.getScene().getWindow()).close();
        }
    }
}
