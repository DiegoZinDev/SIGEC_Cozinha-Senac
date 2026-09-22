package com.sigec.system.sigec.Utils;

import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * Utilitário responsável pela animação fluida do contorno laranja ao selecionar
 * uma opção no menu lateral.
 *
 * A linha laranja (inicialmente na base) dá uma volta completa no botão em direção
 * à direita, contorna o topo e finaliza na lateral esquerda, expandindo sua espessura
 * para 6px e fixando-se no estado ativo (.btn-ativo).
 */
public class ButtonBorderLapAnimator {

    private static final double PAD = 4.0;
    private static final double CORNER_RADIUS = 6.0;
    private static final double DURATION_SECONDS = 0.45; // Equilíbrio perfeito: ágil, visível e fluido

    // Desaceleração suave baseada no modelo Silk Easing
    private static final Interpolator EASE = Interpolator.SPLINE(0.2, 0.0, 0.2, 1.0);

    private static Canvas activeCanvas = null;
    private static AnimationTimer activeTimer = null;
    private static Button activeBtn = null;
    private static Runnable activeCleanup = null;

    /**
     * Verifica se o botão informado já está no meio da animação de volta da linha.
     */
    public static boolean isAnimating(Button btn) {
        return activeBtn == btn && activeTimer != null;
    }

    /**
     * Cancela qualquer animação de contorno ativa e restaura o estado visual definitivo.
     */
    public static void cancelarAnimacaoAtiva() {
        if (activeTimer != null) {
            activeTimer.stop();
            activeTimer = null;
        }
        if (activeCleanup != null) {
            activeCleanup.run();
            activeCleanup = null;
        }
    }

