package com.sigec.system.sigec;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

import com.sigec.system.sigec.Utils.ScreenTransitionManager;

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
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    public static void trocadorDeTelas(String fxml) throws IOException {
        trocadorDeTelas(fxml, ScreenTransitionManager.Direction.AUTO);
    }

    public static void trocadorDeTelas(String fxml, ScreenTransitionManager.Direction direction) throws IOException {
        if (rootContainer == null) {
            throw new IllegalStateException("rootContainer não foi inicializado.");
        }
        ScreenTransitionManager.trocarTela(rootContainer, fxml, direction);
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
            popupStage.setResizable(true);
            popupStage.showAndWait();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static StackPane getRootContainer() {
        return rootContainer;
    }
}
