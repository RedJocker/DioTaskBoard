package org.play.task.board.presentation.cli;


import org.play.task.board.model.Board;
import org.play.task.board.model.Card;
import org.play.task.board.model.Column;
import org.play.task.board.repository.CardRepository;
import org.play.task.board.repository.ColumnRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Scope("singleton")
public class BoardViewModel {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    BoardViewModel(CardRepository cardRepository, ColumnRepository columnRepository) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
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

        return columnRepository.getAllFromBoard(board);
    }
}
