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

    public Optional<Card> save(Card newCard) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int affectedRows = jdbcClient.sql("""
            INSERT INTO CARD (NAME, DESCRIPTION, CREATED_AT, IS_BLOCKED)
            VALUES (?, ?, ?, ?)
            """)
                .param(newCard.name())
                .param(newCard.description())
                .param(newCard.createdAt())
                .param(newCard.isBlocked())
                .update(keyHolder, "id");

        if (affectedRows == 0) {
            return Optional.empty();
        }

        Integer id = keyHolder.getKeyAs(Integer.class);
        return Optional.of(new Card(
                id.longValue(),
                newCard.name(),
                newCard.description(),
                newCard.createdAt(),
                newCard.isBlocked()
        ));
    }
}
