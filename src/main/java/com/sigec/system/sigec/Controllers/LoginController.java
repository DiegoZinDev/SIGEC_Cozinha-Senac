package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.DAOS.UserDAO;
import com.sigec.system.sigec.MainApplication;
import com.sigec.system.sigec.Services.ScreenTransitionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.sql.SQLException;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.Group;
import javafx.util.Duration;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

/**
 * LoginController
 */
public class LoginController {

    @FXML
    private TextField txtemail;

    @FXML
    private PasswordField pswsenha;

    @FXML
    private Button btnlogin;

    @FXML
    private Hyperlink lbesquecisenha;

    @FXML
    private SVGPath linhaLaranja;

    @FXML
    private VBox loginForm;

    @FXML
    private AnchorPane animatedBackground;

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView logoSenac;

    @FXML
    private Group waveGroup;

    @FXML
    public void initialize() {
        if (waveGroup != null && animatedBackground != null && rootPane != null) {
            // Remove os elementos SVG antigos
            waveGroup.getChildren().clear();

            // Usa a classe utilitária para renderizar e animar o fundo
            com.sigec.system.sigec.Utils.BackgroundAnimator.startAnimation(animatedBackground, rootPane);
        }

        if (logoSenac != null) {
            // Animação da logo: surgindo de forma esmaecer bottom-to-top
            logoSenac.setOpacity(0.0);
            logoSenac.setTranslateY(50.0);

            FadeTransition fadeLogo = new FadeTransition(Duration.millis(1200), logoSenac);
            fadeLogo.setToValue(1.0);

            TranslateTransition moveLogo = new TranslateTransition(Duration.millis(1200), logoSenac);
            moveLogo.setToY(0);

            ParallelTransition ptLogo = new ParallelTransition(fadeLogo, moveLogo);
            ptLogo.setInterpolator(Interpolator.EASE_OUT);
            ptLogo.setDelay(Duration.millis(300)); // Pequeno delay antes de iniciar
            ptLogo.play();
        }
    }

    @FXML
    public void onButtonLoginClick(ActionEvent event) {
        String email = txtemail.getText();
        String senha = pswsenha.getText();

        if (email == null || email.trim().isEmpty() || senha == null || senha.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atenção");
            alert.setHeaderText(null);
            alert.setContentText("Todos os campos devem estar preenchidos!");
            alert.showAndWait();
            return;
        }

        boolean autenticado = false;
        try {
            autenticado = UserDAO.autenticar(email.trim(), senha);
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Banco de Dados");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao conectar ao banco de dados: " + e.getMessage());
            alert.showAndWait();
            return;
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Autenticação");
            alert.setHeaderText(null);
            alert.setContentText("Ocorreu um erro ao validar os dados: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        if (autenticado) {
            try {
                transicaoParaHome();
            } catch (Exception e) {
                Alert alertNav = new Alert(Alert.AlertType.ERROR);
                alertNav.setTitle("Erro de Navegação");
                alertNav.setHeaderText(null);
                alertNav.setContentText("Não foi possível carregar a tela principal: " + e.getMessage());
                alertNav.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Usuário ou senha incorretos.");
            alert.showAndWait();
        }
    }

    private void transicaoParaHome() throws IOException {
        try {
            if (rootPane == null || animatedBackground == null || loginForm == null) {
                MainApplication.trocadorDeTelas("home.fxml");
                return;
            }

            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("home.fxml"));
            Parent homeRoot = loader.load();

            HomeController homeController = loader.getController();
            if (homeController != null) {
                homeController.prepararAnimacao();
            }

            // Garante que homeRoot não esteja duplicado em rootPane
            rootPane.getChildren().remove(homeRoot);
            rootPane.getChildren().add(0, homeRoot);

            // Oculta o formulário e as logos antigas
            FadeTransition ftForm = new FadeTransition(Duration.millis(350), loginForm);
            ftForm.setToValue(0);

            FadeTransition ftLogo1 = new FadeTransition(Duration.millis(350), logoSenac);
            ftLogo1.setToValue(0);

            // Transição suave onde a tela de fundo se recolhe até a altura da barra do topo (50px)
            double currentHeight = rootPane.getHeight() > 0 ? rootPane.getHeight() : 600.0;
            javafx.scene.shape.Rectangle clipRect = new javafx.scene.shape.Rectangle();
            clipRect.widthProperty().bind(rootPane.widthProperty());
            clipRect.setHeight(currentHeight);
            animatedBackground.setClip(clipRect);

            javafx.animation.Timeline clipTimeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.ZERO, new javafx.animation.KeyValue(clipRect.heightProperty(), currentHeight)),
                new javafx.animation.KeyFrame(Duration.millis(750), new javafx.animation.KeyValue(clipRect.heightProperty(), 50.0, Interpolator.SPLINE(0.25, 0.1, 0.25, 1.0)))
            );

            // Transição de fusão da tela de fundo com a barra superior da Home
            FadeTransition ftBgFade = new FadeTransition(Duration.millis(250), animatedBackground);
            ftBgFade.setDelay(Duration.millis(550));
            ftBgFade.setToValue(0.0);

            ParallelTransition pt = new ParallelTransition(ftForm, ftLogo1, clipTimeline, ftBgFade);
            pt.setOnFinished(e -> {
                try {
                    animatedBackground.setClip(null);
                    // Remove homeRoot de rootPane antes de inseri-lo no rootContainer
                    rootPane.getChildren().remove(homeRoot);

                    if (MainApplication.getRootContainer() != null) {
                        MainApplication.getRootContainer().getChildren().setAll(homeRoot);
                        if (MainApplication.getPrimaryStage() != null &&
                                MainApplication.getPrimaryStage().getScene() != null &&
                                MainApplication.getPrimaryStage().getScene().getRoot() != MainApplication
                                        .getRootContainer()) {
                            MainApplication.getPrimaryStage().getScene().setRoot(MainApplication.getRootContainer());
                        }
                        MainApplication.getRootContainer().setDisable(false);
                    }
                    if (homeController != null) {
                        homeController.iniciarAnimacaoLogos();
                    }
                    ScreenTransitionManager.setCurrentFxml("home.fxml");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (MainApplication.getRootContainer() != null) {
                        MainApplication.getRootContainer().getChildren().setAll(homeRoot);
                        MainApplication.getRootContainer().setDisable(false);
                    }
                    ScreenTransitionManager.setCurrentFxml("home.fxml");
                }
            });

            // Desativa a janela durante a animação
            if (MainApplication.getRootContainer() != null) {
                MainApplication.getRootContainer().setDisable(true);
            }
            pt.play();
        } catch (Exception e) {
            MainApplication.trocadorDeTelas("home.fxml");
        }
    }

    @FXML
    public void EsqueciSenha(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("esqueceu-senha.fxml");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Navegação");
            alert.setHeaderText(null);
            alert.setContentText("Não foi possível carregar a tela de recuperação de senha: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void Cadastrarme(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("cadastro.fxml");
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Navegação");
            alert.setHeaderText(null);
            alert.setContentText("Não foi possível carregar a tela de cadastro: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
