package com.sigec.system.sigec.Utils;

import com.sigec.system.sigec.MainApplication;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Gerenciador de transições de tela com animação em cascata (stagger),
 * direcionalidade intuitiva (Forward/Backward) e desaceleração suave (Silk
 * easing),
 * baseado nas diretrizes de UI Motion e Design Engineering.
 */
public class ScreenTransitionManager {

    public enum Direction {
        FORWARD,
        BACKWARD,
        AUTO
    }

    private static final Deque<String> history = new ArrayDeque<>();
    private static String currentFxml = "login.fxml";
    private static boolean isTransitioning = false;

    // Ordem das abas principais para navegação intuitiva
    private static final Map<String, Integer> TAB_ORDER = Map.of(
            "home.fxml", 0,
            "lista-estoque.fxml", 1,
            "historico.fxml", 2);

    // Parâmetros de animação para transições de tela cheia (Login <-> Home, etc.)
    private static final double FULL_MOTION_DISTANCE = 55.0;
    private static final double FULL_STAGGER_INTERVAL_MS = 45.0;
    private static final double FULL_MAX_STAGGER_MS = 220.0;
    private static final double FULL_EXIT_DURATION_MS = 300.0;
    private static final double FULL_ENTER_DURATION_MS = 350.0;

    // Parâmetros de animação interna do quadrado de conteúdo (#conteudoPagina) - Rápida, suave e ágil
    private static final double CONTENT_MOTION_DISTANCE = 20.0;
    private static final double CONTENT_STAGGER_INTERVAL_MS = 15.0;
    private static final double CONTENT_MAX_STAGGER_MS = 45.0;
    private static final double CONTENT_EXIT_DURATION_MS = 100.0;
    private static final double CONTENT_ENTER_DURATION_MS = 140.0;

    // Curva cúbica suave para desaceleração natural (Silk Ease-Out)
    private static final Interpolator EASE_OUT_SILK = Interpolator.SPLINE(0.1, 0.9, 0.2, 1.0);
    // Curva cúbica para saída (Silk Ease-In)
    private static final Interpolator EASE_IN_SILK = Interpolator.SPLINE(0.4, 0.0, 0.2, 1.0);

    private static Animation activeAnimation = null;
    private static Runnable activeAnimationCancelAction = null;

    public static void trocarTela(StackPane rootContainer, String fxml, Direction requestedDirection)
            throws IOException {

        if (fxml == null) {
            return;
        }

        // Se já estamos na tela de destino e não há transição ativa, ignora
        if (fxml.equals(currentFxml) && !rootContainer.getChildren().isEmpty() && activeAnimation == null) {
            return;
        }

        // Se houver uma animação em andamento, cancela e conclui imediatamente para permitir navegação rápida e ágil
        if (activeAnimation != null && activeAnimation.getStatus() == Animation.Status.RUNNING) {
            activeAnimation.stop();
            if (activeAnimationCancelAction != null) {
                activeAnimationCancelAction.run();
            }
            activeAnimation = null;
            activeAnimationCancelAction = null;
            isTransitioning = false;
        }

        if (fxml.equals(currentFxml) && !rootContainer.getChildren().isEmpty()) {
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxml));
        Parent newView = fxmlLoader.load();
        sincronizarBotaoAtivo(newView, fxml);

        if (rootContainer.getChildren().isEmpty()) {
            rootContainer.getChildren().add(newView);
            currentFxml = fxml;
            return;
        }

        Parent oldView = (Parent) rootContainer.getChildren().get(rootContainer.getChildren().size() - 1);

        Direction effectiveDirection = resolveDirection(fxml, requestedDirection);
        updateHistory(fxml, effectiveDirection);

        boolean isInternal = isInternalContentTransition(oldView, newView);
        isTransitioning = true;

        // Feedback visual imediato no menu lateral (0ms de latência)
        sincronizarBotaoAtivo(oldView, fxml);

