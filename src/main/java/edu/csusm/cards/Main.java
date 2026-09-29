package edu.csusm.cards;

import javafx.application.Application;

/**
 * Entry point. Kept separate from the Application subclass so the app
 * runs from an IDE "Run" button without extra JavaFX module flags.
 */
public class Main {
    public static void main(String[] args) {
        Application.launch(CardGameApp.class, args);
    }
}
