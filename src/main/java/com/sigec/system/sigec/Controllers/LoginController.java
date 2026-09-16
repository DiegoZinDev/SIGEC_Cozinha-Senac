package com.sigec.system.sigec.Controllers;

import com.sigec.system.sigec.DAOS.UserDAO;
import com.sigec.system.sigec.MainApplication;
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

            Canvas canvas = new Canvas(800, 600);
            // Garante que o canvas ocupe a tela toda usando o rootPane
            canvas.widthProperty().bind(rootPane.widthProperty());
            canvas.heightProperty().bind(rootPane.heightProperty());

            // Adiciona o canvas no fundo (índice 0)
            animatedBackground.getChildren().add(0, canvas);

            GraphicsContext gc = canvas.getGraphicsContext2D();

            AnimationTimer timer = new AnimationTimer() {
                private long lastUpdate = 0;
                private double time = 0;

                @Override
                public void handle(long now) {
                    if (lastUpdate == 0) {
                        lastUpdate = now;
                        return;
                    }
                    double deltaSeconds = (now - lastUpdate) / 1_000_000_000.0;
                    lastUpdate = now;

                    time += deltaSeconds;

                    renderBackground(gc, canvas.getWidth(), canvas.getHeight(), time);
                }
            };
            timer.start();
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

    private void renderBackground(GraphicsContext gc, double width, double height, double time) {
        // Fundo
        gc.setFill(Color.web("#f6f8fb"));
        gc.fillRect(0, 0, width, height);

        // Padrão diagonal suave (agora escalonado para cobrir qualquer tamanho de tela)
        gc.save();
        gc.setStroke(Color.web("#c8d2df", 0.10)); // Muito mais transparente (quase invisível)
        gc.setLineWidth(14); 
        gc.setLineCap(StrokeLineCap.ROUND); // Bordas arredondadas nos traços
        gc.setLineDashes(70, 45); 
        
        double diag = Math.sqrt(width * width + height * height);
        gc.translate(width / 2, height / 2);
        gc.rotate(-15);
        gc.translate(-diag, -diag);
        
        int row = 0;
        for (double y = 0; y < diag * 2; y += 65) { // Espaçamento maior entre as linhas
            gc.setLineDashOffset(row % 2 == 0 ? 0 : 50); 
            gc.strokeLine(0, y, diag * 2, y);
            row++;
        }
        gc.restore();

        // Coordenadas relativas para desenhar a onda proporcional ao tamanho da tela
        double w = width;
        double h = height;
        
        // Forma da onda azul (agora iniciando com espaço em branco na esquerda)
        gc.save();
        gc.beginPath();
        gc.moveTo(w * 0.15, h);
        // Primeiro Cubic Bezier: sobe abruptamente formando uma lombada suave, e desce para um vale
        gc.bezierCurveTo(w * 0.25, h * 0.65, w * 0.45, h * 0.65, w * 0.65, h * 0.88);
        // Segundo Cubic Bezier: sobe suavemente do vale até encostar na borda direita
        gc.bezierCurveTo(w * 0.75, h * 1.0, w * 0.9, h * 0.65, w + 5, h * 0.65);
        gc.lineTo(w + 5, h + 50); // Desce para o canto inferior direito
        gc.lineTo(w * 0.15, h + 50); // Volta reta pelo chão até o ponto X inicial
        gc.closePath();
        gc.setFill(Color.web("#0b2647"));
        gc.fill();
        gc.restore();
        
        // Linha laranja com espessura variável (Tapered shape ajustado ao Cubic Bezier)
        gc.save();
        gc.beginPath();
        // Borda superior (acompanha a nova onda azul)
        gc.moveTo(w * 0.15, h);
        gc.bezierCurveTo(w * 0.25, h * 0.65, w * 0.45, h * 0.65, w * 0.65, h * 0.88);
        gc.bezierCurveTo(w * 0.75, h * 1.0, w * 0.9, h * 0.65, w + 5, h * 0.65);
        
        // Borda inferior (traçando de volta calculando a espessura dinamicamente)
        gc.lineTo(w + 5, h * 0.65 + 14); // 14px na direita
        gc.bezierCurveTo(
            w * 0.9, h * 0.65 + 12.6, 
            w * 0.75, h * 1.0 + 10.5, 
            w * 0.65, h * 0.88 + 9.1
        );
        gc.bezierCurveTo(
            w * 0.45, h * 0.65 + 6.2, 
            w * 0.25, h * 0.65 + 3.4, 
            w * 0.15, h + 2 // 2px na esquerda
        );
        gc.closePath();
        
        // Efeito de REFLEXO suave e contínuo no formato preenchido
        double durationSeconds = 6.0; 
        double phase = (time % durationSeconds) / durationSeconds;
        double highlightCenter = phase * (w + 1600) - 800; 
        
        LinearGradient reflectionGradient = new LinearGradient(
            highlightCenter - 600, 0,
            highlightCenter + 600, 0,
            false,
            CycleMethod.NO_CYCLE,
            new Stop(0.0, Color.web("#e47d1b")),         // Laranja sólido nas pontas
            new Stop(0.5, Color.web("#ffd9b3")),         // Reflexo central suave
            new Stop(1.0, Color.web("#e47d1b"))
        );
        
        gc.setFill(reflectionGradient);
        gc.fill();
        
        gc.restore();
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

        try {
            boolean autenticado = UserDAO.autenticar(email.trim(), senha);
            if (autenticado) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Sucesso");
                alert.setHeaderText(null);
                alert.setContentText("Login realizado com sucesso!");
                alert.showAndWait();

                transicaoParaHome();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Usuário ou senha incorretos.");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Banco de Dados");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao conectar ao banco de dados: " + e.getMessage());
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro de Navegação");
            alert.setHeaderText(null);
            alert.setContentText("Não foi possível carregar a tela principal: " + e.getMessage());
            alert.showAndWait();
        }

    }

    private void transicaoParaHome() throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("home.fxml"));
        Parent homeRoot = loader.load();

        HomeController homeController = loader.getController();
        homeController.prepararAnimacao();

        // Adiciona a home por trás
        rootPane.getChildren().add(0, homeRoot);

        // Oculta o formulário e as logos antigas
        FadeTransition ftForm = new FadeTransition(Duration.millis(400), loginForm);
        ftForm.setToValue(0);

        FadeTransition ftLogo1 = new FadeTransition(Duration.millis(400), logoSenac);
        ftLogo1.setToValue(0);

        // Encolhe o fundo para virar a barra do topo (altura 50)
        ScaleTransition st = new ScaleTransition(Duration.millis(800), animatedBackground);
        st.setToY(50.0 / 600.0);

        TranslateTransition tt = new TranslateTransition(Duration.millis(800), animatedBackground);
        tt.setToY(-275); // Move de modo que o centro fique na posição correta do topo

        ParallelTransition pt = new ParallelTransition(ftForm, ftLogo1, st, tt);
        pt.setOnFinished(e -> {
            MainApplication.getPrimaryStage().getScene().setRoot(homeRoot);
            homeController.iniciarAnimacaoLogos();
        });

        // Desativa a janela durante a animação
        MainApplication.getPrimaryStage().getScene().getRoot().setDisable(true);
        pt.play();
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
