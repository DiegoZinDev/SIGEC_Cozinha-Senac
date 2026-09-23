package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.Utils.FormNavigationUtil;

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

/**
 * Controlador do modal/janela de edição e atualização de produtos do estoque.
 */
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
        configurarSeletores();
        FormNavigationUtil.encadearCampos(btnCadastro, txtNomeProduto, txtQtdAtual, txtEstoqueMinimo);
    }

    private void configurarSeletores() {
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
        String nome = txtNomeProduto != null ? txtNomeProduto.getText() : null;
        String qtd = txtQtdAtual != null ? txtQtdAtual.getText() : null;
        String min = txtEstoqueMinimo != null ? txtEstoqueMinimo.getText() : null;

        if (nome == null || nome.trim().isEmpty() ||
                qtd == null || qtd.trim().isEmpty() ||
                min == null || min.trim().isEmpty()) {

            exibirAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Preencha todos os campos obrigatórios para editar.");
            return;
        }

        try {
            Double.parseDouble(qtd.trim());
            Double.parseDouble(min.trim());
        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Formato Inválido", "Quantidade e estoque mínimo devem ser valores numéricos válidos.");
            return;
        }

        exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto '" + nome.trim() + "' atualizado com sucesso!");
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

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
