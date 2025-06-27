package org.play.task.board.presentation.cli;


import org.play.task.board.model.Board;
import org.play.task.board.model.Card;
import org.play.task.board.model.Column;
import org.play.task.board.repository.BoardRepository;
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
    private final BoardRepository boardRepository;

    BoardViewModel(CardRepository cardRepository, ColumnRepository columnRepository, BoardRepository boardRepository) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.boardRepository = boardRepository;
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

    public List<Board> getBoards() {
        // TODO get from repository
        return boardRepository.getAll();
    }

    public boolean excludeBoard(Board board) {
        return boardRepository.deleteBoard(board);
    }

    public Optional<Board> addBoard(Board board) {
        return boardRepository.save(board);
    }
}