    /**
     * Inicia a animação de contorno da linha laranja ao redor do botão selecionado.
     *
     * @param btn Botão a ser animado e ativado
     */
    public static void animarSelecao(Button btn) {
        if (btn == null) {
            return;
        }

        // Se já estiver animando este mesmo botão, mantém a animação em curso
        if (isAnimating(btn)) {
            return;
        }

        // Interrompe qualquer animação anterior e a consolida
        cancelarAnimacaoAtiva();

        Pane parent = (btn.getParent() instanceof Pane p) ? p : null;
        if (parent == null) {
            if (!btn.getStyleClass().contains("btn-ativo")) {
                btn.getStyleClass().add("btn-ativo");
            }
            return;
        }

        double w = btn.getWidth() > 0 ? btn.getWidth() : (btn.getPrefWidth() > 0 ? btn.getPrefWidth() : 185.0);
        double h = btn.getHeight() > 0 ? btn.getHeight() : (btn.getPrefHeight() > 0 ? btn.getPrefHeight() : 40.0);
        double r = CORNER_RADIUS;

        double canvasW = w + PAD * 2;
        double canvasH = h + PAD * 2;

        Canvas canvas = new Canvas(canvasW, canvasH);
        canvas.setManaged(false);
        canvas.setMouseTransparent(true);

        canvas.layoutXProperty().bind(btn.layoutXProperty().subtract(PAD));
        canvas.layoutYProperty().bind(btn.layoutYProperty().subtract(PAD));

        parent.getChildren().add(canvas);

        activeCanvas = canvas;
        activeBtn = btn;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Oculta a borda estática do botão durante a animação e aplica o fundo ativo
        btn.setStyle("-fx-border-color: transparent; -fx-background-color: linear-gradient(to right, #005bb5, #1877f2); -fx-effect: dropshadow(three-pass-box, rgba(24, 119, 242, 0.4), 12, 0.4, 0, 0);");

        // Alinhamento subpixel para coincidir perfeitamente com os insets do CSS
        double insetLeft = 3.0;
        double insetBottom = 1.0;
        double insetRight = 1.5;
        double insetTop = 1.5;

        double x0 = PAD + insetLeft;
        double y0 = PAD + insetTop;
        double x1 = PAD + w - insetRight;
        double y1 = PAD + h - insetBottom;

        double l1 = (x1 - x0) - 2 * r;
        double l2 = (Math.PI / 2) * r;
        double l3 = (y1 - y0) - 2 * r;
        double l4 = (Math.PI / 2) * r;
        double l5 = (x1 - x0) - 2 * r;
        double l6 = (Math.PI / 2) * r;
        double l7 = (y1 - y0) - 2 * r;
        double l8 = (Math.PI / 2) * r;

        // O traço na lateral esquerda finaliza cobrindo a borda vertical esquerda
        double sLeftTop = l1 + l2 + l3 + l4 + l5 + l6;
        double sLeftBottom = sLeftTop + l7;

        double sStartTail = 0.0;
        double sEndTail = sLeftTop - (l6 * 0.2);
        double sStartHead = l1;
        double sEndHead = sLeftBottom + (l8 * 0.2);

        long startNano = System.nanoTime();

        activeCleanup = () -> {
            if (activeCanvas != null && activeCanvas.getParent() instanceof Pane p) {
                p.getChildren().remove(activeCanvas);
            }
            if (activeBtn != null) {
                activeBtn.setStyle(null);
                if (!activeBtn.getStyleClass().contains("btn-ativo")) {
                    activeBtn.getStyleClass().add("btn-ativo");
                }
            }
            activeCanvas = null;
            activeBtn = null;
            activeTimer = null;
            activeCleanup = null;
        };

        activeTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double elapsedSeconds = (now - startNano) / 1_000_000_000.0;
                double t = Math.min(1.0, elapsedSeconds / DURATION_SECONDS);
                double easeT = EASE.interpolate(0.0, 1.0, t);

                gc.clearRect(0, 0, canvasW, canvasH);

                // Cálculo contínuo e monótono:
                // Em t = 0: cauda em 0, cabeça em l1 (cobre a linha inferior como divisória)
                // Em t = 1: cauda em sEndTail, cabeça em sEndHead (completa a volta na lateral esquerda)
                double sTail = sStartTail + easeT * (sEndTail - sStartTail);
                double sHead = sStartHead + easeT * (sEndHead - sStartHead);

                // Espessura: 2.2px na volta e expande suavemente para 6.0px ao entrar na esquerda
                double thickness;
                if (t < 0.60) {
                    thickness = 2.2;
                } else {
                    double growT = (t - 0.60) / 0.40;
                    double easeGrow = EASE.interpolate(0.0, 1.0, growT);
                    thickness = 2.2 + easeGrow * 3.8;
                }

                // Interpolação de cor elegante: #f3ad50 para #ff9800
                Color coreColor = Color.web("#f3ad50").interpolate(Color.web("#ff9800"), easeT);
                Color glowColor = Color.color(coreColor.getRed(), coreColor.getGreen(), coreColor.getBlue(), 0.38);

                // Passada 1: Halo de brilho externo (Glow)
                drawStrokeSegment(gc, sTail, sHead, thickness + 4.0, glowColor, x0, y0, x1, y1, r);

                // Passada 2: Traço principal nítido com pontas suaves
                drawStrokeSegment(gc, sTail, sHead, thickness, coreColor, x0, y0, x1, y1, r);

                // Conclusão fluida da volta
                if (t >= 1.0) {
                    stop();
                    if (activeCleanup != null) {
                        activeCleanup.run();
                    }
                }
            }
        };

        activeTimer.start();
    }

    private static void drawStrokeSegment(GraphicsContext gc, double sStart, double sEnd, double thickness, Color color,
                                          double x0, double y0, double x1, double y1, double r) {
        if (sEnd <= sStart) {
            return;
        }
        gc.save();
        gc.setStroke(color);
        gc.setLineWidth(thickness);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        gc.beginPath();
        Point2D p0 = getPointAtDistance(sStart, x0, y0, x1, y1, r);
        gc.moveTo(p0.getX(), p0.getY());

        double step = 2.0;
        for (double s = sStart + step; s < sEnd; s += step) {
            Point2D p = getPointAtDistance(s, x0, y0, x1, y1, r);
            gc.lineTo(p.getX(), p.getY());
        }

        Point2D pEnd = getPointAtDistance(sEnd, x0, y0, x1, y1, r);
        gc.lineTo(pEnd.getX(), pEnd.getY());

        gc.stroke();
        gc.restore();
    }

    private static Point2D getPointAtDistance(double s, double x0, double y0, double x1, double y1, double r) {
        double l1 = (x1 - x0) - 2 * r;
        double l2 = (Math.PI / 2) * r;
        double l3 = (y1 - y0) - 2 * r;
        double l4 = (Math.PI / 2) * r;
        double l5 = (x1 - x0) - 2 * r;
        double l6 = (Math.PI / 2) * r;
        double l7 = (y1 - y0) - 2 * r;

        // 1. Base (da esquerda para a direita)
        if (s <= l1) {
            double u = s / l1;
            return new Point2D((x0 + r) + u * l1, y1);
        }
        s -= l1;

        // 2. Canto inferior direito (curva subindo para a direita)
        if (s <= l2) {
            double angle = (Math.PI / 2) - (s / r);
            double cx = x1 - r;
            double cy = y1 - r;
            return new Point2D(cx + r * Math.cos(angle), cy + r * Math.sin(angle));
        }
        s -= l2;

        // 3. Lateral direita (subindo)
        if (s <= l3) {
            double u = s / l3;
            return new Point2D(x1, (y1 - r) - u * l3);
        }
        s -= l3;

        // 4. Canto superior direito (curva virando para a esquerda)
        if (s <= l4) {
            double angle = -(s / r);
            double cx = x1 - r;
            double cy = y0 + r;
            return new Point2D(cx + r * Math.cos(angle), cy + r * Math.sin(angle));
        }
        s -= l4;

        // 5. Topo (da direita para a esquerda)
        if (s <= l5) {
            double u = s / l5;
            return new Point2D((x1 - r) - u * l5, y0);
        }
        s -= l5;

        // 6. Canto superior esquerdo (curva descendo para a lateral esquerda)
        if (s <= l6) {
            double angle = -(Math.PI / 2) - (s / r);
            double cx = x0 + r;
            double cy = y0 + r;
            return new Point2D(cx + r * Math.cos(angle), cy + r * Math.sin(angle));
        }
        s -= l6;

        // 7. Lateral esquerda (descendo até a base)
        if (s <= l7) {
            double u = s / l7;
            return new Point2D(x0, (y0 + r) + u * l7);
        }
        s -= l7;

        // 8. Canto inferior esquerdo (fechando na base)
        double angle = Math.PI - (s / r);
        double cx = x0 + r;
        double cy = y1 - r;
        return new Point2D(cx + r * Math.cos(angle), cy + r * Math.sin(angle));
    }
}
