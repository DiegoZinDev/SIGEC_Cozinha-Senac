package com.sigec.system.sigec;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        trocadorDeTelas("login.fxml");

        primaryStage.setTitle("Sistema Cozinha");
        primaryStage.show();
    }

    public static void trocadorDeTelas(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainApplication.class.getResource(fxml)
        );

        if(primaryStage.isMaximized()){
            Scene scene = new Scene(fxmlLoader.load());
            primaryStage.setScene(scene);
            primaryStage.setMaximized(false);
            primaryStage.setMaximized(true);
        }
        else{
            Scene scene = new Scene(fxmlLoader.load(), 1366,768);

            primaryStage.setScene(scene);
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
