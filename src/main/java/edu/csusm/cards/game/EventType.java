package edu.csusm.cards.game;

/**
 * Everything that can happen during a turn. The UI uses these to decide
 * what the goblin says and which animation to play.
 */
public enum EventType {
    CORRECT,
    WRONG,
    FREE_FLIP,
    SPADE_DOUBLE,
    HEART_LIFE,
    HEART_MAXED,
    DIAMOND_BONUS,
    CLUB_ANGER,
    KING_STEAL,
    JACK_PASS,
    QUEEN_PEEK,
    TANTRUM_SHUFFLE,
    WIN,
    LOSE_NO_LIVES,
    LOSE_GOBLIN
}
