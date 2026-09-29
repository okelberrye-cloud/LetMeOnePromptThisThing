package edu.csusm.cards.display;

import edu.csusm.cards.model.Card;
import edu.csusm.cards.model.Deck;
import edu.csusm.cards.ui.CardImages;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Part 1: shows all 52 cards on a green table in a 13 x 4 grid and
 * shuffles them when the Shuffle button is pressed.
 */
public class CardDisplayView extends BorderPane {

    private static final int COLUMNS = 13;
    private static final double CARD_WIDTH = 82;

    private final Deck deck = new Deck();
    private final List<ImageView> slots = new ArrayList<>(52);
    private final Random random = new Random();
    private final Label status = new Label();
    private final Button shuffleButton = new Button("Shuffle");
    private final Button sortButton = new Button("Sort");
    private int shuffleCount = 0;

    public CardDisplayView(Runnable onBack) {
        getStyleClass().add("felt");
        setPadding(new Insets(20, 24, 20, 24));

        Label title = new Label("All 52 Cards");
        title.getStyleClass().add("title-small");
        status.getStyleClass().add("subtitle");
        VBox header = new VBox(4, title, status);
        header.setAlignment(Pos.CENTER);
        setTop(header);

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        for (int i = 0; i < 52; i++) {
            ImageView view = new ImageView();
            view.setFitWidth(CARD_WIDTH);
            view.setPreserveRatio(true);
            view.setSmooth(true);
            view.setEffect(new DropShadow(6, 2, 3, javafx.scene.paint.Color.rgb(0, 0, 0, 0.45)));
            slots.add(view);
            grid.add(view, i % COLUMNS, i / COLUMNS);
        }
        setCenter(grid);

        shuffleButton.getStyleClass().add("action-button");
        shuffleButton.setDefaultButton(true);
        shuffleButton.setOnAction(e -> {
            deck.reset();
            deck.shuffle();
            shuffleCount++;
            animateDeal();
        });

        sortButton.getStyleClass().add("secondary-button");
        sortButton.setOnAction(e -> {
            deck.reset();
            animateDeal();
        });

        Button back = new Button("Back to Menu");
        back.getStyleClass().add("secondary-button");
        back.setOnAction(e -> onBack.run());

        HBox controls = new HBox(14, shuffleButton, sortButton, back);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(16, 0, 0, 0));
        setBottom(controls);

        showDeck();
    }

    /** Puts each card's image in its grid slot, in deck order. */
    private void showDeck() {
        List<Card> cards = deck.cards();
        for (int i = 0; i < cards.size(); i++) {
            slots.get(i).setImage(CardImages.front(cards.get(i)));
        }
        status.setText(shuffleCount == 0
                ? "Sorted by suit. Press Shuffle to mix them up."
                : "Shuffled " + shuffleCount + (shuffleCount == 1 ? " time" : " times"));
    }

    /** Cards spin away, swap to the new order, then pop back in one by one. */
    private void animateDeal() {
        shuffleButton.setDisable(true);
        sortButton.setDisable(true);

        ParallelTransition out = new ParallelTransition();
        for (ImageView view : slots) {
            ScaleTransition shrink = new ScaleTransition(Duration.millis(220), view);
            shrink.setToX(0);
            shrink.setToY(0);
            RotateTransition spin = new RotateTransition(Duration.millis(220), view);
            spin.setToAngle(random.nextInt(360) - 180);
            out.getChildren().addAll(shrink, spin);
        }

        ParallelTransition in = new ParallelTransition();
        for (int i = 0; i < slots.size(); i++) {
            ImageView view = slots.get(i);
            ScaleTransition grow = new ScaleTransition(Duration.millis(180), view);
            grow.setFromX(0);
            grow.setFromY(0);
            grow.setToX(1);
            grow.setToY(1);
            grow.setInterpolator(Interpolator.EASE_OUT);
            RotateTransition straighten = new RotateTransition(Duration.millis(180), view);
            straighten.setToAngle(0);
            ParallelTransition one = new ParallelTransition(grow, straighten);
            one.setDelay(Duration.millis(i * 9));
            in.getChildren().add(one);
        }

        out.setOnFinished(e -> showDeck());
        SequentialTransition all = new SequentialTransition(out, in);
        all.setOnFinished(e -> {
            shuffleButton.setDisable(false);
            sortButton.setDisable(false);
        });
        all.play();
    }
}
