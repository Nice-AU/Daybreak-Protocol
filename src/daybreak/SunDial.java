package daybreak;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;

final class SunDial extends Region {
    private final Canvas canvas = new Canvas();
    private double lightFraction = 1.0;

    SunDial() {
        getChildren().add(canvas);
        setMinHeight(150);
        setPrefHeight(190);
        widthProperty().addListener((obs, oldValue, newValue) -> draw());
        heightProperty().addListener((obs, oldValue, newValue) -> draw());
    }

    void setLightFraction(double value) {
        lightFraction = Math.max(0.0, Math.min(1.0, value));
        draw();
    }

    @Override
    protected void layoutChildren() {
        canvas.setWidth(getWidth());
        canvas.setHeight(getHeight());
        draw();
    }

    private void draw() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }

        GraphicsContext gc = canvas.getGraphicsContext2D();
        Color top = Color.web(lightFraction > 0.45 ? "#10235f" : "#090b24");
        Color horizon = Color.web("#24133d").interpolate(Color.web("#f7a941"), lightFraction);
        gc.setFill(new LinearGradient(0, 0, 0, height, false, CycleMethod.NO_CYCLE,
                new Stop(0, top), new Stop(1, horizon)));
        gc.fillRect(0, 0, width, height);

        gc.setGlobalAlpha(0.25);
        gc.setStroke(Color.web("#d7dcff"));
        gc.setLineWidth(1.5);
        for (int i = 1; i <= 4; i++) {
            double inset = i * 28.0;
            gc.strokeArc(inset, height * 0.20 + inset * 0.2, width - inset * 2,
                    height * 1.32 - inset * 0.4, 12, 156, javafx.scene.shape.ArcType.OPEN);
        }
        gc.setGlobalAlpha(1.0);

        double x = 32 + lightFraction * (width - 64);
        double arc = Math.sin(lightFraction * Math.PI);
        double y = height - 27 - arc * (height - 62);
        double radius = 17 + 8 * lightFraction;
        gc.setEffect(new Glow(0.85));
        gc.setFill(Color.web("#ff7657").interpolate(Color.web("#fff4ad"), lightFraction));
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        gc.setEffect(null);

        gc.setFill(Color.web("#070a1b"));
        gc.fillRect(0, height - 24, width, 24);
        gc.setStroke(Color.web("#79f2c0"));
        gc.setGlobalAlpha(0.3);
        gc.strokeLine(0, height - 24, width, height - 24);
        gc.setGlobalAlpha(1.0);
    }
}