        // Bloqueia a raiz apenas em transições de tela inteira (ex: Login <-> Home).
        // Em transições internas de abas, o menu lateral e barras NUNCA são desabilitados,
        // eliminando qualquer escurecimento (opacidade 0.4 do JavaFX) e bloqueio de cliques.
        if (!isInternal) {
            rootContainer.setDisable(true);
        }

        try {
            if (isInternal) {
                executarTransicaoConteudo(rootContainer, oldView, newView, fxml, effectiveDirection, () -> {
                    currentFxml = fxml;
                    sincronizarBotaoAtivo(newView, fxml);
                    isTransitioning = false;
                });
            } else {
                executarTransicaoCascata(rootContainer, oldView, newView, effectiveDirection, () -> {
                    currentFxml = fxml;
                    sincronizarBotaoAtivo(newView, fxml);
                    isTransitioning = false;
                    rootContainer.setDisable(false);
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            rootContainer.getChildren().setAll(newView);
            currentFxml = fxml;
            sincronizarBotaoAtivo(newView, fxml);
            isTransitioning = false;
            rootContainer.setDisable(false);
        }
    }

    private static Direction resolveDirection(String targetFxml, Direction requestedDirection) {
        if (requestedDirection != null && requestedDirection != Direction.AUTO) {
            return requestedDirection;
        }

        // Navegação entre abas principais
        if (TAB_ORDER.containsKey(currentFxml) && TAB_ORDER.containsKey(targetFxml)) {
            int currentIndex = TAB_ORDER.get(currentFxml);
            int targetIndex = TAB_ORDER.get(targetFxml);
            return targetIndex >= currentIndex ? Direction.FORWARD : Direction.BACKWARD;
        }

        // Retorno para login é sempre BACKWARD
        if ("login.fxml".equals(targetFxml) && !"login.fxml".equals(currentFxml)) {
            return Direction.BACKWARD;
        }

        // Se a tela de destino já estiver no histórico, estamos voltando
        if (history.contains(targetFxml)) {
            return Direction.BACKWARD;
        }

        // Fluxos de avanço padrão (Login -> Cadastro, Login -> Esqueceu, etc.)
        return Direction.FORWARD;
    }

    private static void updateHistory(String targetFxml, Direction direction) {
        if (direction == Direction.BACKWARD) {
            // Remove do histórico até a tela de destino
            while (!history.isEmpty() && !history.peek().equals(targetFxml)) {
                history.pop();
            }
        } else {
            if (currentFxml != null && !currentFxml.equals(targetFxml)) {
                history.push(currentFxml);
            }
        }
    }

    private static boolean isInternalContentTransition(Parent oldView, Parent newView) {
        if (oldView == null || newView == null) {
            return false;
        }
        Node oldContent = oldView.lookup("#conteudoPagina");
        Node newContent = newView.lookup("#conteudoPagina");
        return oldContent != null && newContent != null;
    }

    /**
     * Executa a animação Silk Stagger exclusivamente dentro do quadrado de conteúdo da página (#conteudoPagina).
     * O topo, menu lateral e a barra do administrador permanecem 100% fixos e sem recarregamento.
     */
    private static void executarTransicaoConteudo(
            StackPane rootContainer,
            Parent oldView,
            Parent newView,
            String targetFxml,
            Direction direction,
            Runnable onFinishedCallback) {

        Node oldContentNode = oldView.lookup("#conteudoPagina");
        Node newContentNode = newView.lookup("#conteudoPagina");

        if (!(oldContentNode instanceof Parent oldContent) || !(newContentNode instanceof Parent newContent)) {
            executarTransicaoCascata(rootContainer, oldView, newView, direction, onFinishedCallback);
            return;
        }

        // Atualiza imediatamente o destaque no menu lateral de oldView e newView
        sincronizarBotaoAtivo(oldView, targetFxml);
        sincronizarBotaoAtivo(newView, targetFxml);

        // Desabilita temporariamente APENAS o conteúdo antigo saindo (o menu lateral permanece 100% ativo e opaco)
        oldContent.setDisable(true);

        double exitTranslateX = (direction == Direction.FORWARD) ? -CONTENT_MOTION_DISTANCE : CONTENT_MOTION_DISTANCE;
        double enterTranslateX = (direction == Direction.FORWARD) ? CONTENT_MOTION_DISTANCE : -CONTENT_MOTION_DISTANCE;

        // Aplica recorte delimitado (clip) para evitar vazamento sobre o menu lateral ou barras
        aplicarClipDelimitado(oldContent);
        aplicarClipDelimitado(newContent);

        List<Node> exitNodes = extractContentChildren(oldContent);

        // Timeline de Saída exclusivamente dos nós internos de conteúdo
        Timeline exitTimeline = new Timeline();

        for (int i = 0; i < exitNodes.size(); i++) {
            Node node = exitNodes.get(i);
            double delay = Math.min(i * CONTENT_STAGGER_INTERVAL_MS, CONTENT_MAX_STAGGER_MS);

            KeyFrame startKf = new KeyFrame(
                    Duration.millis(delay),
                    new KeyValue(node.translateXProperty(), node.getTranslateX()),
                    new KeyValue(node.opacityProperty(), node.getOpacity()));

            KeyFrame endKf = new KeyFrame(
                    Duration.millis(delay + CONTENT_EXIT_DURATION_MS),
                    new KeyValue(node.translateXProperty(), exitTranslateX, EASE_IN_SILK),
                    new KeyValue(node.opacityProperty(), 0.0, EASE_IN_SILK));

            exitTimeline.getKeyFrames().addAll(startKf, endKf);
        }

        activeAnimation = exitTimeline;
        activeAnimationCancelAction = () -> {
            for (Node node : exitNodes) {
                node.setTranslateX(0.0);
                node.setOpacity(1.0);
            }
            oldContent.setDisable(false);
            if (oldView instanceof BorderPane oldBp && newView instanceof BorderPane newBp) {
                if (oldBp.getTop() != null) {
                    Node existingTop = oldBp.getTop();
                    oldBp.setTop(null);
                    newBp.setTop(existingTop);
                }
                if (oldBp.getLeft() != null) {
                    Node existingLeft = oldBp.getLeft();
                    oldBp.setLeft(null);
                    newBp.setLeft(existingLeft);
                }
            }
            rootContainer.getChildren().setAll(newView);
            sincronizarBotaoAtivo(newView, targetFxml);
        };

        exitTimeline.setOnFinished(e -> {
            try {
                // Reseta os nós da tela anterior para consistência
                for (Node node : exitNodes) {
                    node.setTranslateX(0.0);
                    node.setOpacity(1.0);
                }
                oldContent.setDisable(false);

                // Preserva o topBarPane e o menu lateral já ativos de oldView para continuidade total sem piscar nem interromper animações
                if (oldView instanceof BorderPane oldBp && newView instanceof BorderPane newBp) {
                    if (oldBp.getTop() != null) {
                        Node existingTop = oldBp.getTop();
                        oldBp.setTop(null);
                        newBp.setTop(existingTop);
                    }
                    if (oldBp.getLeft() != null) {
                        Node existingLeft = oldBp.getLeft();
                        oldBp.setLeft(null);
                        newBp.setLeft(existingLeft);
                    }
                }

                // Troca a raiz para a nova tela: o topo, painel lateral e barra secundária coincidem pixel a pixel
                rootContainer.getChildren().setAll(newView);
                sincronizarBotaoAtivo(newView, targetFxml);

                List<Node> enterNodes = extractContentChildren(newContent);
                for (Node node : enterNodes) {
                    node.setTranslateX(enterTranslateX);
                    node.setOpacity(0.0);
                }

                // Timeline de Entrada exclusivamente dos nós internos de conteúdo
                Timeline enterTimeline = new Timeline();

                for (int i = 0; i < enterNodes.size(); i++) {
                    Node node = enterNodes.get(i);
                    double delay = Math.min(i * CONTENT_STAGGER_INTERVAL_MS, CONTENT_MAX_STAGGER_MS);

                    KeyFrame startKf = new KeyFrame(
                            Duration.millis(delay),
                            new KeyValue(node.translateXProperty(), enterTranslateX),
                            new KeyValue(node.opacityProperty(), 0.0));

                    KeyFrame endKf = new KeyFrame(
                            Duration.millis(delay + CONTENT_ENTER_DURATION_MS),
                            new KeyValue(node.translateXProperty(), 0.0, EASE_OUT_SILK),
                            new KeyValue(node.opacityProperty(), 1.0, EASE_OUT_SILK));

                    enterTimeline.getKeyFrames().addAll(startKf, endKf);
                }

                activeAnimation = enterTimeline;
                activeAnimationCancelAction = () -> {
                    for (Node node : enterNodes) {
                        node.setTranslateX(0.0);
                        node.setOpacity(1.0);
                    }
                };

                enterTimeline.setOnFinished(ev -> {
                    try {
                        for (Node node : enterNodes) {
                            node.setTranslateX(0.0);
                            node.setOpacity(1.0);
                        }
                    } finally {
                        activeAnimation = null;
                        activeAnimationCancelAction = null;
                        if (onFinishedCallback != null) {
                            onFinishedCallback.run();
                        }
                    }
                });

                enterTimeline.play();
            } catch (Exception ex) {
                ex.printStackTrace();
                rootContainer.getChildren().setAll(newView);
                activeAnimation = null;
                activeAnimationCancelAction = null;
                if (onFinishedCallback != null) {
                    onFinishedCallback.run();
                }
            }
        });

        exitTimeline.play();
    }

    private static void aplicarClipDelimitado(Parent content) {
        if (content instanceof Region region) {
            Rectangle clip = new Rectangle();
            clip.widthProperty().bind(region.widthProperty());
            clip.heightProperty().bind(region.heightProperty());
            region.setClip(clip);
        }
    }

    private static List<Node> extractContentChildren(Parent content) {
        if (content instanceof Pane pane && !pane.getChildren().isEmpty()) {
            return new ArrayList<>(pane.getChildren());
        }
        return Collections.singletonList(content);
    }

    private static void executarTransicaoCascata(
            StackPane rootContainer,
            Parent oldView,
            Parent newView,
            Direction direction,
            Runnable onFinishedCallback) {
        ButtonBorderLapAnimator.cancelarAnimacaoAtiva();

        // Direção dos eixos:
        // FORWARD: Sai para a ESQUERDA (-), entra da DIREITA (+)
        // BACKWARD: Sai para a DIREITA (+), entra da ESQUERDA (-)
        double exitTranslateX = (direction == Direction.FORWARD) ? -FULL_MOTION_DISTANCE : FULL_MOTION_DISTANCE;
        double enterTranslateX = (direction == Direction.FORWARD) ? FULL_MOTION_DISTANCE : -FULL_MOTION_DISTANCE;

        List<Node> exitNodes = extractStaggerNodes(oldView);

        // Timeline de Saída (Cascata 1 por 1)
        Timeline exitTimeline = new Timeline();

        for (int i = 0; i < exitNodes.size(); i++) {
            Node node = exitNodes.get(i);
            double delay = Math.min(i * FULL_STAGGER_INTERVAL_MS, FULL_MAX_STAGGER_MS);

            KeyFrame startKf = new KeyFrame(
                    Duration.millis(delay),
                    new KeyValue(node.translateXProperty(), node.getTranslateX()),
                    new KeyValue(node.opacityProperty(), node.getOpacity()));

            KeyFrame endKf = new KeyFrame(
                    Duration.millis(delay + FULL_EXIT_DURATION_MS),
                    new KeyValue(node.translateXProperty(), exitTranslateX, EASE_IN_SILK),
                    new KeyValue(node.opacityProperty(), 0.0, EASE_IN_SILK));

            exitTimeline.getKeyFrames().addAll(startKf, endKf);
        }

        // Esmaecimento suave do fundo da tela antiga acompanhando o final do último nó
        double maxExitDelay = exitNodes.isEmpty() ? 0
                : Math.min((exitNodes.size() - 1) * FULL_STAGGER_INTERVAL_MS, FULL_MAX_STAGGER_MS);
        double totalExitTime = maxExitDelay + FULL_EXIT_DURATION_MS;

        exitTimeline.getKeyFrames().add(
                new KeyFrame(
                        Duration.millis(totalExitTime),
                        new KeyValue(oldView.opacityProperty(), 0.0, EASE_IN_SILK)));

        activeAnimation = exitTimeline;
        activeAnimationCancelAction = () -> {
            for (Node node : exitNodes) {
                node.setTranslateX(0.0);
                node.setOpacity(1.0);
            }
            oldView.setOpacity(1.0);
            rootContainer.getChildren().setAll(newView);
        };

        exitTimeline.setOnFinished(e -> {
            try {
                // Reseta nós da tela antiga para consistência se ela for reutilizada
                for (Node node : exitNodes) {
                    node.setTranslateX(0.0);
                    node.setOpacity(1.0);
                }
                oldView.setOpacity(1.0);

                // Troca a tela no container raiz
                rootContainer.getChildren().setAll(newView);

                // Prepara a entrada da tela nova
                List<Node> enterNodes = extractStaggerNodes(newView);
                for (Node node : enterNodes) {
                    node.setTranslateX(enterTranslateX);
                    node.setOpacity(0.0);
                }

                // Timeline de Entrada (Cascata 1 por 1)
                Timeline enterTimeline = new Timeline();

                for (int i = 0; i < enterNodes.size(); i++) {
                    Node node = enterNodes.get(i);
                    double delay = Math.min(i * FULL_STAGGER_INTERVAL_MS, FULL_MAX_STAGGER_MS);

                    KeyFrame startKf = new KeyFrame(
                            Duration.millis(delay),
                            new KeyValue(node.translateXProperty(), enterTranslateX),
                            new KeyValue(node.opacityProperty(), 0.0));

                    KeyFrame endKf = new KeyFrame(
                            Duration.millis(delay + FULL_ENTER_DURATION_MS),
                            new KeyValue(node.translateXProperty(), 0.0, EASE_OUT_SILK),
                            new KeyValue(node.opacityProperty(), 1.0, EASE_OUT_SILK));

                    enterTimeline.getKeyFrames().addAll(startKf, endKf);
                }

                activeAnimation = enterTimeline;
                activeAnimationCancelAction = () -> {
                    for (Node node : enterNodes) {
                        node.setTranslateX(0.0);
                        node.setOpacity(1.0);
                    }
                    newView.setOpacity(1.0);
                };

                enterTimeline.setOnFinished(ev -> {
                    try {
                        // Assegura estado final perfeito
                        for (Node node : enterNodes) {
                            node.setTranslateX(0.0);
                            node.setOpacity(1.0);
                        }
                        newView.setOpacity(1.0);
                    } finally {
                        activeAnimation = null;
                        activeAnimationCancelAction = null;
                        if (onFinishedCallback != null) {
                            onFinishedCallback.run();
                        }
                    }
                });

                enterTimeline.play();
            } catch (Exception ex) {
                ex.printStackTrace();
                rootContainer.getChildren().setAll(newView);
                activeAnimation = null;
                activeAnimationCancelAction = null;
                if (onFinishedCallback != null) {
                    onFinishedCallback.run();
                }
            }
        });

        exitTimeline.play();
    }

    /**
     * Extrai os nós principais de conteúdo de uma tela para animar em cascata
     * de forma contextualizada à estrutura do layout.
     */
    public static List<Node> extractStaggerNodes(Parent root) {
        if (root == null) {
            return Collections.emptyList();
        }

        // Se for um BorderPane (Home, Lista de Estoque, Histórico)
        if (root instanceof BorderPane bp) {
            Node center = bp.getCenter();
            if (center instanceof Parent centerParent) {
                List<Node> centerNodes = extractStaggerNodes(centerParent);
                if (!centerNodes.isEmpty()) {
                    return centerNodes;
                }
            }
            if (center != null) {
                return Collections.singletonList(center);
            }
        }

        // Se for ScrollPane, anima o conteúdo interno
        if (root instanceof ScrollPane sp && sp.getContent() instanceof Parent contentParent) {
            return extractStaggerNodes(contentParent);
        }

        // Se for StackPane (ex: Login, Cadastro, Recuperação de Senha)
        if (root instanceof StackPane sp) {
            // Busca o container principal do formulário/cartão
            for (Node child : sp.getChildren()) {
                if (child instanceof VBox vbox && vbox.getChildren().size() > 1) {
                    return new ArrayList<>(vbox.getChildren());
                }
                if (child instanceof GridPane gp && gp.getChildren().size() > 1) {
                    return new ArrayList<>(gp.getChildren());
                }
            }
            for (Node child : sp.getChildren()) {
                if (child instanceof Pane pane && pane.getChildren().size() > 1 && !(child instanceof Group)) {
                    return new ArrayList<>(pane.getChildren());
                }
            }
        }

        // Se for um VBox ou GridPane direto com múltiplos filhos
        if (root instanceof VBox vbox && !vbox.getChildren().isEmpty()) {
            return new ArrayList<>(vbox.getChildren());
        }

        if (root instanceof GridPane gp && !gp.getChildren().isEmpty()) {
            return new ArrayList<>(gp.getChildren());
        }

        if (root instanceof Pane pane && !pane.getChildren().isEmpty()) {
            return new ArrayList<>(pane.getChildren());
        }

        return Collections.singletonList(root);
    }

    public static String getCurrentFxml() {
        return currentFxml;
    }

    public static void setCurrentFxml(String fxml) {
        currentFxml = fxml;
        isTransitioning = false;
    }

    public static void reset() {
        isTransitioning = false;
    }

    public static void clearHistory() {
        history.clear();
    }

    /**
     * Sincroniza visualmente o botão da tela atual no menu lateral,
     * disparando a animação da linha laranja contornando o botão em direção à direita,
     * completando na esquerda com espessura de 6px e ativando a classe 'btn-ativo'.
     */
    public static void sincronizarBotaoAtivo(Parent view, String fxml) {
        if (view == null || fxml == null) {
            return;
        }

        Node painelLateral = view.lookup(".painel-lateral");
        if (painelLateral instanceof Parent lateralParent) {
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
                btn.setOnAction(e -> {
                    try {
                        ButtonBorderLapAnimator.cancelarAnimacaoAtiva();
                        MainApplication.trocadorDeTelas("login.fxml");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            }
        }
    }

    private static void configurarAcaoNavegacao(Button btn) {
        String id = btn.getId() != null ? btn.getId().toLowerCase() : "";
        String text = btn.getText() != null ? btn.getText().toLowerCase().trim() : "";

        String destino = null;
        if (id.contains("home") || text.contains("inicial") || text.contains("home")) {
            destino = "home.fxml";
        } else if (id.contains("estoque") || text.contains("estoque")) {
            destino = "lista-estoque.fxml";
        } else if (id.contains("relatorio") || id.contains("historico") || text.contains("relat") || text.contains("hist")) {
            destino = "historico.fxml";
        } else if (id.contains("cadastro") || text.contains("cadastr")) {
            destino = "cadastro.fxml";
        }

        if (destino != null) {
            final String targetFxml = destino;
            btn.setOnAction(e -> {
                try {
                    if (!targetFxml.equals(currentFxml) && !isTransitioning) {
                        MainApplication.trocadorDeTelas(targetFxml);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private static boolean isBotaoCorrespondente(Button btn, String fxml) {
        String id = btn.getId() != null ? btn.getId().toLowerCase() : "";
        String text = btn.getText() != null ? btn.getText().toLowerCase().trim() : "";
        String target = fxml.toLowerCase();

        if (target.contains("home")) {
            return id.contains("home") || text.contains("inicial") || text.contains("home");
        }
        if (target.contains("estoque")) {
            return id.contains("estoque") || text.contains("estoque");
        }
        if (target.contains("historico") || target.contains("relatorio")) {
            return id.contains("relatorio") || id.contains("historico") || text.contains("relat") || text.contains("hist");
        }
        if (target.contains("cadastro")) {
            return id.contains("cadastro") || text.contains("cadastr");
        }
        return false;
    }
}
