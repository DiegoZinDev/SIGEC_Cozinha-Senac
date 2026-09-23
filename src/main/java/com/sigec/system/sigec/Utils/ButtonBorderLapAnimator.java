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
 * Utilitário responsável pelo ciclo de vida e pela animação fluida do contorno
 * laranja (Border Lap) ao selecionar uma opção no menu lateral.
 *
 * Para botões padrão, a linha laranja dá uma volta completa no botão em direção
 * à direita, contorna o topo e finaliza na lateral esquerda, expandindo sua
 * espessura
 * para 6px e fixando-se no estado ativo (.btn-ativo).
 *
 * Para botões dropdown, delega a execução especializada para
 * {@link DropdownBorderLapAnimator}.
 */
public final class ButtonBorderLapAnimator {

    private static final double PAD = 4.0;
    private static final double CORNER_RADIUS = 6.0;
    private static final double DURATION_SECONDS = 0.45;
    private static final Interpolator EASE = Interpolator.SPLINE(0.2, 0.0, 0.2, 1.0);

    private static Canvas activeCanvas = null;
    private static AnimationTimer activeTimer = null;
    private static Button activeBtn = null;
    private static Runnable activeCleanup = null;

    private ButtonBorderLapAnimator() {
        // Construtor privado para utilitário estático
    }

    /**
     * Verifica se o botão informado já está no meio da animação de volta da linha.
     */
    public static boolean isAnimating(Button btn) {
        return activeBtn == btn && activeTimer != null;
    }

    /**
     * Registra o estado da animação ativa em andamento (padrão ou dropdown).
     */
    static void registrarAnimacaoAtiva(Canvas canvas, Button btn, AnimationTimer timer, Runnable cleanup) {
        activeCanvas = canvas;
        activeBtn = btn;
        activeTimer = timer;
        activeCleanup = cleanup;
    }

    /**
     * Conclui a animação ativa, executando a limpeza e restaurando os estados visuais.
     */
    static void finalizarAnimacaoAtiva() {
        if (activeCleanup != null) {
            activeCleanup.run();
            activeCleanup = null;
        }
        activeCanvas = null;
        activeBtn = null;
        activeTimer = null;
    }

    /**
     * Cancela qualquer animação de contorno ativa e restaura o estado visual definitivo.
     */
    public static void cancelarAnimacaoAtiva() {
        if (activeTimer != null) {
            activeTimer.stop();
            activeTimer = null;
        }
        finalizarAnimacaoAtiva();
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

        if (isAnimating(btn)) {
            return;
        }

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

        GraphicsContext gc = canvas.getGraphicsContext2D();

        btn.setStyle(
                "-fx-border-color: transparent; -fx-background-color: linear-gradient(to right, #005bb5, #1877f2); -fx-effect: dropshadow(three-pass-box, rgba(24, 119, 242, 0.4), 12, 0.4, 0, 0);");

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

        double sLeftTop = l1 + l2 + l3 + l4 + l5 + l6;
        double sLeftBottom = sLeftTop + l7;

        double sStartTail = 0.0;
        double sEndTail = sLeftTop - (l6 * 0.2);
        double sStartHead = l1;
        double sEndHead = sLeftBottom + (l8 * 0.2);

        long startNano = System.nanoTime();

        Runnable cleanup = () -> {
            if (canvas.getParent() instanceof Pane p) {
                p.getChildren().remove(canvas);
            }
            btn.setStyle(null);
            if (!btn.getStyleClass().contains("btn-ativo")) {
                btn.getStyleClass().add("btn-ativo");
            }
        };

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    double elapsedSeconds = (now - startNano) / 1_000_000_000.0;
                    double t = Math.max(0.0, Math.min(1.0, elapsedSeconds / DURATION_SECONDS));
                    double easeT = EASE.interpolate(0.0, 1.0, t);

                    gc.clearRect(0, 0, canvasW, canvasH);

                    double sTail = sStartTail + easeT * (sEndTail - sStartTail);
                    double sHead = sStartHead + easeT * (sEndHead - sStartHead);

                    double thickness;
                    if (t < 0.60) {
                        thickness = 2.2;
                    } else {
                        double growT = Math.max(0.0, Math.min(1.0, (t - 0.60) / 0.40));
                        double easeGrow = EASE.interpolate(0.0, 1.0, growT);
                        thickness = 2.2 + easeGrow * 3.8;
                    }

                    Color coreColor = Color.web("#f3ad50").interpolate(Color.web("#ff9800"), easeT);
                    Color glowColor = Color.color(coreColor.getRed(), coreColor.getGreen(), coreColor.getBlue(), 0.38);

                    drawStrokeSegment(gc, sTail, sHead, thickness + 4.0, glowColor, x0, y0, x1, y1, r);
                    drawStrokeSegment(gc, sTail, sHead, thickness, coreColor, x0, y0, x1, y1, r);

                    if (t >= 1.0) {
                        stop();
                        finalizarAnimacaoAtiva();
                    }
                } catch (Exception ex) {
                    stop();
                    finalizarAnimacaoAtiva();
                }
            }
        };

        registrarAnimacaoAtiva(canvas, btn, timer, cleanup);
        timer.start();
    }

    /**
     * Ponto de entrada compatível para a animação dropdown, delegando para
     * {@link DropdownBorderLapAnimator#animarDropdown(Button, Pane)}.
     */
    public static void animarDropdown(Button btn, Pane submenu) {
        DropdownBorderLapAnimator.animarDropdown(btn, submenu);
    }

    private static void drawStrokeSegment(GraphicsContext gc, double sStart, double sEnd, double thickness,
            Color color, double x0, double y0, double x1, double y1, double r) {
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

        // 1. Base
        if (s <= l1) {
            double u = s / l1;
            return new Point2D((x0 + r) + u * l1, y1);
        }
        s -= l1;

        // 2. Canto inferior direito
        if (s <= l2) {
            double angle = (Math.PI / 2) - (s / r);
            double cx = x1 - r;
            double cy = y1 - r;
            return new Point2D(cx + r * Math.cos(angle), cy + r * Math.sin(angle));
        }
        s -= l2;

        // 3. Lateral direita
        if (s <= l3) {
            double u = s / l3;
            return new Point2D(x1, (y1 - r) - u * l3);
        }
        s -= l3;

        // 4. Canto superior direito
        if (s <= l4) {
            double angle = -(s / r);
            double cx = x1 - r;
            double cy = y0 + r;
            return new Point2D(cx + r * Math.cos(angle), cy + r * Math.sin(angle));
        }
        s -= l4;

        // 5. Topo
        if (s <= l5) {
            double u = s / l5;
            return new Point2D((x1 - r) - u * l5, y0);
        }
        s -= l5;

        // 6. Canto superior esquerdo
        if (s <= l6) {
            double angle = -(Math.PI / 2) - (s / r);
            double cx = x0 + r;
            double cy = y0 + r;
            return new Point2D(cx + r * Math.cos(angle), cy + r * Math.sin(angle));
        }
        s -= l6;

        // 7. Lateral esquerda
        double u = Math.min(1.0, Math.max(0.0, s / l7));
        return new Point2D(x0, (y0 + r) + u * l7);
    }
}
