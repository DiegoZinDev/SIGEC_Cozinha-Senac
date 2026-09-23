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
 * Utilitário especializado na animação de contorno para menus Dropdown.
 * 
 * Executa o contorno (lap) ao redor do botão principal de Cadastros, remove a
 * linha inferior da base do botão ao completar a volta e estende a espinha
 * vertical
 * de 6px descendo pela lateral esquerda cobrindo o submenu, projetando
 * simultaneamente
 * as linhas horizontais finas (1.2px) conectadas sob as opções do submenu.
 */
public final class DropdownBorderLapAnimator {

    private static final double PAD = 4.0;
    private static final double CORNER_RADIUS = 6.0;
    private static final double DURATION_SECONDS = 0.50;
    private static final Interpolator EASE = Interpolator.SPLINE(0.2, 0.0, 0.2, 1.0);

    private DropdownBorderLapAnimator() {
        // Construtor privado para utilitário estático
    }

    /**
     * Inicia a animação fluida especializada para botões Dropdown do menu lateral.
     *
     * @param btn     Botão dropdown principal
     * @param submenu Container de opções do submenu
     */
    public static void animarDropdown(Button btn, Pane submenu) {
        if (btn == null) {
            return;
        }

        if (ButtonBorderLapAnimator.isAnimating(btn)) {
            return;
        }

        ButtonBorderLapAnimator.cancelarAnimacaoAtiva();

        Pane parent = (btn.getParent() instanceof Pane p) ? p : null;
        if (parent == null) {
            aplicarEstilosImediatos(btn, submenu);
            return;
        }

        if (submenu != null) {
            submenu.setVisible(true);
            submenu.setManaged(true);
        }
        parent.applyCss();
        parent.layout();

        double w = btn.getWidth() > 0 ? btn.getWidth() : (btn.getPrefWidth() > 0 ? btn.getPrefWidth() : 185.0);
        double h = btn.getHeight() > 0 ? btn.getHeight() : (btn.getPrefHeight() > 0 ? btn.getPrefHeight() : 40.0);
        double r = CORNER_RADIUS;

        double subH = (submenu != null && submenu.getHeight() > 0) ? submenu.getHeight()
                : (submenu != null ? submenu.prefHeight(w) : 72.0);
        if (subH <= 0 && submenu != null) {
            subH = Math.max(72.0, submenu.getChildren().size() * 36.0);
        }

        double btnY = btn.getLayoutY();
        double subY = (submenu != null && submenu.getLayoutY() > btnY) ? submenu.getLayoutY() : (btnY + h);
        double rawOffset = subY - btnY;
        final double verticalOffset = Math.max(h, rawOffset);
        final double totalH = verticalOffset + subH;

        double canvasW = w + PAD * 2 + 10.0;
        double canvasH = totalH + PAD * 2 + 10.0;

        Canvas canvas = new Canvas(canvasW, canvasH);
        canvas.setManaged(false);
        canvas.setMouseTransparent(true);

        canvas.layoutXProperty().bind(btn.layoutXProperty().subtract(PAD));
        canvas.layoutYProperty().bind(btn.layoutYProperty().subtract(PAD));

        parent.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        btn.setStyle(
                "-fx-border-color: transparent; -fx-background-color: linear-gradient(to right, #005bb5, #1877f2); -fx-effect: dropshadow(three-pass-box, rgba(24, 119, 242, 0.4), 12, 0.4, 0, 0);");
        if (submenu != null) {
            submenu.setStyle("-fx-border-color: transparent;");
            for (javafx.scene.Node child : submenu.getChildren()) {
                if (child instanceof Button subBtn) {
                    subBtn.setStyle("-fx-border-color: transparent;");
                }
            }
        }

        double insetLeft = 3.0;
        double insetBottom = 1.0;
        double insetRight = 1.5;
        double insetTop = 1.5;

        double x0 = PAD + insetLeft;
        double y0 = PAD + insetTop;
        double x1 = PAD + w - insetRight;
        double y1 = PAD + h - insetBottom;
        double ySubBottom = PAD + totalH - 1.0;

        double l1 = (x1 - x0) - 2 * r;
        double l2 = (Math.PI / 2) * r;
        double l3 = (y1 - y0) - 2 * r;
        double l4 = (Math.PI / 2) * r;
        double l5 = (x1 - x0) - 2 * r;
        double l6 = (Math.PI / 2) * r;
        double l7Dropdown = Math.max(30.0, ySubBottom - (y0 + r));

        double sLeftTop = l1 + l2 + l3 + l4 + l5 + l6;
        double sTotal = sLeftTop + l7Dropdown;

        double sStartTail = 0.0;
        double sEndTail = sLeftTop - (l6 * 0.2);
        double sStartHead = l1;
        double sEndHead = sTotal;

        long startNano = System.nanoTime();

        Runnable cleanup = () -> {
            if (canvas.getParent() instanceof Pane p) {
                p.getChildren().remove(canvas);
            }
            btn.setStyle(null);
            if (!btn.getStyleClass().contains("btn-dropdown-ativo")) {
                btn.getStyleClass().add("btn-dropdown-ativo");
            }
            if (submenu != null) {
                submenu.setStyle(null);
                for (javafx.scene.Node child : submenu.getChildren()) {
                    if (child instanceof Button subBtn) {
                        subBtn.setStyle(null);
                    }
                }
                if (!submenu.getStyleClass().contains("submenu-lateral-ativo")) {
                    submenu.getStyleClass().add("submenu-lateral-ativo");
                }
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

                    // Espessura: 2.2px na volta e cresce suavemente para 6.0px ao cobrir a lateral esquerda
                    double thickness;
                    if (t < 0.55) {
                        thickness = 2.2;
                    } else {
                        double growT = Math.max(0.0, Math.min(1.0, (t - 0.55) / 0.45));
                        double easeGrow = EASE.interpolate(0.0, 1.0, growT);
                        thickness = 2.2 + easeGrow * 3.8;
                    }

                    Color coreColor = Color.web("#f3ad50").interpolate(Color.web("#ff9800"), easeT);
                    Color glowColor = Color.color(coreColor.getRed(), coreColor.getGreen(), coreColor.getBlue(), 0.38);

                drawDropdownStrokeSegment(gc, sTail, sHead, thickness + 4.0, glowColor,
                        x0, y0, x1, y1, ySubBottom, r, l1, l2, l3, l4, l5, l6, l7Dropdown);

                drawDropdownStrokeSegment(gc, sTail, sHead, thickness, coreColor,
                        x0, y0, x1, y1, ySubBottom, r, l1, l2, l3, l4, l5, l6, l7Dropdown);

                // Ramificação conectada das linhas horizontais sob as opções de cadastro
                double ySub1 = PAD + verticalOffset + 36.0;
                double ySub2 = PAD + verticalOffset + 72.0;
                double yCurrentHead = y0 + r;
                if (sHead > sLeftTop) {
                    double spineDist = sHead - sLeftTop;
                    yCurrentHead = (y0 + r) + (spineDist / l7Dropdown) * (ySubBottom - (y0 + r));
                }
                drawDropdownBranches(gc, yCurrentHead, x0, x1, ySub1, ySub2, glowColor, 1.2 + 2.0);
                drawDropdownBranches(gc, yCurrentHead, x0, x1, ySub1, ySub2, coreColor, 1.2);

                if (t >= 1.0) {
                    stop();
                    ButtonBorderLapAnimator.finalizarAnimacaoAtiva();
                }
            } catch (Exception ex) {
                stop();
                ButtonBorderLapAnimator.finalizarAnimacaoAtiva();
            }
        }
    };

