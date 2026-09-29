package edu.csusm.cards.game;

import edu.csusm.cards.model.Card;
import edu.csusm.cards.model.Deck;
import edu.csusm.cards.model.Rank;
import edu.csusm.cards.model.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Game logic for Goblin Gamble. No JavaFX in here, so it can be unit tested.
 *
 * Turn order (see docs/GOBLIN_GAMBLE_SPEC.md):
 *   1. Flip the next vault card.
 *   2. Score the guess (+10, doubled on a Spade) or lose a life. Free flips skip this.
 *   3. Suit power always fires.
 *   4. Face card power fires.
 *   5. If Clubs pushed anger to 3 or 4, the goblin throws a tantrum and shuffles the vault.
 *   6. Queen peek happens after any tantrum, so the peek is always accurate.
 *   7. Check for win/loss.
 */
public class GoblinGame {

    public static final int VAULT_SIZE = 13;
    public static final int START_LIVES = 3;
    public static final int MAX_LIVES = 5;
    public static final int CORRECT_POINTS = 10;
    public static final int DIAMOND_BONUS = 25;
    public static final int KING_STEAL = 30;
    public static final int TANTRUM_ANGER = 3;
    public static final int MAX_ANGER = 5;

    private final Random random;
    private final List<Card> vault = new ArrayList<>();
    private final List<Card> revealed = new ArrayList<>();

    private Card current;
    private int lives = START_LIVES;
    private int score = 0;
    private int anger = 0;
    private boolean freePassReady = false;
    private Card peeked = null;
    private GameStatus status = GameStatus.PLAYING;

    /** Initial setup: shuffle, deal 13 face down into the vault, flip one more face up. */
    public GoblinGame(Random random) {
        this.random = random;
        Deck deck = new Deck(random);
        deck.shuffle();
        for (int i = 0; i < VAULT_SIZE; i++) {
            vault.add(deck.draw());
        }
        current = deck.draw();
    }

    public GoblinGame() {
        this(new Random());
    }

    /** Test hook: start from a known current card and vault order. */
    GoblinGame(Card start, List<Card> vaultOrder, Random random) {
        this.random = random;
        this.current = start;
        this.vault.addAll(vaultOrder);
    }

    /** Plays one turn. */
    public TurnResult play(Guess guess) {
        if (status.isOver()) {
            throw new IllegalStateException("The game is over");
        }
        if (guess == Guess.FREE && !freePassReady) {
            throw new IllegalArgumentException("No free pass available");
        }
        if (freePassReady) {
            guess = Guess.FREE; // Jack's free pass: this flip is not a guess
        }

        List<GameEvent> events = new ArrayList<>();
        Card previous = current;
        Card next = vault.remove(0);
        peeked = null;

        // 2. Score the guess
        if (guess == Guess.FREE) {
            freePassReady = false;
            events.add(new GameEvent(EventType.FREE_FLIP,
                    "Free Pass: " + next + " flipped with no guess needed."));
        } else if (isCorrect(guess, previous, next)) {
            int points = CORRECT_POINTS;
            if (next.suit() == Suit.SPADES) {
                points *= 2;
            }
            score += points;
            events.add(new GameEvent(EventType.CORRECT,
                    "Correct! " + next + " vs " + previous + ". +" + points + " points."));
            if (next.suit() == Suit.SPADES) {
                events.add(new GameEvent(EventType.SPADE_DOUBLE, "Spade: points doubled!"));
            }
        } else {
            lives--;
            events.add(new GameEvent(EventType.WRONG,
                    "Wrong! " + next + " vs " + previous + ". -1 life."));
        }

        // 3. Suit power (always fires)
        switch (next.suit()) {
            case HEARTS -> {
                if (lives < MAX_LIVES) {
                    lives++;
                    events.add(new GameEvent(EventType.HEART_LIFE, "Heart: +1 life."));
                } else {
                    events.add(new GameEvent(EventType.HEART_MAXED, "Heart: already at max lives."));
                }
            }
            case DIAMONDS -> {
                score += DIAMOND_BONUS;
                events.add(new GameEvent(EventType.DIAMOND_BONUS, "Diamond: +" + DIAMOND_BONUS + " bonus coins."));
            }
            case CLUBS -> {
                anger++;
                events.add(new GameEvent(EventType.CLUB_ANGER, "Club: goblin anger " + anger + "/" + MAX_ANGER + "."));
            }
            case SPADES -> {
                // Spade doubling is handled when scoring the guess.
            }
        }

        // 4. Face card power
        if (next.rank() == Rank.KING) {
            int stolen = Math.min(KING_STEAL, score);
            score -= stolen;
            events.add(new GameEvent(EventType.KING_STEAL, "King: the goblin steals " + stolen + " points."));
        } else if (next.rank() == Rank.JACK && !vault.isEmpty()) {
            freePassReady = true;
            events.add(new GameEvent(EventType.JACK_PASS, "Jack: your next flip is free."));
        }

        // 5. Tantrum
        if (next.suit() == Suit.CLUBS && anger >= TANTRUM_ANGER && anger < MAX_ANGER && vault.size() > 1) {
            Collections.shuffle(vault, random);
            events.add(new GameEvent(EventType.TANTRUM_SHUFFLE, "TANTRUM! The goblin shuffled the vault."));
        }

        // 6. Queen peek
        if (next.rank() == Rank.QUEEN && !vault.isEmpty()) {
            peeked = vault.get(0);
            events.add(new GameEvent(EventType.QUEEN_PEEK, "Queen: you peek at the next card. It's " + peeked + "."));
        }

        revealed.add(next);
        current = next;

        // 7. Win / loss
        if (lives <= 0) {
            status = GameStatus.LOST_NO_LIVES;
            events.add(new GameEvent(EventType.LOSE_NO_LIVES, "Out of lives. GOBLIN WINS."));
        } else if (anger >= MAX_ANGER) {
            status = GameStatus.LOST_GOBLIN_ATE_DECK;
            events.add(new GameEvent(EventType.LOSE_GOBLIN, "Anger 5/5. The goblin ate the deck. GOBLIN WINS."));
        } else if (vault.isEmpty()) {
            status = GameStatus.WON;
            events.add(new GameEvent(EventType.WIN, "Vault cracked! You win with " + score + " points."));
        }

        return new TurnResult(previous, next, guess, List.copyOf(events), status);
    }

    /** Ties count as correct. Aces are high. */
    static boolean isCorrect(Guess guess, Card previous, Card next) {
        int diff = next.rank().value() - previous.rank().value();
        return guess == Guess.HIGHER ? diff >= 0 : diff <= 0;
    }

    public Card current() {
        return current;
    }

    public int lives() {
        return lives;
    }

    public int score() {
        return score;
    }

    public int anger() {
        return anger;
    }

    public boolean freePassReady() {
        return freePassReady;
    }

    /** The next vault card if a Queen let the player peek, otherwise null. */
    public Card peeked() {
        return peeked;
    }

    public int cardsLeft() {
        return vault.size();
    }

    public List<Card> revealedCards() {
        return Collections.unmodifiableList(revealed);
    }

    public GameStatus status() {
        return status;
    }
}
