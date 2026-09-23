package com.sigec.system.sigec.Utils;

import com.sigec.system.sigec.MainApplication;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Coordenador de navegação e sincronização visual do menu lateral (sidebar).
 * Responsável por gerenciar os estados ativos dos botões, comportamento de
 * dropdown,
 * animações de seleção e roteamento entre telas do sistema SIGEC.
 */
public final class SidebarNavigationManager {

    private SidebarNavigationManager() {
        // Construtor privado para classe utilitária estática
    }

    /**
     * Sincroniza visualmente o botão da tela atual no menu lateral,
     * disparando a animação da linha laranja contornando o botão em direção à
     * direita,
     * completando na esquerda com espessura de 6px e ativando a classe 'btn-ativo'.
     *
     * @param view Raiz da cena ou componente contendo o painel lateral
     * @param fxml Nome do arquivo FXML correspondente à tela atual
     */
    public static void sincronizarBotaoAtivo(Parent view, String fxml) {
        if (view == null || fxml == null) {
            return;
        }

        Node painelLateral = view.lookup(".painel-lateral");
        if (!(painelLateral instanceof Parent lateralParent)) {
            return;
        }

        configurarMenuDropdown(lateralParent, fxml);

        for (Node node : lateralParent.lookupAll(".btn-padrao")) {
            if (node instanceof Button btn) {
                configurarAcaoNavegacao(btn);
                boolean deveDestacar = isBotaoCorrespondente(btn, fxml);
                if (deveDestacar) {
                    if (!btn.getStyleClass().contains("btn-ativo") && !ButtonBorderLapAnimator.isAnimating(btn)) {
                        ButtonBorderLapAnimator.animarSelecao(btn);
                    }
                } else {
                    btn.getStyleClass().remove("btn-ativo");
                    btn.setStyle(null);
                }
            }
        }

        Node btnSair = lateralParent.lookup(".btn-sair");
        if (btnSair instanceof Button btn) {
            btn.setOnAction(e -> executarLogout());
        }
    }

    /**
     * Configura o comportamento do menu dropdown de Cadastros e seus subitens.
     *
     * @param lateralParent Container do menu lateral
     * @param fxml          Nome do arquivo FXML atual
     */
    public static void configurarMenuDropdown(Parent lateralParent, String fxml) {
        Node btnCadNode = lateralParent.lookup("#btnNavCadastro");
        Node subCadNode = lateralParent.lookup("#submenuCadastro");
        Node setaCadNode = lateralParent.lookup("#setaCadastro");
        Node btnSubUsuarioNode = lateralParent.lookup("#btnSubNavUsuario");
        Node btnSubTurmaNode = lateralParent.lookup("#btnSubNavTurma");

        boolean isCadastroUsuario = "cadastro.fxml".equalsIgnoreCase(fxml);
        boolean isCadastroTurma = "cadastro-turma.fxml".equalsIgnoreCase(fxml);
        boolean isCadastroTela = isCadastroUsuario || isCadastroTurma;

        if (subCadNode != null) {
            subCadNode.setVisible(isCadastroTela);
            subCadNode.setManaged(isCadastroTela);
        }

        if (setaCadNode instanceof Label setaLbl) {
            setaLbl.setText(isCadastroTela ? "▶" : "▼");
        }

        if (btnSubUsuarioNode instanceof Button btnSubUsuario) {
            if (isCadastroUsuario) {
                if (!btnSubUsuario.getStyleClass().contains("btn-sub-ativo")) {
                    btnSubUsuario.getStyleClass().add("btn-sub-ativo");
                }
            } else {
                btnSubUsuario.getStyleClass().remove("btn-sub-ativo");
            }

            btnSubUsuario.setOnAction(e -> navegarComVerificacao("cadastro.fxml"));
        }

        if (btnSubTurmaNode instanceof Button btnSubTurma) {
            if (isCadastroTurma) {
                if (!btnSubTurma.getStyleClass().contains("btn-sub-ativo")) {
                    btnSubTurma.getStyleClass().add("btn-sub-ativo");
                }
            } else {
                btnSubTurma.getStyleClass().remove("btn-sub-ativo");
            }

            btnSubTurma.setOnAction(e -> navegarComVerificacao("cadastro-turma.fxml"));
        }

        boolean isAlreadyInCadastro = ScreenTransitionManager.getCurrentFxml() != null &&
                (ScreenTransitionManager.getCurrentFxml().equalsIgnoreCase("cadastro.fxml") ||
                 ScreenTransitionManager.getCurrentFxml().equalsIgnoreCase("cadastro-turma.fxml"));

        if (btnCadNode instanceof Button btnCad && subCadNode instanceof Pane subPane) {
            if (isCadastroTela) {
                if (!btnCad.getStyleClass().contains("btn-dropdown-ativo")) {
                    btnCad.getStyleClass().add("btn-dropdown-ativo");
                }
                if (!subPane.getStyleClass().contains("submenu-lateral-ativo")) {
                    subPane.getStyleClass().add("submenu-lateral-ativo");
                }
                // Se já estava em uma das telas de cadastro, apenas preserva o menu ativo sem reiniciar a timeline
                if (!isAlreadyInCadastro && !ButtonBorderLapAnimator.isAnimating(btnCad)) {
                    DropdownBorderLapAnimator.animarDropdown(btnCad, subPane);
                }
            } else {
                btnCad.getStyleClass().remove("btn-dropdown-ativo");
                btnCad.setStyle(null);
                subPane.getStyleClass().remove("submenu-lateral-ativo");
                subPane.setStyle(null);
            }

            btnCad.setOnAction(e -> alternarDropdown(btnCad, subPane, setaCadNode));
        }
    }

