package edu.csusm.cards.model;

/** A single playing card. Immutable. */
public record Card(Rank rank, Suit suit) {

    /** Image file name inside /cards, e.g. "queen_of_hearts.png". */
    public String imageFile() {
        return rank.fileName() + "_of_" + suit.fileName() + ".png";
    }

    /** Short label like "Q♥". */
    public String label() {
        return rank.shortName() + suit.symbol();
    }

    @Override
    public String toString() {
        return label();
    }
}
