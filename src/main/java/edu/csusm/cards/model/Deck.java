package edu.csusm.cards.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** A standard 52-card deck. */
public class Deck {

    private final List<Card> cards = new ArrayList<>(52);
    private final Random random;

    public Deck() {
        this(new Random());
    }

    public Deck(Random random) {
        this.random = random;
        reset();
    }

    /** Puts all 52 cards back in sorted order (suit by suit, 2 through Ace). */
    public void reset() {
        cards.clear();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    /** Randomly reorders the deck (Fisher-Yates via Collections.shuffle). */
    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    /** Removes and returns the top card. */
    public Card draw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty");
        }
        return cards.remove(0);
    }

    public int size() {
        return cards.size();
    }

    /** Read-only view of the cards in their current order. */
    public List<Card> cards() {
        return Collections.unmodifiableList(cards);
    }
}
