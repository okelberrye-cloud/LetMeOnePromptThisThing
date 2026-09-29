package edu.csusm.cards.game;

/** Where the game is at. */
public enum GameStatus {
    PLAYING,
    /** Success state: the vault is empty and the player still has a life. */
    WON,
    /** Failure state: the player ran out of lives. */
    LOST_NO_LIVES,
    /** Failure state: the Goblin Anger Meter hit 5 and the goblin ate the deck. */
    LOST_GOBLIN_ATE_DECK;

    public boolean isOver() {
        return this != PLAYING;
    }
}
