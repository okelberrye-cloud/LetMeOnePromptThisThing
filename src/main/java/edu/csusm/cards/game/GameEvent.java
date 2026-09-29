package edu.csusm.cards.game;

/** One thing that happened during a turn, with a message for the log. */
public record GameEvent(EventType type, String message) {
}
