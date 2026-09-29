package edu.csusm.cards.model;

/** The four suits in a standard deck. */
public enum Suit {
    CLUBS("clubs", "♣", false),
    DIAMONDS("diamonds", "♦", true),
    HEARTS("hearts", "♥", true),
    SPADES("spades", "♠", false);

    private final String fileName;
    private final String symbol;
    private final boolean red;

    Suit(String fileName, String symbol, boolean red) {
        this.fileName = fileName;
        this.symbol = symbol;
        this.red = red;
    }

    /** Name used in the card image file, e.g. "hearts". */
    public String fileName() {
        return fileName;
    }

    /** Unicode suit symbol, e.g. a heart. */
    public String symbol() {
        return symbol;
    }

    public boolean isRed() {
        return red;
    }
}
