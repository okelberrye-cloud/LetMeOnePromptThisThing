package edu.csusm.cards.ui;

import edu.csusm.cards.game.EventType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Things the goblin says. */
public final class Taunts {

    private static final Random RANDOM = new Random();
    private static final Map<EventType, List<String>> LINES = new EnumMap<>(EventType.class);

    static {
        LINES.put(EventType.CORRECT, List.of(
                "Lucky guess, human.",
                "Hmph. Beginner's luck.",
                "The goblin allows it. For now.",
                "Ugh. Fine. Take your points."));
        LINES.put(EventType.WRONG, List.of(
                "HA! WRONG! Your hearts are MINE!",
                "Nyeh heh heh! Should've gone the other way!",
                "Oof. The goblin saw that coming.",
                "Wrong! Keep feeding me lives, it's delicious."));
        LINES.put(EventType.FREE_FLIP, List.of(
                "Free flip?! This is goblin tax fraud!",
                "Enjoy your little free pass. Won't last."));
        LINES.put(EventType.SPADE_DOUBLE, List.of(
                "A spade? Double points?! Who wrote these rules?!",
                "Spades are for digging... into MY treasure. Rude."));
        LINES.put(EventType.HEART_LIFE, List.of(
                "Blech, a heart. Have a life, I guess.",
                "Hearts are disgusting. Take it away."));
        LINES.put(EventType.HEART_MAXED, List.of(
                "Max lives already? Greedy little human."));
        LINES.put(EventType.DIAMOND_BONUS, List.of(
                "Sparkly! Wait. That was MY diamond!",
                "Put that diamond BACK."));
        LINES.put(EventType.CLUB_ANGER, List.of(
                "Clubs make me CRANKY.",
                "Grrr... do that again. I dare you.",
                "Another club?! My blood is boiling!"));
        LINES.put(EventType.KING_STEAL, List.of(
                "The King pays tribute... to ME. Yoink!",
                "Royal tax! Hand over 30 points."));
        LINES.put(EventType.JACK_PASS, List.of(
                "A Jack?! Ugh, free pass. Go on then.",
                "Jacks. Always sneaking people past me."));
        LINES.put(EventType.QUEEN_PEEK, List.of(
                "Hey! No peeking! ...fine.",
                "The Queen snitched. Traitor."));
        LINES.put(EventType.TANTRUM_SHUFFLE, List.of(
                "THAT'S IT! SHUFFLE TIME!!",
                "AAARGH! Nobody touches my vault in ORDER!",
                "TANTRUM! Good luck now, card nerd!"));
        LINES.put(EventType.WIN, List.of(
                "My vault... my beautiful vault... (sob)",
                "You cracked it?! I'm telling my mom."));
        LINES.put(EventType.LOSE_NO_LIVES, List.of(
                "Out of hearts! Come back when you're luckier.",
                "GOBLIN WINS! As was foretold."));
        LINES.put(EventType.LOSE_GOBLIN, List.of(
                "CHOMP CHOMP. The deck is mine now.",
                "I ATE THE DECK. No regrets."));
    }

    /** Most dramatic first. The goblin reacts to the top event in this list. */
    private static final List<EventType> PRIORITY = List.of(
            EventType.LOSE_GOBLIN, EventType.LOSE_NO_LIVES, EventType.WIN,
            EventType.TANTRUM_SHUFFLE, EventType.KING_STEAL, EventType.WRONG,
            EventType.CLUB_ANGER, EventType.QUEEN_PEEK, EventType.JACK_PASS,
            EventType.SPADE_DOUBLE, EventType.DIAMOND_BONUS, EventType.HEART_LIFE,
            EventType.HEART_MAXED, EventType.FREE_FLIP, EventType.CORRECT);

    public static final List<String> INTRO = List.of(
            "Welcome to my vault, human. Guess wrong and I eat your hearts.",
            "13 cards. 3 lives. 1 very grumpy goblin. Good luck.",
            "Higher or lower? Choose poorly. Please.");

    private Taunts() {
    }

    public static EventType mostDramatic(List<EventType> types) {
        for (EventType t : PRIORITY) {
            if (types.contains(t)) {
                return t;
            }
        }
        return EventType.CORRECT;
    }

    public static String line(EventType type) {
        return pick(LINES.getOrDefault(type, List.of("...")));
    }

    public static String intro() {
        return pick(INTRO);
    }

    private static String pick(List<String> options) {
        return options.get(RANDOM.nextInt(options.size()));
    }
}
