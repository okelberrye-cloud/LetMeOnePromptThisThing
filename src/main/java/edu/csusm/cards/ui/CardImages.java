package edu.csusm.cards.ui;

import edu.csusm.cards.model.Card;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads card images from src/main/resources/cards and caches them,
 * so each image file is only read from disk once.
 */
public final class CardImages {

    private static final Map<String, Image> CACHE = new HashMap<>();
    private static final String BACK = "card_back.png";

    private CardImages() {
    }

    public static Image front(Card card) {
        return load(card.imageFile());
    }

    public static Image back() {
        return load(BACK);
    }

    private static Image load(String file) {
        return CACHE.computeIfAbsent(file, f -> {
            InputStream in = CardImages.class.getResourceAsStream("/cards/" + f);
            if (in == null) {
                throw new IllegalStateException("Missing card image: /cards/" + f);
            }
            return new Image(in);
        });
    }
}
