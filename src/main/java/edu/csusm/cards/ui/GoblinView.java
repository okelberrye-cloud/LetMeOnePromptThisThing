package edu.csusm.cards.ui;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/**
 * The goblin, drawn with plain JavaFX shapes (no image files).
 * Its face turns red as anger rises, and it has a few moods for reactions.
 */
public class GoblinView extends Group {

    public enum Mood { SMUG, LAUGHING, ANNOYED, FURIOUS, CRYING, CHOMP }

    private static final Color CALM_SKIN = Color.web("#74b347");
    private static final Color ANGRY_SKIN = Color.web("#d9472b");
    private static final Color OUTLINE = Color.web("#1f3a12");

    private final Ellipse head = new Ellipse(110, 120, 70, 60);
    private final Polygon leftEar = new Polygon(52, 102, 0, 62, 56, 136);
    private final Polygon rightEar = new Polygon(168, 102, 220, 62, 164, 136);
    private final Ellipse nose = new Ellipse(110, 130, 9, 12);
    private final Line leftBrow = new Line();
    private final Line rightBrow = new Line();
    private final Circle leftPupil = new Circle(87, 112, 6, Color.BLACK);
    private final Circle rightPupil = new Circle(133, 112, 6, Color.BLACK);
    private final Arc mouth = new Arc();
    private final Group teeth = new Group();
    private final Group tears = new Group();

    private int anger = 0;

    public GoblinView() {
        for (Polygon ear : new Polygon[]{leftEar, rightEar}) {
            ear.setStroke(OUTLINE);
            ear.setStrokeWidth(3);
        }
        head.setStroke(OUTLINE);
        head.setStrokeWidth(3);

        Polygon crown = new Polygon(84, 66, 90, 38, 101, 56, 110, 30, 119, 56, 130, 38, 136, 66);
        crown.setFill(Color.web("#e8bf3a"));
        crown.setStroke(Color.web("#8a6a10"));
        crown.setStrokeWidth(2.5);
        crown.setRotate(-8);
        Circle gem = new Circle(110, 56, 5, Color.web("#c0203a"));

        Ellipse leftEye = new Ellipse(87, 110, 15, 17);
        Ellipse rightEye = new Ellipse(133, 110, 15, 17);
        for (Ellipse eye : new Ellipse[]{leftEye, rightEye}) {
            eye.setFill(Color.web("#fffbe6"));
            eye.setStroke(OUTLINE);
            eye.setStrokeWidth(2);
        }

        for (Line brow : new Line[]{leftBrow, rightBrow}) {
            brow.setStroke(OUTLINE);
            brow.setStrokeWidth(5);
            brow.setStrokeLineCap(StrokeLineCap.ROUND);
        }

        nose.setStroke(OUTLINE);
        nose.setStrokeWidth(1.5);

        mouth.setFill(Color.web("#5a1414"));
        mouth.setStroke(OUTLINE);
        mouth.setStrokeWidth(2.5);
        mouth.setType(ArcType.CHORD);

        Polygon tooth1 = new Polygon(96, 151, 104, 151, 100, 161);
        Polygon tooth2 = new Polygon(116, 151, 124, 151, 120, 161);
        tooth1.setFill(Color.WHITE);
        tooth2.setFill(Color.WHITE);
        teeth.getChildren().addAll(tooth1, tooth2);

        Ellipse tearL = new Ellipse(78, 138, 5, 9);
        Ellipse tearR = new Ellipse(142, 138, 5, 9);
        tearL.setFill(Color.web("#5bc0ff"));
        tearR.setFill(Color.web("#5bc0ff"));
        tears.getChildren().addAll(tearL, tearR);

        getChildren().addAll(leftEar, rightEar, head, crown, gem, leftEye, rightEye,
                leftPupil, rightPupil, leftBrow, rightBrow, nose, mouth, teeth, tears);

        setAnger(0);
        setMood(Mood.SMUG);
    }

    /** 0 to 5. Changes skin color and eyebrow angle. */
    public void setAnger(int anger) {
        this.anger = Math.max(0, Math.min(5, anger));
        Color skin = CALM_SKIN.interpolate(ANGRY_SKIN, this.anger / 5.0);
        head.setFill(skin);
        leftEar.setFill(skin.darker());
        rightEar.setFill(skin.darker());
        nose.setFill(skin.darker());

        double inner = 86 + this.anger * 3.2;  // inner brow drops as anger rises
        leftBrow.setStartX(68);
        leftBrow.setStartY(86);
        leftBrow.setEndX(100);
        leftBrow.setEndY(inner);
        rightBrow.setStartX(120);
        rightBrow.setStartY(inner);
        rightBrow.setEndX(152);
        rightBrow.setEndY(86);
    }

    public void setMood(Mood mood) {
        tears.setVisible(mood == Mood.CRYING);
        teeth.setVisible(mood != Mood.CRYING);
        leftPupil.setCenterY(112);
        rightPupil.setCenterY(112);

        switch (mood) {
            case SMUG -> smile(26, 12);
            case LAUGHING -> smile(34, 24);
            case ANNOYED -> flat();
            case FURIOUS -> frown(30, 14);
            case CRYING -> {
                frown(24, 12);
                leftPupil.setCenterY(118);
                rightPupil.setCenterY(118);
            }
            case CHOMP -> smile(40, 34);
        }
    }

    private void smile(double width, double depth) {
        mouth.setCenterX(110);
        mouth.setCenterY(150);
        mouth.setRadiusX(width);
        mouth.setRadiusY(depth);
        mouth.setStartAngle(180);
        mouth.setLength(180);
        moveTeeth(151);
    }

    private void frown(double width, double depth) {
        mouth.setCenterX(110);
        mouth.setCenterY(166);
        mouth.setRadiusX(width);
        mouth.setRadiusY(depth);
        mouth.setStartAngle(0);
        mouth.setLength(180);
        moveTeeth(154);
    }

    private void flat() {
        mouth.setCenterX(110);
        mouth.setCenterY(154);
        mouth.setRadiusX(24);
        mouth.setRadiusY(5);
        mouth.setStartAngle(180);
        mouth.setLength(180);
        moveTeeth(154);
    }

    private void moveTeeth(double top) {
        teeth.setTranslateY(top - 151);
    }

    // ----- animations -----

    public Animation bounce() {
        TranslateTransition t = new TranslateTransition(Duration.millis(110), this);
        t.setByY(-18);
        t.setAutoReverse(true);
        t.setCycleCount(4);
        return t;
    }

    public Animation shake() {
        TranslateTransition t = new TranslateTransition(Duration.millis(45), this);
        t.setFromX(-10);
        t.setToX(10);
        t.setAutoReverse(true);
        t.setCycleCount(8);
        t.setOnFinished(e -> setTranslateX(0));
        return t;
    }

    public Animation tantrumSpin() {
        RotateTransition spin = new RotateTransition(Duration.millis(700), this);
        spin.setByAngle(720);
        spin.setInterpolator(Interpolator.EASE_BOTH);
        ScaleTransition puff = new ScaleTransition(Duration.millis(350), this);
        puff.setToX(1.25);
        puff.setToY(1.25);
        puff.setAutoReverse(true);
        puff.setCycleCount(2);
        return new ParallelTransition(spin, puff);
    }

    public Animation chomp() {
        ScaleTransition s = new ScaleTransition(Duration.millis(160), this);
        s.setToX(1.35);
        s.setToY(1.35);
        s.setAutoReverse(true);
        s.setCycleCount(6);
        return s;
    }
}
