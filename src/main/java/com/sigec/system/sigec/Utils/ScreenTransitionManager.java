package com.sigec.system.sigec.Utils;

import com.sigec.system.sigec.MainApplication;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

    // Parâmetros de animação baseados em Emil Kowalski & StyleSeed (Silk motion)
    private static final double MOTION_DISTANCE = 55.0;
    private static final double STAGGER_INTERVAL_MS = 45.0;
    private static final double MAX_STAGGER_MS = 220.0;
    private static final double EXIT_DURATION_MS = 320.0;
    private static final double ENTER_DURATION_MS = 380.0;

    // Curva cúbica suave para desaceleração natural (Silk Ease-Out)
    private static final Interpolator EASE_OUT_SILK = Interpolator.SPLINE(0.1, 0.9, 0.2, 1.0);
    // Curva cúbica para saída (Silk Ease-In)
    private static final Interpolator EASE_IN_SILK = Interpolator.SPLINE(0.4, 0.0, 0.2, 1.0);

    public static void trocarTela(StackPane rootContainer, String fxml, Direction requestedDirection)
            throws IOException {
        if (isTransitioning) {
            return;
        }

        if (fxml == null || (fxml.equals(currentFxml) && !rootContainer.getChildren().isEmpty())) {
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxml));
        Parent newView = fxmlLoader.load();

        if (rootContainer.getChildren().isEmpty()) {
            rootContainer.getChildren().add(newView);
            currentFxml = fxml;
            return;
        }

        Parent oldView = (Parent) rootContainer.getChildren().get(rootContainer.getChildren().size() - 1);

        Direction effectiveDirection = resolveDirection(fxml, requestedDirection);
        updateHistory(fxml, effectiveDirection);

        isTransitioning = true;
        rootContainer.setDisable(true);

        try {
            executarTransicaoCascata(rootContainer, oldView, newView, effectiveDirection, () -> {
                currentFxml = fxml;
                isTransitioning = false;
                rootContainer.setDisable(false);
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            rootContainer.getChildren().setAll(newView);
            currentFxml = fxml;
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

    private static void executarTransicaoCascata(
            StackPane rootContainer,
            Parent oldView,
            Parent newView,
            Direction direction,
            Runnable onFinishedCallback) {
        // Direção dos eixos:
        // FORWARD: Sai para a ESQUERDA (-), entra da DIREITA (+)
        // BACKWARD: Sai para a DIREITA (+), entra da ESQUERDA (-)
        double exitTranslateX = (direction == Direction.FORWARD) ? -MOTION_DISTANCE : MOTION_DISTANCE;
        double enterTranslateX = (direction == Direction.FORWARD) ? MOTION_DISTANCE : -MOTION_DISTANCE;

        List<Node> exitNodes = extractStaggerNodes(oldView);

        // Timeline de Saída (Cascata 1 por 1)
        Timeline exitTimeline = new Timeline();

        for (int i = 0; i < exitNodes.size(); i++) {
            Node node = exitNodes.get(i);
            double delay = Math.min(i * STAGGER_INTERVAL_MS, MAX_STAGGER_MS);

            KeyFrame startKf = new KeyFrame(
                    Duration.millis(delay),
                    new KeyValue(node.translateXProperty(), node.getTranslateX()),
                    new KeyValue(node.opacityProperty(), node.getOpacity()));

            KeyFrame endKf = new KeyFrame(
                    Duration.millis(delay + EXIT_DURATION_MS),
                    new KeyValue(node.translateXProperty(), exitTranslateX, EASE_IN_SILK),
                    new KeyValue(node.opacityProperty(), 0.0, EASE_IN_SILK));

            exitTimeline.getKeyFrames().addAll(startKf, endKf);
        }

        // Esmaecimento suave do fundo da tela antiga acompanhando o final do último nó
        double maxExitDelay = exitNodes.isEmpty() ? 0
                : Math.min((exitNodes.size() - 1) * STAGGER_INTERVAL_MS, MAX_STAGGER_MS);
        double totalExitTime = maxExitDelay + EXIT_DURATION_MS;

        exitTimeline.getKeyFrames().add(
                new KeyFrame(
                        Duration.millis(totalExitTime),
                        new KeyValue(oldView.opacityProperty(), 0.0, EASE_IN_SILK)));

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
                    double delay = Math.min(i * STAGGER_INTERVAL_MS, MAX_STAGGER_MS);

                    KeyFrame startKf = new KeyFrame(
                            Duration.millis(delay),
                            new KeyValue(node.translateXProperty(), enterTranslateX),
                            new KeyValue(node.opacityProperty(), 0.0));

                    KeyFrame endKf = new KeyFrame(
                            Duration.millis(delay + ENTER_DURATION_MS),
                            new KeyValue(node.translateXProperty(), 0.0, EASE_OUT_SILK),
                            new KeyValue(node.opacityProperty(), 1.0, EASE_OUT_SILK));

                    enterTimeline.getKeyFrames().addAll(startKf, endKf);
                }

                enterTimeline.setOnFinished(ev -> {
                    try {
                        // Assegura estado final perfeito
                        for (Node node : enterNodes) {
                            node.setTranslateX(0.0);
                            node.setOpacity(1.0);
                        }
                        newView.setOpacity(1.0);
                    } finally {
                        if (onFinishedCallback != null) {
                            onFinishedCallback.run();
                        }
                    }
                });

                enterTimeline.play();
            } catch (Exception ex) {
                ex.printStackTrace();
                rootContainer.getChildren().setAll(newView);
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
}
