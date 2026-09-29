package edu.csusm.cards;

import edu.csusm.cards.display.CardDisplayView;
import edu.csusm.cards.ui.GoblinGambleView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Main window. Shows a menu that opens either the Part 1 card display
 * or the Part 4 Goblin Gamble game. All screens share one Scene; we just
 * swap its root node.
 */
public class CardGameApp extends Application {

    public static final double WIDTH = 1220;
    public static final double HEIGHT = 780;

    private Scene scene;

    @Override
    public void start(Stage stage) {
        scene = new Scene(buildMenu(), WIDTH, HEIGHT);
        scene.getStylesheets().add(
                CardGameApp.class.getResource("/styles/app.css").toExternalForm());

        stage.setTitle("Lab 5: Card Display + Goblin Gamble");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(720);
        stage.show();
    }

    private void show(Parent root) {
        scene.setRoot(root);
    }

    private Parent buildMenu() {
        Label title = new Label("LAB 5 CARD TABLE");
        title.getStyleClass().add("title");

        Label subtitle = new Label("Pick a mode");
        subtitle.getStyleClass().add("subtitle");

        Button display = new Button("Part 1: Card Display + Shuffle");
        display.getStyleClass().addAll("big-button");
        display.setOnAction(e -> show(new CardDisplayView(() -> show(buildMenu()))));

        Button game = new Button("Part 4: Goblin Gamble");
        game.getStyleClass().addAll("big-button", "goblin-button");
        game.setOnAction(e -> show(new GoblinGambleView(() -> show(buildMenu()))));

        Label credit = new Label("Card faces: Byron Knoll, \"Vector Playing Cards\" (public domain / CC0, via OpenGameArt.org)");
        credit.getStyleClass().add("credit");

        VBox menu = new VBox(22, title, subtitle, display, game, credit);
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(40));
        menu.getStyleClass().add("felt");
        VBox.setMargin(credit, new Insets(40, 0, 0, 0));
        return menu;
    }
}
