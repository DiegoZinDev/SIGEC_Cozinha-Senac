package com.sigec.system.sigec.Utils;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;

public class BackgroundAnimator {

    public static void startAnimation(AnchorPane animatedBackground, StackPane rootPane) {
        if (animatedBackground == null || rootPane == null) {
            return;
        }

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

    private static void renderBackground(GraphicsContext gc, double width, double height, double time) {
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
        gc.moveTo(0, h);
        // Primeiro Cubic Bezier: retornado ao pico suave, mas estendido até a ponta esquerda inferior (x=0)
        gc.bezierCurveTo(w * 0.20, h * 0.60, w * 0.45, h * 0.75, w * 0.60, h * 0.75);
        // Segundo Cubic Bezier: sai do vale perfeitamente arredondado e sobe até a borda direita
        gc.bezierCurveTo(w * 0.75, h * 0.75, w * 0.85, h * 0.55, w + 5, h * 0.55);
        gc.lineTo(w + 5, h + 50); // Desce para o canto inferior direito
        gc.lineTo(0, h + 50); // Volta reta pelo chão até a ponta esquerda
        gc.closePath();
        gc.setFill(Color.web("#0b2647"));
        gc.fill();
        gc.restore();
        
        // Linha laranja com espessura variável (Tapered shape ajustado ao Cubic Bezier)
        gc.save();
        gc.beginPath();
        // Borda superior (acompanha a nova onda azul)
        gc.moveTo(0, h);
        gc.bezierCurveTo(w * 0.20, h * 0.60, w * 0.45, h * 0.75, w * 0.60, h * 0.75);
        gc.bezierCurveTo(w * 0.75, h * 0.75, w * 0.85, h * 0.55, w + 5, h * 0.55);
        
        // Borda inferior (traçando de volta calculando a espessura dinamicamente)
        gc.lineTo(w + 5, h * 0.55 + 14); // 14px na direita
        gc.bezierCurveTo(
            w * 0.85, h * 0.55 + 12.2, 
            w * 0.75, h * 0.75 + 11.0, 
            w * 0.60, h * 0.75 + 9.2
        );
        gc.bezierCurveTo(
            w * 0.45, h * 0.75 + 7.4, 
            w * 0.20, h * 0.60 + 4.4, 
            0, h + 2 // 2px na esquerda
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
}
