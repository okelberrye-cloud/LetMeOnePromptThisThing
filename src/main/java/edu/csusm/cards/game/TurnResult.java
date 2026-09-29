package edu.csusm.cards.game;

import edu.csusm.cards.model.Card;

import java.util.List;

/**
 * Everything the UI needs to animate one turn.
 *
 * @param previous the card the player was guessing against
 * @param revealed the vault card that was flipped this turn
 * @param guess    what the player chose
 * @param events   what happened, in the order it happened
 * @param status   the game status after this turn
 */
public record TurnResult(Card previous, Card revealed, Guess guess,
                         List<GameEvent> events, GameStatus status) {

    public boolean has(EventType type) {
        return events.stream().anyMatch(e -> e.type() == type);
    }
}
