package com.sigec.system.sigec;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainApplication extends Application {

    private static Stage primaryStage;
    private static StackPane rootContainer;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        rootContainer = new StackPane();
        
        // Define a cena inicial usando o StackPane como raiz
        Scene scene = new Scene(rootContainer, 1366, 768);
        primaryStage.setScene(scene);

        // Carrega a primeira tela
        trocadorDeTelas("login.fxml");

        primaryStage.setTitle("Sistema Cozinha");
        primaryStage.show();
    }

    public static void trocadorDeTelas(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApplication.class.getResource(fxml)
        );
        Parent newView = fxmlLoader.load();

        if (rootContainer.getChildren().isEmpty()) {
            rootContainer.getChildren().add(newView);
        } else {
            // Pega a tela atual (último elemento adicionado no StackPane)
            Parent oldView = (Parent) rootContainer.getChildren().get(rootContainer.getChildren().size() - 1);
            // Desativa interações na tela antiga durante a transição para evitar cliques duplos
            oldView.setDisable(true);
            
            // Prepara a nova tela para entrar pela direita
            newView.setTranslateX(rootContainer.getWidth());
            rootContainer.getChildren().add(newView);
            
            // Cria a animação de transição deslizando
            Timeline timeline = new Timeline();
            KeyValue kvNovaTela = new KeyValue(newView.translateXProperty(), 0, Interpolator.EASE_BOTH);
            KeyValue kvTelaAntiga = new KeyValue(oldView.translateXProperty(), -rootContainer.getWidth(), Interpolator.EASE_BOTH);
            
            KeyFrame kf = new KeyFrame(Duration.millis(500), kvNovaTela, kvTelaAntiga);
            timeline.getKeyFrames().add(kf);
            timeline.setOnFinished(e -> {
                // Remove a tela antiga quando a animação termina
                rootContainer.getChildren().remove(oldView);
            });
            timeline.play();
        }
    }

    public static void abrirPopUp(String fxml) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(fxml));
            Parent root = fxmlLoader.load();

            Stage popupStage = new Stage();

            Scene scene = new Scene(root);

            popupStage.setScene(scene);
            popupStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            popupStage.initOwner(primaryStage);
            popupStage.setResizable(false);
            popupStage.showAndWait();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}
