package org.play.task.board.model;

import java.time.OffsetDateTime;

public record Card(Long id, String name, String description, OffsetDateTime createdAt, boolean isBlocked) {}
