package org.play.task.board.repository;

import org.play.task.board.model.Card;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Scope("singleton")
public class CardRepository {
    private final JdbcClient jdbcClient;

    public CardRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Card> getAll() {
        return jdbcClient.sql("SELECT * FROM CARD")
                .query(Card.class)
                .list();
    }

    public Optional<Card> save(Card newCard, Long columnId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int affectedRows = jdbcClient.sql("""
            INSERT INTO CARD (NAME, DESCRIPTION, CREATED_AT, IS_BLOCKED, COLUMN_ID)
            VALUES (?, ?, ?, ?, ?)
            """)
                .param(newCard.name())
                .param(newCard.description())
                .param(newCard.createdAt())
                .param(newCard.isBlocked())
                .param(columnId)
                .update(keyHolder, "card_id");

        if (affectedRows == 0) {
            return Optional.empty();
        }

        Long id = keyHolder.getKeyAs(Long.class);
        return Optional.of(new Card(
                id,
                newCard.name(),
                newCard.description(),
                newCard.createdAt(),
                newCard.isBlocked(),
                columnId
        ));
    }
}
