package org.play.task.board.model;

import java.time.OffsetDateTime;

public record Card(Long cardId, String name, String description, OffsetDateTime createdAt, boolean isBlocked, Long columnId) {

    public static boolean validName(String name) {
        return name != null && !name.isBlank();
    }

    public static boolean validDescription(String description) {
        return description != null && !description.isBlank();
    }
}