    /**
     * Alterna a visibilidade e animação do dropdown de cadastros ao ser clicado.
     */
    private static void alternarDropdown(Button btnCad, Pane subPane, Node setaCadNode) {
        boolean novoEstado = !subPane.isVisible();
        subPane.setVisible(novoEstado);
        subPane.setManaged(novoEstado);

        if (setaCadNode instanceof Label setaLbl) {
            setaLbl.setText(novoEstado ? "▶" : "▼");
        }

        if (novoEstado) {
            DropdownBorderLapAnimator.animarDropdown(btnCad, subPane);
        } else {
            ButtonBorderLapAnimator.cancelarAnimacaoAtiva();
            btnCad.getStyleClass().remove("btn-dropdown-ativo");
            btnCad.setStyle(null);
            subPane.getStyleClass().remove("submenu-lateral-ativo");
            subPane.setStyle(null);
        }
    }

    /**
     * Vincula a rota de navegação correspondente a cada botão padrão do menu lateral.
     */
    private static void configurarAcaoNavegacao(Button btn) {
        String id = btn.getId() != null ? btn.getId().toLowerCase() : "";
        String text = btn.getText() != null ? btn.getText().toLowerCase().trim() : "";

        // O botão pai de dropdown é gerenciado exclusivamente por configurarMenuDropdown
        if (id.equals("btnnavcadastro") || id.contains("dropdown")) {
            return;
        }

        String destino = null;
        if (id.contains("home") || text.contains("inicial") || text.contains("home")) {
            destino = "home.fxml";
        } else if (id.contains("estoque") || text.contains("estoque")) {
            destino = "lista-estoque.fxml";
        } else if (id.contains("relatorio") || id.contains("historico") || text.contains("relat") || text.contains("hist")) {
            destino = "historico.fxml";
        }

        if (destino != null) {
            final String targetFxml = destino;
            btn.setOnAction(e -> navegarComVerificacao(targetFxml));
        }
    }

    /**
     * Verifica se o botão corresponde à tela indicada pelo FXML.
     */
    private static boolean isBotaoCorrespondente(Button btn, String fxml) {
        String id = btn.getId() != null ? btn.getId().toLowerCase() : "";
        String text = btn.getText() != null ? btn.getText().toLowerCase().trim() : "";
        String target = fxml.toLowerCase();

        // Botão dropdown pai não deve receber contorno direto de aba única
        if (id.equals("btnnavcadastro")) {
            return false;
        }

        if (target.contains("home")) {
            return id.contains("home") || text.contains("inicial") || text.contains("home");
        }
        if (target.contains("estoque")) {
            return id.contains("estoque") || text.contains("estoque");
        }
        if (target.contains("historico") || target.contains("relatorio")) {
            return id.contains("relatorio") || id.contains("historico") || text.contains("relat") || text.contains("hist");
        }
        return false;
    }

    private static void navegarComVerificacao(String targetFxml) {
        try {
            if (!targetFxml.equals(ScreenTransitionManager.getCurrentFxml()) && !ScreenTransitionManager.isTransitioning()) {
                MainApplication.trocadorDeTelas(targetFxml);
            }
        } catch (IOException ex) {
            exibirAlertaErro("Navegação", "Erro ao carregar a tela: " + targetFxml, ex);
        }
    }

    private static void executarLogout() {
        try {
            ButtonBorderLapAnimator.cancelarAnimacaoAtiva();
            MainApplication.trocadorDeTelas("login.fxml");
        } catch (Exception ex) {
            exibirAlertaErro("Logout", "Erro ao retornar à tela de login", ex);
        }
    }

    private static void exibirModuloEmDesenvolvimento(String modulo) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(modulo);
        alert.setHeaderText("Módulo em Desenvolvimento");
        alert.setContentText("A tela de " + modulo + " será implementada em breve.");
        alert.showAndWait();
    }

    private static void exibirAlertaErro(String titulo, String mensagem, Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem + "\nDetalhes: " + ex.getMessage());
        alert.showAndWait();
    }
}
