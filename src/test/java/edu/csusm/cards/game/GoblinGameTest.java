package edu.csusm.cards.game;

import edu.csusm.cards.model.Card;
import edu.csusm.cards.model.Rank;
import edu.csusm.cards.model.Suit;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class GoblinGameTest {

    private static Card c(Rank r, Suit s) {
        return new Card(r, s);
    }

    private static GoblinGame game(Card start, Card... vault) {
        return new GoblinGame(start, List.of(vault), new Random(1));
    }

    @Test
    void setupDealsThirteenAndFlipsOneWithNoDuplicates() {
        GoblinGame g = new GoblinGame(new Random(42));
        assertEquals(13, g.cardsLeft());
        assertEquals(3, g.lives());
        assertEquals(0, g.score());
        assertEquals(0, g.anger());
        assertEquals(GameStatus.PLAYING, g.status());

        // play through with free/any guesses and make sure all 14 cards are unique
        HashSet<Card> seen = new HashSet<>();
        seen.add(g.current());
        while (!g.status().isOver()) {
            TurnResult r = g.play(g.freePassReady() ? Guess.FREE : Guess.HIGHER);
            assertTrue(seen.add(r.revealed()), "duplicate card " + r.revealed());
        }
    }

    @Test
    void correctGuessScoresTenAndTiesCount() {
        GoblinGame g = game(c(Rank.FIVE, Suit.HEARTS), c(Rank.FIVE, Suit.DIAMONDS), c(Rank.TWO, Suit.HEARTS));
        TurnResult r = g.play(Guess.HIGHER); // tie
        assertTrue(r.has(EventType.CORRECT));
        assertEquals(10 + 25, g.score()); // +10 guess, +25 diamond
    }

    @Test
    void acesAreHigh() {
        GoblinGame g = game(c(Rank.KING, Suit.HEARTS), c(Rank.ACE, Suit.HEARTS), c(Rank.TWO, Suit.HEARTS));
        assertTrue(g.play(Guess.HIGHER).has(EventType.CORRECT));
    }

    @Test
    void spadeDoublesPoints() {
        GoblinGame g = game(c(Rank.TWO, Suit.HEARTS), c(Rank.NINE, Suit.SPADES), c(Rank.TWO, Suit.HEARTS));
        TurnResult r = g.play(Guess.HIGHER);
        assertTrue(r.has(EventType.SPADE_DOUBLE));
        assertEquals(20, g.score());
    }

    @Test
    void wrongGuessCostsALifeButHeartGivesItBack() {
        GoblinGame g = game(c(Rank.TWO, Suit.CLUBS), c(Rank.NINE, Suit.HEARTS), c(Rank.TWO, Suit.SPADES));
        TurnResult r = g.play(Guess.LOWER);
        assertTrue(r.has(EventType.WRONG));
        assertTrue(r.has(EventType.HEART_LIFE));
        assertEquals(3, g.lives());
    }

    @Test
    void heartsCapAtFiveLives() {
        GoblinGame g = game(c(Rank.TWO, Suit.CLUBS),
                c(Rank.THREE, Suit.HEARTS), c(Rank.FOUR, Suit.HEARTS), c(Rank.FIVE, Suit.HEARTS), c(Rank.SIX, Suit.SPADES));
        g.play(Guess.HIGHER);
        g.play(Guess.HIGHER);
        TurnResult r = g.play(Guess.HIGHER);
        assertEquals(5, g.lives());
        assertTrue(r.has(EventType.HEART_MAXED));
    }

    @Test
    void kingStealsThirtyButNotBelowZero() {
        GoblinGame g = game(c(Rank.TWO, Suit.CLUBS), c(Rank.KING, Suit.SPADES), c(Rank.TWO, Suit.HEARTS));
        g.play(Guess.HIGHER); // +20 (spade), then king steals up to 30
        assertEquals(0, g.score());
    }

    @Test
    void jackGivesFreeFlipThatCannotLoseALife() {
        GoblinGame g = game(c(Rank.TWO, Suit.HEARTS), c(Rank.JACK, Suit.SPADES), c(Rank.TWO, Suit.SPADES), c(Rank.THREE, Suit.SPADES));
        g.play(Guess.HIGHER);
        assertTrue(g.freePassReady());
        TurnResult r = g.play(Guess.HIGHER); // would be wrong, but it's free
        assertEquals(Guess.FREE, r.guess());
        assertTrue(r.has(EventType.FREE_FLIP));
        assertEquals(3, g.lives());
        assertFalse(g.freePassReady());
    }

    @Test
    void freeGuessWithoutJackIsRejected() {
        GoblinGame g = game(c(Rank.TWO, Suit.HEARTS), c(Rank.THREE, Suit.SPADES));
        assertThrows(IllegalArgumentException.class, () -> g.play(Guess.FREE));
    }

    @Test
    void queenPeeksTheNextCard() {
        Card next = c(Rank.SEVEN, Suit.DIAMONDS);
        GoblinGame g = game(c(Rank.TWO, Suit.HEARTS), c(Rank.QUEEN, Suit.HEARTS), next);
        TurnResult r = g.play(Guess.HIGHER);
        assertTrue(r.has(EventType.QUEEN_PEEK));
        assertEquals(next, g.peeked());
    }

    @Test
    void thirdClubCausesTantrumAndFifthEatsTheDeck() {
        GoblinGame g = game(c(Rank.TWO, Suit.HEARTS),
                c(Rank.THREE, Suit.CLUBS), c(Rank.FOUR, Suit.CLUBS), c(Rank.FIVE, Suit.CLUBS),
                c(Rank.SIX, Suit.CLUBS), c(Rank.SEVEN, Suit.CLUBS), c(Rank.EIGHT, Suit.CLUBS),
                c(Rank.NINE, Suit.CLUBS), c(Rank.TEN, Suit.CLUBS));
        assertFalse(g.play(Guess.HIGHER).has(EventType.TANTRUM_SHUFFLE));
        assertFalse(g.play(Guess.HIGHER).has(EventType.TANTRUM_SHUFFLE));
        assertTrue(g.play(Guess.HIGHER).has(EventType.TANTRUM_SHUFFLE));
        assertEquals(3, g.anger());
        g.play(Guess.HIGHER);
        TurnResult last = g.play(Guess.HIGHER);
        assertEquals(5, g.anger());
        assertEquals(GameStatus.LOST_GOBLIN_ATE_DECK, last.status());
        assertTrue(last.has(EventType.LOSE_GOBLIN));
    }

    @Test
    void runningOutOfLivesLoses() {
        GoblinGame g = game(c(Rank.ACE, Suit.SPADES),
                c(Rank.TWO, Suit.SPADES), c(Rank.TWO, Suit.DIAMONDS), c(Rank.THREE, Suit.SPADES), c(Rank.FOUR, Suit.SPADES));
        g.play(Guess.HIGHER); // 2 vs A: wrong
        g.play(Guess.LOWER);  // 2 vs 2: tie, correct
        g.play(Guess.LOWER);  // 3 vs 2: wrong
        TurnResult r = g.play(Guess.LOWER); // 4 vs 3: wrong
        assertEquals(0, g.lives());
        assertEquals(GameStatus.LOST_NO_LIVES, r.status());
    }

    @Test
    void emptyingTheVaultWins() {
        List<Card> vault = new ArrayList<>();
        for (Rank r : new Rank[]{Rank.TWO, Rank.THREE, Rank.FOUR}) {
            vault.add(c(r, Suit.DIAMONDS));
        }
        GoblinGame g = new GoblinGame(c(Rank.TWO, Suit.SPADES), vault, new Random(1));
        g.play(Guess.HIGHER);
        g.play(Guess.HIGHER);
        TurnResult r = g.play(Guess.HIGHER);
        assertEquals(GameStatus.WON, r.status());
        assertTrue(r.has(EventType.WIN));
        assertThrows(IllegalStateException.class, () -> g.play(Guess.HIGHER));
    }
}
