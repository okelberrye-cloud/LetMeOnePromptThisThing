package edu.csusm.cards.ui;

import edu.csusm.cards.game.EventType;
import edu.csusm.cards.game.GameEvent;
import edu.csusm.cards.game.GameStatus;
import edu.csusm.cards.game.GoblinGame;
import edu.csusm.cards.game.Guess;
import edu.csusm.cards.game.TurnResult;
import edu.csusm.cards.model.Card;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Part 4: the Goblin Gamble screen.
 * All the rules live in {@link GoblinGame}; this class only draws and animates.
 */
public class GoblinGambleView extends StackPane {

    private static final double CURRENT_WIDTH = 170;
    private static final double VAULT_WIDTH = 70;

    private final Runnable onBack;
    private GoblinGame game;

    // stats bar
    private final Label heartsFull = new Label();
    private final Label heartsEmpty = new Label();
    private final Label scoreValue = new Label();
    private final Label vaultValue = new Label();
    private final List<Rectangle> angerBars = new ArrayList<>();

    // table
    private final ImageView currentCard = cardView(CURRENT_WIDTH);
    private final Label badge = new Label();
    private final Label speech = new Label();
    private final GoblinView goblin = new GoblinView();
    private final List<ImageView> vaultSlots = new ArrayList<>();
    private final HBox vaultRow = new HBox(8);

    // controls
    private final Button higher = new Button("HIGHER ▲");
    private final Button lower = new Button("LOWER ▼");
    private final Button freeFlip = new Button("FREE FLIP (Jack's Pass)");
    private final Label log = new Label();

    // end screen
    private final StackPane overlay = new StackPane();
    private final Label overlayTitle = new Label();
    private final Label overlayReason = new Label();
    private final Label overlayScore = new Label();

    private boolean busy = false;

    public GoblinGambleView(Runnable onBack) {
        this.onBack = onBack;
        getStyleClass().add("felt");

        BorderPane board = new BorderPane();
        board.setTop(buildStatsBar());
        board.setCenter(buildTable());
        board.setBottom(buildBottom());

        buildOverlay();
        getChildren().addAll(board, overlay);

        addEventFilter(KeyEvent.KEY_PRESSED, this::onKey);
        newGame();
    }

    // ------------------------------------------------------------------ layout

    private Node buildStatsBar() {
        Label title = new Label("GOBLIN GAMBLE");
        title.getStyleClass().add("game-title");

        heartsFull.getStyleClass().add("hearts");
        heartsEmpty.getStyleClass().add("hearts-empty");
        HBox hearts = new HBox(0, heartsFull, heartsEmpty);
        hearts.setAlignment(Pos.CENTER_LEFT);

        HBox meter = new HBox(4);
        meter.setAlignment(Pos.CENTER_LEFT);
        for (int i = 0; i < GoblinGame.MAX_ANGER; i++) {
            Rectangle bar = new Rectangle(26, 18);
            bar.setArcWidth(6);
            bar.setArcHeight(6);
            bar.setStroke(Color.rgb(0, 0, 0, 0.5));
            angerBars.add(bar);
            meter.getChildren().add(bar);
        }

        scoreValue.getStyleClass().add("stat-value");
        vaultValue.getStyleClass().add("stat-value");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(34, title, spacer,
                stat("LIVES", hearts), stat("SCORE", scoreValue),
                stat("GOBLIN ANGER", meter), stat("VAULT", vaultValue));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("stats-bar");
        return bar;
    }

