package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.Constructors.User;
import com.sigec.system.sigec.DAOS.UserDAO;
import com.sigec.system.sigec.MainApplication;
import com.sigec.system.sigec.Services.SessaoService;
import com.sigec.system.sigec.Utils.BackgroundAnimator;
import com.sigec.system.sigec.Utils.FormNavigationUtil;
import com.sigec.system.sigec.Utils.ScreenTransitionManager;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Controlador da tela de Login e Autenticação de Usuários.
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
            waveGroup.getChildren().clear();
            BackgroundAnimator.startAnimation(animatedBackground, rootPane);
        }

        if (logoSenac != null) {
            iniciarAnimacaoLogo();
        }

        // Navegação por tecla Enter através dos campos de formulário
        FormNavigationUtil.encadearCampos(btnlogin, txtemail, pswsenha);
    }

    private void iniciarAnimacaoLogo() {
        logoSenac.setOpacity(0.0);
        logoSenac.setTranslateY(50.0);

        FadeTransition fadeLogo = new FadeTransition(Duration.millis(1200), logoSenac);
        fadeLogo.setToValue(1.0);

        TranslateTransition moveLogo = new TranslateTransition(Duration.millis(1200), logoSenac);
        moveLogo.setToY(0);

        ParallelTransition ptLogo = new ParallelTransition(fadeLogo, moveLogo);
        ptLogo.setInterpolator(Interpolator.EASE_OUT);
        ptLogo.setDelay(Duration.millis(300));
        ptLogo.play();
    }

    @FXML
    public void onButtonLoginClick(ActionEvent event) {
        String email = txtemail != null ? txtemail.getText() : null;
        String senha = pswsenha != null ? pswsenha.getText() : null;

        if (email == null || email.trim().isEmpty() || senha == null || senha.isEmpty()) {
            exibirAlerta(Alert.AlertType.WARNING, "Atenção", "Todos os campos devem estar preenchidos!");
            return;
        }

        try {
            boolean autenticado = UserDAO.autenticar(email.trim(), senha);
            if (autenticado) {
                // Registra usuário logado na sessão ativa
                User userLogado = UserDAO.buscarPorEmail(email.trim());
                if (userLogado != null) {
                    SessaoService.setUsuarioLogado(userLogado);
                }
                transicaoParaHome();
            } else {
                exibirAlerta(Alert.AlertType.ERROR, "Erro", "Usuário ou senha incorretos.");
            }
        } catch (SQLException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Banco de Dados", "Erro ao conectar ao banco de dados: " + e.getMessage());
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Autenticação", "Ocorreu um erro ao validar os dados: " + e.getMessage());
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

            rootPane.getChildren().remove(homeRoot);
            rootPane.getChildren().add(0, homeRoot);

            // Animação de fade-out do formulário de login e logo
            FadeTransition ftForm = new FadeTransition(Duration.millis(350), loginForm);
            ftForm.setToValue(0);

            FadeTransition ftLogo = new FadeTransition(Duration.millis(350), logoSenac);
            ftLogo.setToValue(0);

            // Recolhimento suave do fundo até a altura do topo (50px)
            double currentHeight = rootPane.getHeight() > 0 ? rootPane.getHeight() : 600.0;
            Rectangle clipRect = new Rectangle();
            clipRect.widthProperty().bind(rootPane.widthProperty());
            clipRect.setHeight(currentHeight);
            animatedBackground.setClip(clipRect);

            Timeline clipTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(clipRect.heightProperty(), currentHeight)),
                    new KeyFrame(Duration.millis(750), new KeyValue(clipRect.heightProperty(), 50.0, Interpolator.SPLINE(0.25, 0.1, 0.25, 1.0)))
            );

            FadeTransition ftBgFade = new FadeTransition(Duration.millis(250), animatedBackground);
            ftBgFade.setDelay(Duration.millis(550));
            ftBgFade.setToValue(0.0);

            ParallelTransition pt = new ParallelTransition(ftForm, ftLogo, clipTimeline, ftBgFade);
            pt.setOnFinished(e -> finalizarTransicaoHome(homeRoot, homeController));

            if (MainApplication.getRootContainer() != null) {
                MainApplication.getRootContainer().setDisable(true);
            }
            pt.play();
        } catch (Exception e) {
            MainApplication.trocadorDeTelas("home.fxml");
        }
    }

    private void finalizarTransicaoHome(Parent homeRoot, HomeController homeController) {
        try {
            animatedBackground.setClip(null);
            rootPane.getChildren().remove(homeRoot);

            if (MainApplication.getRootContainer() != null) {
                MainApplication.getRootContainer().getChildren().setAll(homeRoot);
                if (MainApplication.getPrimaryStage() != null &&
                        MainApplication.getPrimaryStage().getScene() != null &&
                        MainApplication.getPrimaryStage().getScene().getRoot() != MainApplication.getRootContainer()) {
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
    }

    @FXML
    public void EsqueciSenha(ActionEvent event) {
        try {
            MainApplication.trocadorDeTelas("esqueceu-senha.fxml");
        } catch (IOException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Navegação", "Não foi possível carregar a tela de recuperação de senha: " + e.getMessage());
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
