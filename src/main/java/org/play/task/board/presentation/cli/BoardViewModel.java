package org.play.task.board.presentation.cli;

import org.play.task.board.model.Board;
import org.play.task.board.model.Card;
import org.play.task.board.model.Column;
import org.play.task.board.repository.BoardRepository;
import org.play.task.board.repository.CardRepository;
import org.play.task.board.repository.ColumnRepository;
import org.play.task.board.util.Either;
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

    private List<Column> boardColumnsCache = null;
    private List<Card> cardsAllCache = null;

    BoardViewModel(
        CardRepository cardRepository,
        ColumnRepository columnRepository,
        BoardRepository boardRepository
    ) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
        this.boardRepository = boardRepository;
    }

    public void onDebug() {
        final List<Card> cards = cardRepository.getAll();
        System.out.println(cards);
    }

    public Optional<Card> onCreateCard(Card newCard, Column initialColumn) {
        Optional<Card> maybeNewCard =
            cardRepository.save(newCard, initialColumn.columnId());
        if (maybeNewCard.isPresent()) {
            cardsAllCache = null;
        }
        return maybeNewCard;
    }

    public List<Board> getBoards() {
        return boardRepository.getAll();
    }

    public boolean excludeBoard(Board board) {
        return boardRepository.deleteBoard(board);
    }

    public Optional<Board> addBoard(Board board) {
        return boardRepository.save(board);
    }


    public Optional<Column> onCreateColumn(Column column) {
        Optional<Column> maybeNewColumn =
	    columnRepository.createPending(column);
        if (maybeNewColumn.isPresent()) {
            boardColumnsCache = null; // reset cached columns
        }
        return maybeNewColumn;
    }

    public boolean moveCard(Card card, Column column) {
        boolean wasCardMoved = cardRepository.moveCard(card, column);
        if (wasCardMoved) {
            cardsAllCache = null;
        }
        return wasCardMoved;
    }

    public List<Column> boardColumns(Board board) {
        if (boardColumnsCache == null) {
            boardColumnsCache = columnRepository.getAllFromBoard(board);
        }
        return boardColumnsCache;
    }

    public List<Card> cardsAll(Board board) {
        if (cardsAllCache == null) {
            cardsAllCache =
                cardRepository.getCardsFromColumns(boardColumns(board));
        }
        return cardsAllCache;
    }

    public void clearCaches() {
        boardColumnsCache = null;
        cardsAllCache = null;
    }

    public boolean blockCard(Board board, Card card) {
        boolean wasBlocked = cardRepository
            .setIsBlockCard(board, card, true);
        if (wasBlocked) {
            cardsAllCache = null; // reset cached cards
        }
        return wasBlocked;
    }

    public boolean unblockCard(Board board, Card card) {
        boolean wasUnblocked = cardRepository
            .setIsBlockCard(board, card, false);
        if (wasUnblocked) {
            cardsAllCache = null; // reset cached cards
        }
        return wasUnblocked;
    }

    public Either<Column, String> deleteColumn(Board board, Column column) {
        if (column.type() != Column.Type.PENDING) {
            return Either.bad("Can only delete pending type columns");
        }
        if (cardsAll(board).stream()
            .anyMatch(card -> card.columnId().equals(column.columnId()))) {
            return Either.bad("Cannot delete column "
                + column.name() + " with cards in it");
        }
        boolean wasDeleted = columnRepository.deleteColumn(board, column);
        if (wasDeleted) {
            boardColumnsCache = null; // reset cached columns
            return Either.good(column);
        } else {
            return Either.bad("Failed to delete column");
        }
    }
}
