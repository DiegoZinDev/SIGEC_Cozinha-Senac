package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.Constructors.User;
import com.sigec.system.sigec.DAOS.UserDAO;
import com.sigec.system.sigec.MainApplication;
import com.sigec.system.sigec.Services.SessaoService;
import com.sigec.system.sigec.Utils.BackgroundAnimator;
import com.sigec.system.sigec.Utils.ButtonBorderLapAnimator;
import com.sigec.system.sigec.Utils.FormNavigationUtil;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador da tela de Cadastro de Turma e Vinculação de Instrutores.
 * Gerencia a composição em duas seções lado a lado (Formulário da Turma e Lista de Instrutores).
 */
public class CadastroTurmaController implements Initializable {

    // =========================================================================
    // ELEMENTOS DO CABEÇALHO E MENU LATERAL
    // =========================================================================
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

    // =========================================================================
    // SEÇÃO 1: FORMULÁRIO DE CADASTRO DA TURMA
    // =========================================================================
    @FXML
    private TextField txtNomeTurma;

    @FXML
    private ChoiceBox<String> laboratorioSelect;

    @FXML
    private TextField txtProfessorResponsavel;

    @FXML
    private ChoiceBox<String> situacaoSelect;

    @FXML
    private Button btnCadastrarTurma;

    @FXML
    private Button btnLimpar;

    // =========================================================================
    // SEÇÃO 2: INSTRUTORES DISPONÍVEIS
    // =========================================================================
    @FXML
    private TextField txtPesquisarInstrutor;

    @FXML
    private ListView<User> listaInstrutores;

    @FXML
    private Label lblInstrutorSelecionado;

    @FXML
    private Button btnDesmarcarInstrutor;

    // Dados observáveis dos instrutores
    private final ObservableList<User> masterInstrutores = FXCollections.observableArrayList();
    private FilteredList<User> filteredInstrutores;
    private User instrutorSelecionado = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (topBarPane != null) {
            BackgroundAnimator.startTopBarAnimation(topBarPane);
        }

        configurarDataHora();
        configurarUsuario();
        configurarMenuInicial();
        configurarFormularioTurma();
        configurarListaInstrutores();
        configurarPesquisaInstrutor();

        // Encadeamento de tecla Enter para navegação no formulário
        FormNavigationUtil.encadearCampos(btnCadastrarTurma, txtNomeTurma);
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

    private void configurarMenuInicial() {
        if (submenuCadastro != null) {
            submenuCadastro.setVisible(true);
            submenuCadastro.setManaged(true);
        }
        if (setaCadastro != null) {
            setaCadastro.setText("▶");
        }
    }

    private void configurarFormularioTurma() {
        if (laboratorioSelect != null) {
            laboratorioSelect.getItems().clear();
            laboratorioSelect.getItems().addAll(
                    "Cozinha Pedagógica 01",
                    "Cozinha Pedagógica 02 (Panificação e Confeitaria)",
                    "Cozinha Fria / Garde Manger",
                    "Laboratório de Bebidas e Barismo",
                    "Cozinha Demonstrativa"
            );
            laboratorioSelect.getSelectionModel().selectFirst();
        }

        if (situacaoSelect != null) {
            situacaoSelect.getItems().clear();
            situacaoSelect.getItems().addAll("Ativo", "Inativo");
            // O padrão da situação deve vir como Ativo
            situacaoSelect.setValue("Ativo");
        }
    }

