package org.play.task.board.presentation.cli;


import org.play.task.board.model.Board;
import org.play.task.board.model.Card;
import org.play.task.board.model.Column;
import org.play.task.board.repository.CardRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Scope("singleton")
public class BoardViewModel {

    private final CardRepository cardRepository;
    BoardViewModel(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void onDebug() {
        final List<Card> cards = cardRepository.getAll();
        System.out.println(cards);
    }

    public Optional<Card> onCreateCard(Card newCard, Column initialColumn) {
        System.out.println("ViewModel.onCreateCard: " + newCard.toString());

        return cardRepository.save(newCard, initialColumn.columnId());
    }

    public List<Column> getColumns(Board board) {
        // TODO get columns from database
        Column initial = Column.initial();
        initial.setColumnId(2);
        return List.of(initial);
    }
}
