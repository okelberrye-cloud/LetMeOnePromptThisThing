package edu.csusm.cards.model;

/** Card ranks, lowest to highest. Aces are high (value 14). */
public enum Rank {
    TWO(2, "2", "2"),
    THREE(3, "3", "3"),
    FOUR(4, "4", "4"),
    FIVE(5, "5", "5"),
    SIX(6, "6", "6"),
    SEVEN(7, "7", "7"),
    EIGHT(8, "8", "8"),
    NINE(9, "9", "9"),
    TEN(10, "10", "10"),
    JACK(11, "jack", "J"),
    QUEEN(12, "queen", "Q"),
    KING(13, "king", "K"),
    ACE(14, "ace", "A");

    private final int value;
    private final String fileName;
    private final String shortName;

    Rank(int value, String fileName, String shortName) {
        this.value = value;
        this.fileName = fileName;
        this.shortName = shortName;
    }

    /** Numeric value used for higher/lower comparisons. */
    public int value() {
        return value;
    }

    /** Name used in the card image file, e.g. "queen". */
    public String fileName() {
        return fileName;
    }

    /** Short label, e.g. "Q". */
    public String shortName() {
        return shortName;
    }

    public boolean isFace() {
        return this == JACK || this == QUEEN || this == KING;
    }
}