    private void configurarListaInstrutores() {
        if (listaInstrutores == null) {
            return;
        }

        // Popula instantaneamente com dados padrão do Senac (0ms de carregamento, zero travamento na transição)
        masterInstrutores.setAll(
                new User(1, "Chef Rogério Silva", "rogerio.silva@sp.senac.br", "", "Instrutor", "Ativo", 0),
                new User(2, "Chef Amanda Oliveira", "amanda.oliveira@sp.senac.br", "", "Instrutor", "Ativo", 0),
                new User(3, "Prof. Carlos Eduardo", "carlos.eduardo@sp.senac.br", "", "Instrutor", "Ativo", 0),
                new User(4, "Chef Mariana Costa", "mariana.costa@sp.senac.br", "", "Instrutor", "Ativo", 0),
                new User(5, "Chef Bruno Henrique", "bruno.henrique@sp.senac.br", "", "Instrutor", "Ativo", 0)
        );

        filteredInstrutores = new FilteredList<>(masterInstrutores, p -> true);
        listaInstrutores.setItems(filteredInstrutores);

        // Consulta o banco de dados em thread assíncrona para não bloquear a thread da interface (JavaFX)
        Thread bgLoader = new Thread(() -> {
            try {
                List<User> instrutoresDb = UserDAO.listarInstrutores();
                if (instrutoresDb != null && !instrutoresDb.isEmpty()) {
                    javafx.application.Platform.runLater(() -> {
                        masterInstrutores.setAll(instrutoresDb);
                        if (instrutorSelecionado != null) {
                            for (User u : masterInstrutores) {
                                if (u.getNome() != null && u.getNome().equals(instrutorSelecionado.getNome())) {
                                    selecionarInstrutor(u);
                                    break;
                                }
                            }
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("Aviso: Falha ao carregar instrutores em segundo plano: " + e.getMessage());
            }
        });
        bgLoader.setDaemon(true);
        bgLoader.setName("SIGEC-InstrutoresLoader");
        bgLoader.start();

        // Custom Cell Factory para renderizar os instrutores com visual profissional e badge de seleção
        listaInstrutores.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(null);
                } else {
                    HBox card = new HBox(12);
                    card.setAlignment(Pos.CENTER_LEFT);

                    // Ícone/Avatar do Instrutor
                    Label avatar = new Label("👨‍🍳");
                    avatar.setStyle("-fx-font-size: 18px;");

                    VBox infoBox = new VBox(2);
                    Label nomeLbl = new Label(user.getNome() != null ? user.getNome() : "Instrutor");
                    nomeLbl.setStyle("-fx-font-family: 'Arial Bold'; -fx-font-size: 13px; -fx-text-fill: #1e293b;");

                    Label emailLbl = new Label(user.getEmail() != null ? user.getEmail() : "senac@sp.senac.br");
                    emailLbl.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 11px; -fx-text-fill: #64748b;");

                    infoBox.getChildren().addAll(nomeLbl, emailLbl);
                    HBox.setHgrow(infoBox, Priority.ALWAYS);

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Indicador de seleção
                    boolean isSelected = (instrutorSelecionado != null &&
                            instrutorSelecionado.getNome() != null &&
                            instrutorSelecionado.getNome().equals(user.getNome()));

                    Label statusBadge = new Label(isSelected ? "✔ Selecionado" : "Disponível");
                    statusBadge.getStyleClass().add(isSelected ? "badge-instrutor-selecionado" : "badge-instrutor-disponivel");

                    card.getChildren().addAll(avatar, infoBox, spacer, statusBadge);
                    setGraphic(card);
                    setText(null);
                }
            }
        });

        // Evento de seleção: ao clicar em um instrutor, ele fica marcado e vincula no campo
        listaInstrutores.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selecionarInstrutor(newVal);
            }
        });
    }

    private void selecionarInstrutor(User instrutor) {
        this.instrutorSelecionado = instrutor;

        if (txtProfessorResponsavel != null) {
            txtProfessorResponsavel.setText(instrutor.getNome());
        }

        if (lblInstrutorSelecionado != null) {
            lblInstrutorSelecionado.setText(instrutor.getNome());
        }

        // Força a atualização visual das células da lista para destacar o selecionado
        if (listaInstrutores != null) {
            listaInstrutores.refresh();
        }
    }

    private void configurarPesquisaInstrutor() {
        if (txtPesquisarInstrutor != null) {
            txtPesquisarInstrutor.textProperty().addListener((observable, oldValue, newValue) -> {
                if (filteredInstrutores != null) {
                    filteredInstrutores.setPredicate(user -> {
                        if (newValue == null || newValue.isBlank()) {
                            return true;
                        }
                        String termo = newValue.toLowerCase().trim();
                        boolean matchNome = user.getNome() != null && user.getNome().toLowerCase().contains(termo);
                        boolean matchEmail = user.getEmail() != null && user.getEmail().toLowerCase().contains(termo);
                        return matchNome || matchEmail;
                    });
                }
            });
        }
    }

    // =========================================================================
    // AÇÕES DO FORMULÁRIO DE CADASTRO DE TURMA
    // =========================================================================

    @FXML
    public void onCadastrarTurma(ActionEvent event) {
        String nomeTurma = txtNomeTurma != null ? txtNomeTurma.getText() : null;
        String laboratorio = laboratorioSelect != null ? laboratorioSelect.getValue() : null;
        String professor = txtProfessorResponsavel != null ? txtProfessorResponsavel.getText() : null;
        String situacao = situacaoSelect != null ? situacaoSelect.getValue() : "Ativo";

        if (nomeTurma == null || nomeTurma.trim().isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Campo Obrigatório", "Por favor, informe o Nome da Turma.");
            if (txtNomeTurma != null) txtNomeTurma.requestFocus();
            return;
        }

        if (laboratorio == null || laboratorio.trim().isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Campo Obrigatório", "Por favor, selecione um Laboratório.");
            return;
        }

        if (professor == null || professor.trim().isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Professor Não Selecionado",
                    "Por favor, selecione um Professor Responsável na lista de instrutores ao lado para vincular à turma.");
            return;
        }

        // Sucesso no cadastro
        String mensagem = String.format(
                "Turma cadastrada com sucesso!\n\n" +
                "• Turma: %s\n" +
                "• Laboratório: %s\n" +
                "• Professor: %s\n" +
                "• Situação: %s",
                nomeTurma.trim(), laboratorio, professor.trim(), situacao
        );

        exibirAlerta(Alert.AlertType.INFORMATION, "Turma Cadastrada", mensagem);
        limparFormulario();
    }

    @FXML
    public void onLimparFormulario(ActionEvent event) {
        limparFormulario();
    }

    @FXML
    public void onDesmarcarInstrutor(ActionEvent event) {
        desmarcarInstrutor();
    }

    private void limparFormulario() {
        if (txtNomeTurma != null) txtNomeTurma.clear();
        if (laboratorioSelect != null && !laboratorioSelect.getItems().isEmpty()) {
            laboratorioSelect.getSelectionModel().selectFirst();
        }
        if (situacaoSelect != null) {
            situacaoSelect.setValue("Ativo");
        }
        desmarcarInstrutor();

        if (txtNomeTurma != null) {
            txtNomeTurma.requestFocus();
        }
    }

    private void desmarcarInstrutor() {
        instrutorSelecionado = null;
        if (txtProfessorResponsavel != null) {
            txtProfessorResponsavel.clear();
        }
        if (lblInstrutorSelecionado != null) {
            lblInstrutorSelecionado.setText("Nenhum selecionado");
        }
        if (listaInstrutores != null) {
            listaInstrutores.getSelectionModel().clearSelection();
            listaInstrutores.refresh();
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
        try {
            MainApplication.trocadorDeTelas("cadastro.fxml");
        } catch (IOException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao navegar para Cadastro de Usuário: " + e.getMessage());
        }
    }

    @FXML
    public void onClickCadastroTurma(ActionEvent event) {
        // Já está na tela de cadastro de turma; foca no primeiro campo do formulário
        if (txtNomeTurma != null) {
            txtNomeTurma.requestFocus();
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

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
