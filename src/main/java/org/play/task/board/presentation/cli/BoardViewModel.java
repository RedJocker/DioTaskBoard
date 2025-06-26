package org.play.task.board.presentation.cli;


import org.play.task.board.model.Card;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Scope("singleton")
public class BoardViewModel {
    private final JdbcClient jdbcClient;

    BoardViewModel(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void onDebug() {
        final List<Card> cards = jdbcClient.sql("SELECT * FROM CARD")
                .query(Card.class)
                .list();
        System.out.println(cards);
    }

    public Optional<Card> onCreateCard(Card newCard) {
        System.out.println("ViewModel.onCreateCard: " + newCard.toString());
        return Optional.of(newCard);
    }
}