        ButtonBorderLapAnimator.registrarAnimacaoAtiva(canvas, btn, timer, cleanup);
        timer.start();
    }

    private static void aplicarEstilosImediatos(Button btn, Pane submenu) {
        if (!btn.getStyleClass().contains("btn-dropdown-ativo")) {
            btn.getStyleClass().add("btn-dropdown-ativo");
        }
        if (submenu != null && !submenu.getStyleClass().contains("submenu-lateral-ativo")) {
            submenu.getStyleClass().add("submenu-lateral-ativo");
        }
    }

    private static void drawDropdownBranches(GraphicsContext gc, double yHead, double x0, double x1,
            double ySub1, double ySub2, Color color, double width) {
        if (yHead >= ySub1) {
            double t1 = Math.min(1.0, Math.max(0.0, (yHead - ySub1) / 10.0));
            double xEnd1 = x0 + t1 * (x1 - x0);
            gc.save();
            gc.setStroke(color);
            gc.setLineWidth(width);
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.strokeLine(x0, ySub1, xEnd1, ySub1);
            gc.restore();
        }
        if (yHead >= ySub2) {
            double t2 = Math.min(1.0, Math.max(0.0, (yHead - ySub2) / 10.0));
            double xEnd2 = x0 + t2 * (x1 - x0);
            gc.save();
            gc.setStroke(color);
            gc.setLineWidth(width);
            gc.setLineCap(StrokeLineCap.ROUND);
            gc.strokeLine(x0, ySub2, xEnd2, ySub2);
            gc.restore();
        }
    }

    private static void drawDropdownStrokeSegment(GraphicsContext gc, double sStart, double sEnd, double thickness,
            Color color,
            double x0, double y0, double x1, double y1, double ySubBottom, double r,
            double l1, double l2, double l3, double l4, double l5, double l6, double l7Dropdown) {
        if (sEnd <= sStart) {
            return;
        }
        gc.save();
        gc.setStroke(color);
        gc.setLineWidth(thickness);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);

        gc.beginPath();
        Point2D p0 = getDropdownPointAtDistance(sStart, x0, y0, x1, y1, ySubBottom, r, l1, l2, l3, l4, l5, l6,
                l7Dropdown);
        gc.moveTo(p0.getX(), p0.getY());

        double step = 2.0;
        for (double s = sStart + step; s < sEnd; s += step) {
            Point2D p = getDropdownPointAtDistance(s, x0, y0, x1, y1, ySubBottom, r, l1, l2, l3, l4, l5, l6,
                    l7Dropdown);
            gc.lineTo(p.getX(), p.getY());
        }

        Point2D pEnd = getDropdownPointAtDistance(sEnd, x0, y0, x1, y1, ySubBottom, r, l1, l2, l3, l4, l5, l6,
                l7Dropdown);
        gc.lineTo(pEnd.getX(), pEnd.getY());

        gc.stroke();
        gc.restore();
    }

    private static Point2D getDropdownPointAtDistance(double s,
            double x0, double y0, double x1, double y1, double ySubBottom, double r,
            double l1, double l2, double l3, double l4, double l5, double l6, double l7Dropdown) {
        // 1. Base do botão (da esquerda para a direita)
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

        // 7. Lateral esquerda estendida (descendo pela borda esquerda do botão e das 2
        // opções)
        double u = Math.min(1.0, Math.max(0.0, s / l7Dropdown));
        return new Point2D(x0, (y0 + r) + u * l7Dropdown);
    }
}