    private Node stat(String name, Node value) {
        Label label = new Label(name);
        label.getStyleClass().add("stat-label");
        VBox box = new VBox(2, label, value);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private Node buildTable() {
        // current card
        Label currentLabel = new Label("CURRENT CARD");
        currentLabel.getStyleClass().add("stat-label");
        badge.getStyleClass().add("badge");
        badge.setVisible(false);
        VBox currentCol = new VBox(10, currentLabel, currentCard, badge);
        currentCol.setAlignment(Pos.CENTER);
        currentCol.setMinWidth(220);

        // goblin + speech bubble
        speech.getStyleClass().add("speech");
        speech.setWrapText(true);
        speech.setMaxWidth(330);
        speech.setMinHeight(64);
        Polygon tail = new Polygon(0, 0, 26, 0, 8, 20);
        tail.setFill(Color.WHITE);
        tail.setStroke(Color.web("#222"));
        tail.setStrokeWidth(3);
        VBox bubble = new VBox(-3, speech, tail);
        bubble.setAlignment(Pos.CENTER);
        StackPane goblinHolder = new StackPane(goblin);
        goblinHolder.setMinSize(240, 230);
        VBox goblinCol = new VBox(0, bubble, goblinHolder);
        goblinCol.setAlignment(Pos.CENTER);
        goblinCol.setMinWidth(360);

        HBox table = new HBox(40, currentCol, goblinCol, buildRules());
        table.setAlignment(Pos.CENTER);
        table.setPadding(new Insets(14, 24, 6, 24));
        return table;
    }

    private Node buildRules() {
        Label title = new Label("HOW TO PLAY");
        title.getStyleClass().add("rules-title");
        Label body = new Label(String.join("\n",
                "Guess if the next vault card is higher",
                "or lower. Aces high. Ties count as right.",
                "",
                "Right: +10        Wrong: -1 life",
                "",
                "♥  +1 life (max 5)",
                "♠  double points",
                "♦  +25 bonus coins",
                "♣  +1 goblin anger",
                "J   next flip is free",
                "Q   peek at the next card",
                "K   goblin steals 30 points",
                "",
                "Anger 3+: TANTRUM (vault shuffle)",
                "Anger 5: goblin eats the deck",
                "",
                "Win: empty the vault with a life left.",
                "Keys: ↑ higher  ↓ lower  Space free flip"));
        body.getStyleClass().add("rules-text");
        VBox panel = new VBox(8, title, body);
        panel.getStyleClass().add("panel");
        panel.setMinWidth(290);
        panel.setMaxWidth(310);
        return panel;
    }

    private Node buildBottom() {
        Label vaultLabel = new Label("THE VAULT");
        vaultLabel.getStyleClass().add("stat-label");
        vaultRow.setAlignment(Pos.CENTER);
        for (int i = 0; i < GoblinGame.VAULT_SIZE; i++) {
            ImageView slot = cardView(VAULT_WIDTH);
            vaultSlots.add(slot);
            vaultRow.getChildren().add(slot);
        }

        higher.getStyleClass().addAll("guess-button", "higher");
        lower.getStyleClass().addAll("guess-button", "lower");
        freeFlip.getStyleClass().addAll("guess-button", "free");
        higher.setOnAction(e -> takeTurn(Guess.HIGHER));
        lower.setOnAction(e -> takeTurn(Guess.LOWER));
        freeFlip.setOnAction(e -> takeTurn(Guess.FREE));
        HBox guesses = new HBox(18, higher, lower, freeFlip);
        guesses.setAlignment(Pos.CENTER);

        log.getStyleClass().add("log");
        log.setWrapText(true);
        log.setMinHeight(48);
        log.setMaxWidth(900);
        log.setAlignment(Pos.CENTER);

        Button restart = new Button("New Game");
        restart.getStyleClass().add("secondary-button");
        restart.setOnAction(e -> newGame());
        Button menu = new Button("Back to Menu");
        menu.getStyleClass().add("secondary-button");
        menu.setOnAction(e -> onBack.run());
        HBox nav = new HBox(12, restart, menu);
        nav.setAlignment(Pos.CENTER);

        VBox bottom = new VBox(10, vaultLabel, vaultRow, guesses, log, nav);
        bottom.setAlignment(Pos.CENTER);
        bottom.setPadding(new Insets(4, 20, 16, 20));
        return bottom;
    }

    private void buildOverlay() {
        overlayTitle.getStyleClass().add("overlay-title");
        overlayReason.getStyleClass().add("overlay-reason");
        overlayScore.getStyleClass().add("overlay-score");
        overlayReason.setWrapText(true);
        overlayReason.setMaxWidth(640);

        Button again = new Button("Play Again");
        again.getStyleClass().add("big-button");
        again.setOnAction(e -> newGame());
        Button menu = new Button("Back to Menu");
        menu.getStyleClass().add("secondary-button");
        menu.setOnAction(e -> onBack.run());

        VBox box = new VBox(18, overlayTitle, overlayReason, overlayScore, again, menu);
        box.setAlignment(Pos.CENTER);
        overlay.getChildren().add(box);
        overlay.getStyleClass().add("overlay");
        overlay.setVisible(false);
    }

    private static ImageView cardView(double width) {
        ImageView view = new ImageView();
        view.setFitWidth(width);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        view.setEffect(new DropShadow(8, 2, 4, Color.rgb(0, 0, 0, 0.5)));
        return view;
    }

    // ------------------------------------------------------------------ game flow

    private void newGame() {
        game = new GoblinGame();
        busy = false;
        overlay.setVisible(false);
        goblin.setRotate(0);
        goblin.setScaleX(1);
        goblin.setScaleY(1);
        goblin.setAnger(0);
        goblin.setMood(GoblinView.Mood.SMUG);
        speech.setText(Taunts.intro());
        log.setText("The goblin dealt 13 cards into the vault. Is the next one higher or lower than "
                + game.current() + "?");
        currentCard.setImage(CardImages.front(game.current()));
        refresh();
        requestFocus();
    }

    private void onKey(KeyEvent e) {
        if (busy || game.status().isOver()) {
            if (e.getCode() == KeyCode.N) {
                newGame();
            }
            return;
        }
        if (game.freePassReady() && (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER)) {
            takeTurn(Guess.FREE);
            e.consume();
        } else if (!game.freePassReady() && e.getCode() == KeyCode.UP) {
            takeTurn(Guess.HIGHER);
            e.consume();
        } else if (!game.freePassReady() && e.getCode() == KeyCode.DOWN) {
            takeTurn(Guess.LOWER);
            e.consume();
        } else if (e.getCode() == KeyCode.N) {
            newGame();
        }
    }

    private void takeTurn(Guess guess) {
        if (busy || game.status().isOver()) {
            return;
        }
        busy = true;
        setGuessButtonsDisabled(true);

        TurnResult result = game.play(guess);
        int slotIndex = game.revealedCards().size() - 1;
        Image front = CardImages.front(result.revealed());

        Animation flipVault = flip(vaultSlots.get(slotIndex), front, 140);
        Animation flipCurrent = flip(currentCard, front, 150);

        SequentialTransition turn = new SequentialTransition(flipVault, flipCurrent);
        turn.setOnFinished(e -> react(result));
        turn.play();
    }

    /** After the flip: update the numbers, make the goblin react, then unlock. */
    private void react(TurnResult result) {
        List<EventType> types = result.events().stream().map(GameEvent::type).toList();
        EventType headline = Taunts.mostDramatic(types);

        refresh();
        speech.setText(Taunts.line(headline));
        log.setText(result.events().stream().map(GameEvent::message).collect(Collectors.joining("   ")));
        goblin.setAnger(game.anger());

        Animation reaction;
        switch (headline) {
            case LOSE_GOBLIN, LOSE_NO_LIVES -> {
                goblin.setMood(GoblinView.Mood.CHOMP);
                reaction = goblin.chomp();
            }
            case WIN -> {
                goblin.setMood(GoblinView.Mood.CRYING);
                reaction = goblin.shake();
            }
            case TANTRUM_SHUFFLE -> {
                goblin.setMood(GoblinView.Mood.FURIOUS);
                reaction = new ParallelTransition(goblin.tantrumSpin(), shakeVault());
            }
            case WRONG, KING_STEAL -> {
                goblin.setMood(GoblinView.Mood.LAUGHING);
                reaction = goblin.bounce();
            }
            case CLUB_ANGER -> {
                goblin.setMood(GoblinView.Mood.FURIOUS);
                reaction = goblin.shake();
            }
            default -> {
                goblin.setMood(GoblinView.Mood.ANNOYED);
                reaction = new PauseTransition(Duration.millis(150));
            }
        }

        reaction.setOnFinished(e -> {
            if (result.status().isOver()) {
                PauseTransition wait = new PauseTransition(Duration.millis(500));
                wait.setOnFinished(x -> showEnd(result.status()));
                wait.play();
            } else {
                busy = false;
                refresh();
            }
        });
        reaction.play();
    }

    private void showEnd(GameStatus status) {
        int cracked = game.revealedCards().size();
        switch (status) {
            case WON -> {
                overlayTitle.setText("VAULT CRACKED!");
                overlayTitle.setStyle("-fx-text-fill: #f6e27a;");
                overlayReason.setText("You emptied all 13 cards and the goblin is crying into his crown.");
            }
            case LOST_NO_LIVES -> {
                overlayTitle.setText("GOBLIN WINS");
                overlayTitle.setStyle("-fx-text-fill: #ff5a4e;");
                overlayReason.setText("You ran out of lives after cracking " + cracked + " of 13 cards.");
            }
            case LOST_GOBLIN_ATE_DECK -> {
                overlayTitle.setText("GOBLIN WINS");
                overlayTitle.setStyle("-fx-text-fill: #ff5a4e;");
                overlayReason.setText("Anger hit 5/5 and the goblin ATE THE DECK after "
                        + cracked + " of 13 cards.");
            }
            default -> {
            }
        }
        overlayScore.setText("Final score: " + game.score());
        overlay.setOpacity(0);
        overlay.setVisible(true);
        FadeTransition fade = new FadeTransition(Duration.millis(400), overlay);
        fade.setToValue(1);
        fade.play();
    }

    // ------------------------------------------------------------------ drawing

    /** Redraws stats, the vault row, and which buttons are showing. */
    private void refresh() {
        int lives = game.lives();
        heartsFull.setText("♥".repeat(Math.max(0, lives)));
        heartsEmpty.setText("♡".repeat(Math.max(0, GoblinGame.MAX_LIVES - lives)));
        scoreValue.setText(String.valueOf(game.score()));
        vaultValue.setText(game.cardsLeft() + " left");

        for (int i = 0; i < angerBars.size(); i++) {
            boolean lit = i < game.anger();
            Color on = i >= 2 ? Color.web("#ff4b2b") : Color.web("#ffb02e");
            angerBars.get(i).setFill(lit ? on : Color.rgb(0, 0, 0, 0.35));
        }

        List<Card> revealed = game.revealedCards();
        Card peek = game.peeked();
        for (int i = 0; i < vaultSlots.size(); i++) {
            ImageView slot = vaultSlots.get(i);
            slot.setOpacity(1);
            slot.setTranslateY(0);
            if (i < revealed.size()) {
                slot.setImage(CardImages.front(revealed.get(i)));
                slot.setOpacity(0.55);
            } else if (i == revealed.size() && peek != null) {
                slot.setImage(CardImages.front(peek));
                slot.setTranslateY(-12);
                slot.setEffect(new DropShadow(22, Color.web("#f6e27a")));
                continue;
            } else {
                slot.setImage(CardImages.back());
                if (i == revealed.size() && !game.status().isOver()) {
                    slot.setTranslateY(-12);
                }
            }
            slot.setEffect(new DropShadow(8, 2, 4, Color.rgb(0, 0, 0, 0.5)));
        }

        boolean free = game.freePassReady();
        higher.setVisible(!free);
        higher.setManaged(!free);
        lower.setVisible(!free);
        lower.setManaged(!free);
        freeFlip.setVisible(free);
        freeFlip.setManaged(free);

        if (free) {
            badge.setText("JACK'S FREE PASS READY");
            badge.setVisible(true);
        } else if (peek != null) {
            badge.setText("QUEEN PEEK: NEXT IS " + peek);
            badge.setVisible(true);
        } else {
            badge.setVisible(false);
        }

        setGuessButtonsDisabled(busy || game.status().isOver());
    }

    private void setGuessButtonsDisabled(boolean disabled) {
        higher.setDisable(disabled);
        lower.setDisable(disabled);
        freeFlip.setDisable(disabled);
    }

    /** Card flip: squash to 0 width, swap the image, stretch back out. */
    private Animation flip(ImageView view, Image newImage, int halfMillis) {
        ScaleTransition shrink = new ScaleTransition(Duration.millis(halfMillis), view);
        shrink.setFromX(1);
        shrink.setToX(0);
        shrink.setOnFinished(e -> {
            view.setImage(newImage);
            view.setOpacity(1);
        });
        ScaleTransition grow = new ScaleTransition(Duration.millis(halfMillis), view);
        grow.setFromX(0);
        grow.setToX(1);
        return new SequentialTransition(shrink, grow);
    }

    /** The face-down vault cards wobble while the goblin shuffles them. */
    private Animation shakeVault() {
        ParallelTransition all = new ParallelTransition();
        int start = game.revealedCards().size();
        for (int i = start; i < vaultSlots.size(); i++) {
            RotateTransition wobble = new RotateTransition(Duration.millis(70), vaultSlots.get(i));
            wobble.setFromAngle(-14);
            wobble.setToAngle(14);
            wobble.setAutoReverse(true);
            wobble.setCycleCount(10);
            wobble.setOnFinished(e -> ((Node) ((RotateTransition) e.getSource()).getNode()).setRotate(0));
            all.getChildren().add(wobble);
        }
        return all;
    }
}
