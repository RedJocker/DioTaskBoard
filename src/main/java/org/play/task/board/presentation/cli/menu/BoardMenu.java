package org.play.task.board.presentation.cli.menu;

import org.play.task.board.model.Board;
import org.play.task.board.model.Card;
import org.play.task.board.model.Column;
import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.form.CardCreateIoForm;
import org.play.task.board.presentation.cli.form.ColumnCreateIoForm;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.play.task.board.util.Pair;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.play.task.board.presentation.cli.menu.BoardMenu.Choice.CREATE_CARD;
import static org.play.task.board.presentation.cli.menu.BoardMenu.Choice.CREATE_COLUMN;
import static org.play.task.board.presentation.cli.menu.BoardMenu.Choice.EXIT;
import static org.play.task.board.presentation.cli.menu.BoardMenu.Choice.SHOW_COLUMNS_FULL;
import static org.play.task.board.presentation.cli.menu.BoardMenu.Choice.SHOW_COLUMNS_SHORT;
import static org.play.task.board.presentation.cli.menu.BoardMenu.Choice.choices;


@Component
@Scope("singleton")
public class BoardMenu extends Menu<Board> {

    enum Choice {
        EXIT(0, "Go Back"),
        CREATE_CARD(1, "Create Card"),
        CREATE_COLUMN(2, "Create Column"),
        SHOW_COLUMNS_SHORT(3, "Show Columns"),
        SHOW_COLUMNS_FULL(4, "Show Columns Detailed"),
        ;

        private final int menuId;
        private final String menuStr;

        Choice(int menuId, String menuStr) {
            this.menuId = menuId;
            this.menuStr = menuStr;
        }

        static String choices() {
            StringBuilder builder = new StringBuilder();
            for (Choice choice : Choice.values()) {
                builder.append(String.format("\n\t- (%d) %s", choice.menuId, choice.menuStr));
            }
            return builder.toString();
        }
    }

    private static final String startMenu =
            "Board Menu:" + choices() + "\n";

    private final CardCreateIoForm cardCreateIoForm;

    private final ColumnCreateIoForm columnCreateIoForm;

    List<Column> boardColumns = null;
    List<Card> cardsAll = null;

    BoardMenu(IoAdapter ioAdapter, CardCreateIoForm cardCreateIoForm, ColumnCreateIoForm columnCreateIoForm) {
        super(ioAdapter);
        this.cardCreateIoForm = cardCreateIoForm;
        this.columnCreateIoForm = columnCreateIoForm;
    }

    @Override
    public Integer getMenuSize() {
        return Choice.values().length;
    }

    @Override
    public String getMenuString() {
        return startMenu;
    }

    @Override
    public void loop(BoardViewModel viewModel, Board board) {
        resetCache();

        int choice = -1;
        while (choice != 0) {

            choice = this.promptMenuChoice();
            if (choice == EXIT.menuId) {
                break;
            }

            //bellow choices depend on boardColumns cache
            ensureBoardColumns(viewModel, board);

            if (choice == CREATE_CARD.menuId) {
                createCard(viewModel, board);
            } else if (choice == CREATE_COLUMN.menuId) {
                createColumn(viewModel, board);
            } else if (choice == SHOW_COLUMNS_SHORT.menuId) {
                showColumnsShort(board);
            }

            // bellow choices depends on cardsAll cache
            ensureCardsAll(viewModel);

            if (choice == SHOW_COLUMNS_FULL.menuId) {
                showColumnsFull(board);
            }
        }
    }

    private void showColumnsFull(Board board) {
        // retrieve columns with cards
        io.printf("Columns (%s):\n", board.name());
        for (Column column : boardColumns) {
            io.printf("\t- (%d) %s\n", column.columnId(), column.name());
            List<Card> cardsInColumn = cardsAll.stream()
                    .filter(card -> card.columnId().equals(column.columnId()))
                    .toList();
            if (cardsInColumn.isEmpty()) {
                io.printf("\t\tNo cards in this column\n");
            } else {
                for (Card card : cardsInColumn) {
                    io.printf("\t\t- (%d) %s\n", card.cardId(), card.name());
                }
            }
        }
    }

    private void showColumnsShort(Board board) {
        io.printf("Columns (%s):\n", board.name());
        boardColumns.forEach(column -> {
            io.printf("\t- (%d) %s\n", column.columnId(), column.name());
        });
        io.printf("\n");
    }

    private void createCard(BoardViewModel viewModel, Board board) {
        Optional<Card> maybeNewCard = cardCreateIoForm.collect(null);
        if (maybeNewCard.isEmpty()) {
            io.printf("Card creation cancelled\n");
            return;
        }

        // always creates card on initial column
        Column initialColumn = boardColumns.getFirst();
        maybeNewCard = viewModel.onCreateCard(maybeNewCard.get(), initialColumn);
        if (maybeNewCard.isPresent()) {
            io.printf("Card created: %s\n", maybeNewCard.get().toString());
            resetCardsAllCache();
        } else {
            io.printf("Card creation failed\n");
        }
    }

    private void createColumn(BoardViewModel viewModel, Board board) {

        Optional<Column> maybeNewColumn = columnCreateIoForm.collect(new Pair<>(board, boardColumns));
        if (maybeNewColumn.isEmpty()) {
            io.printf("Column creation cancelled\n");
            return;
        }
        maybeNewColumn = viewModel.onCreateColumn(maybeNewColumn.get());
        if (maybeNewColumn.isPresent()) {
            io.printf("Column created: %s\n", maybeNewColumn.get().name());
            resetBoardColumnsCache();
        } else {
            io.printf("Column creation failed\n");
        }
    }

    private void resetCache() {
        this.boardColumns = null;
        this.cardsAll = null;
    }

    private void resetCardsAllCache() {
        this.cardsAll = null;
    }

    private void resetBoardColumnsCache() {
        this.boardColumns = null;
    }

    private void ensureBoardColumns(BoardViewModel viewModel, Board board) {
        if (boardColumns == null || boardColumns.isEmpty()) {
            boardColumns = viewModel.getColumns(board);
        }
    }

    private void ensureCardsAll(BoardViewModel viewModel) {
        if (cardsAll == null) {
            cardsAll = viewModel.getCardsForColumns(boardColumns);
        }
    }
}