package org.play.task.board.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public record Card(Long cardId, String name, String description, OffsetDateTime createdAt, boolean isBlocked, Long columnId) {

    public static boolean validName(String name) {
        return name != null && !name.isBlank();
    }

    public static boolean validDescription(String description) {
        return description != null && !description.isBlank();
    }

    public static Optional<Card> findCardByNameOrId(List<Card> cards, String nameOrId) {
        return cards.stream()
                .filter(card -> card.name().equalsIgnoreCase(nameOrId)
                        || card.cardId.toString().equals(nameOrId))
                .findFirst();

    }

    public static Card findCardById(List<Card> cards, long cardId) {
        return cards.stream()
                .filter(card -> card.cardId() == cardId)
                .findFirst()
                .orElseThrow();
    }
}
