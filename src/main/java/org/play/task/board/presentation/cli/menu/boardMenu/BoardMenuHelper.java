package org.play.task.board.presentation.cli.menu.boardMenu;

import org.play.task.board.model.Board;
import org.play.task.board.model.Card;
import org.play.task.board.model.Column;
import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.io.IoAdapter;

import java.util.List;

import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenu.BOARD_DETAILS_TITLE;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenu.COLUMN_DETAILS_TITLE;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenu.LIST_ITEM_MS1;

class BoardMenuHelper {

    static boolean isMove(String input) {
        return input != null &&  input.matches("\\s*m(ove)?\\s*$");
    }

    static boolean isBlock(String input) {
        return input != null && input.matches("^\\s*b(lock)?\\s*$");
    }

    static boolean isUnblock(String input) {
        return input != null &&  input.matches("\\s*u(nblock)?\\s*$");
    }

    static boolean isCancel(String input) {
        return input != null &&  input.matches("\\s*c(ancel)?\\s*$");
    }

    static boolean isMoveArg(String input) {
        return input != null && input.matches("^\\s*m(ove)? \\S.*$");
    }

    static boolean isBlockArg(String input) {
        return input != null && input.matches("^\\s*b(lock)? \\S.*$");
    }

    static boolean isUnblockArg(String input) {
        return input != null &&  input.matches("\\s*u(nblock)? \\S.*");
    }

    static boolean isCancelArg(String input) {
        return input != null &&  input.matches("\\s*c(ancel)? \\S.*");
    }

    static boolean isCardArgsCommand(String input) {
        return isMoveArg(input) || isBlockArg(input) || isUnblockArg(input) || isCancelArg(input);
    }

    static boolean isListColumnsShort(String input) {
        return input != null &&  input.matches("^\\s*l(ist)?\\s*$");
    }

    static boolean isListColumnsFull(String input) {
        return input != null &&  input.matches("^\\s*f(ull)?\\s*$");
    }

    static boolean isIndent(String input) {
        return input != null && input.charAt(0) ==  '\t';
    }

    static boolean isNext(String input) {
        return input.matches("^\\s*n(ext)?\\s*$");
    }

    static boolean isPrevious(String input) {
        return input.matches("^\\s*p(rev(ious)?)?\\s*$");
    }

    static void displayListColumnsShort(IoAdapter io, BoardViewModel viewModel, Board board) {
        io.printf(BOARD_DETAILS_TITLE, board.name());
        viewModel.boardColumns(board).forEach(column -> {
            io.printf(LIST_ITEM_MS1, column.columnId(), column.name());
        });
        io.printf("\n");
    }

    static void displayCardDetail(IoAdapter io, BoardViewModel viewModel, Card card, Board board) {
        io.printf("Card Details:\n");
        Column cardColumn = Column.findColumnById(viewModel.boardColumns(board), card.columnId());
        io.printf("\tColumn Name: %s\n", cardColumn.name());
        io.printf("\tID: %d\n", card.cardId());
        io.printf("\tName: %s\n", card.name());
        io.printf("\tDescription: %s\n", card.description());
        io.printf("\tCreated At: %s\n", card.createdAt());
        io.printf("\tBlocked: %s\n", card.isBlocked() ? "Yes" : "No");
    }

    static void displayListColumnsFull(IoAdapter io, BoardViewModel viewModel, Board board) {
        io.printf(BOARD_DETAILS_TITLE, board.name());
        for (Column column : viewModel.boardColumns(board)) {
            io.printf(LIST_ITEM_MS1, column.columnId(), column.name());
            List<Card> cardsInColumn = viewModel.cardsAll(board).stream()
                    .filter(card -> card.columnId().equals(column.columnId()))
                    .toList();
            if (cardsInColumn.isEmpty()) {
                io.printf("\t\tNo cards in this column\n");
            } else {
                for (Card card : cardsInColumn) {
                    io.printf("\t"+LIST_ITEM_MS1, card.cardId(), card.name());
                }
            }
        }
    }

    static void displayColumnDetails(IoAdapter io, BoardViewModel viewModel, Column column, Board board) {
        io.printf(COLUMN_DETAILS_TITLE, column.name());
        List<Card> cardsInColumn = viewModel.cardsAll(board).stream()
                .filter(card -> card.columnId().equals(column.columnId()))
                .toList();
        if (cardsInColumn.isEmpty()) {
            io.printf("\tNo cards in this column\n");
        } else {
            for (Card card : cardsInColumn) {
                io.printf(LIST_ITEM_MS1, card.cardId(), card.name());
            }
        }
    }
}
