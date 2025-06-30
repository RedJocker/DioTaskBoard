package org.play.task.board.presentation.cli.menu.boardMenu;

import org.play.task.board.model.Board;
import org.play.task.board.model.Card;
import org.play.task.board.model.Column;
import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.form.CardCreateIoForm;
import org.play.task.board.presentation.cli.form.ColumnCreateIoForm;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.play.task.board.presentation.cli.menu.Menu;
import org.play.task.board.util.Either;
import org.play.task.board.util.Pair;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenu.Choice.choices;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuCardCommands.consumeCardArgCommand;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuCardCommands.showCardDetails;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.displayColumnDetails;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.displayListColumnsFull;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.displayListColumnsShort;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isCardArgsCommand;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isIndent;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isListColumnsFull;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isListColumnsShort;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isNext;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isPrevious;


@Component
@Scope("singleton")
public class BoardMenu extends Menu<Board> {
    public static final String LIST_ITEM_MS1 = "\t- (%d) %s\n";
    public static final String BOARD_DETAILS_TITLE = "Board (%s):\n";

    public static final String COLUMN_DETAILS_TITLE = "Column (%s):\n";

    enum Choice {
        EXIT(0, "Go Back"),
        CREATE_CARD(1, "Create Card"),
        CREATE_COLUMN(2, "Create Column"),
        SHOW_BOARD(3, "Show Board"),
        SHOW_BOARD_FULL(4, "Show Board Full"),
        DELETE_COLUMN(5, "Delete Column"),
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
                builder.append(String.format(
			LIST_ITEM_MS1, choice.menuId, choice.menuStr));
            }
            return builder.toString();
        }
    }

    private static final String startMenu =
        "Board Menu:\n" + choices();

    private final CardCreateIoForm cardCreateIoForm;

    private final ColumnCreateIoForm columnCreateIoForm;



    BoardMenu(IoAdapter ioAdapter,
        CardCreateIoForm cardCreateIoForm,
        ColumnCreateIoForm columnCreateIoForm) {

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

        int choice = -1;
        while (choice != 0) {
            choice = this.promptMenuChoice();
            switch (Choice.values()[choice]) {
            case EXIT -> { return; }
            case CREATE_CARD -> createCard(viewModel, board);
            case CREATE_COLUMN -> createColumn(viewModel, board);
            case SHOW_BOARD -> showBoard(viewModel, board);
            case SHOW_BOARD_FULL -> showBoardFull(viewModel, board);
            case DELETE_COLUMN -> deleteColumn(viewModel, board);
            default -> {}
            }
        }
    }

    private void createCard(BoardViewModel viewModel, Board board) {
        Optional<Card> maybeNewCard = cardCreateIoForm.collect(null);
        if (maybeNewCard.isEmpty()) {
            io.printf("Card creation cancelled\n");
            return;
        }

        // always creates card on initial column
        Column initialColumn = viewModel.boardColumns(board).getFirst();
        maybeNewCard = viewModel
            .onCreateCard(maybeNewCard.get(), initialColumn);
        if (maybeNewCard.isPresent()) {
            io.printf("Card created: %s\n", maybeNewCard.get().toString());
        } else {
            io.printf("Card creation failed\n");
        }
    }

    private void createColumn(BoardViewModel viewModel, Board board) {

        Optional<Column> maybeNewColumn = columnCreateIoForm
            .collect(new Pair<>(board, viewModel.boardColumns(board)));
        if (maybeNewColumn.isEmpty()) {
            io.printf("Column creation cancelled\n");
            return;
        }
        maybeNewColumn = viewModel.onCreateColumn(maybeNewColumn.get());
        if (maybeNewColumn.isPresent()) {
            io.printf("Column created: %s\n", maybeNewColumn.get().name());

        } else {
            io.printf("Column creation failed\n");
        }
    }

    private void showBoard(BoardViewModel viewModel, Board board) {
        displayListColumnsShort(io, viewModel, board);

        String input = io.readLine().trim();
        if (input.isEmpty())
            return ;
        else if (isListColumnsFull(input)) {
            showBoardFull(viewModel, board);
            return;
        }

        Optional<Column> columnByNameOrId =
            Column.findColumnByNameOrId(
                viewModel.boardColumns(board), input.trim());
        if (columnByNameOrId.isPresent()) {
            showColumnDetails(viewModel, columnByNameOrId.get(), board);
        } else {
            io.printf("Column not found: %s\n", input.trim());
            return;
        }
    }

    private void showBoardFull(BoardViewModel viewModel, Board board) {
        while (true) {
            displayListColumnsFull(io, viewModel, board);

            String input = io.readLine();
            if (input.trim().isEmpty())
                return ;
            else if (isCardArgsCommand(input)) {
                String[] parts = input.split(" ");
                Optional<Card> maybeCard = Card
                    .findCardByNameOrId(viewModel.cardsAll(board), parts[1]);
                if (maybeCard.isPresent()) {
                    Card card = maybeCard.get();
                    Column cardColumn = Column.findColumnById(
                        viewModel.boardColumns(board), card.columnId());
                    if (consumeCardArgCommand(
                            io, input, viewModel, board, cardColumn)) {
                        continue;
                    }
                }
            } else if(isIndent(input)) {
                Optional<Card> maybeCard =
                    Card.findCardByNameOrId(
                        viewModel.cardsAll(board), input.trim());
                if (maybeCard.isPresent()) {
                    Card card = maybeCard.get();
                    showCardDetails(io, viewModel, card, board);
                    continue;
                } else {
                    io.printf("Card not found: %s\n", input.trim());
                    return;
                }
            } else if (isListColumnsShort(input)) {
                showBoard(viewModel, board);
                return;
            }

            Optional<Column> columnByNameOrId =
                Column.findColumnByNameOrId(
                    viewModel.boardColumns(board), input.trim());
            if (columnByNameOrId.isPresent()) {
                showColumnDetails(viewModel, columnByNameOrId.get(), board);
            } else {
                io.printf("Column not found: %s\n", input.trim());
                return;
            }
        }
    }

    private void deleteColumn(BoardViewModel viewModel, Board board) {
        io.printf("Delete Column:\n");
        List<Column> pendingColumns = viewModel.boardColumns(board)
            .stream()
            .filter(c -> c.type() == Column.Type.PENDING)
            .toList();
        if (pendingColumns.isEmpty()) {
            io.printf("No columns to delete\n");
            return;
        }

        for (Column column : pendingColumns) {
            io.printf(LIST_ITEM_MS1, column.columnId(), column.name());
        }

        String input = io.readLine().trim();
        if (input.isEmpty())
            return;

        Optional<Column> maybeColumn = Column
            .findColumnByNameOrId(pendingColumns, input);
        if (maybeColumn.isPresent()) {
            Column column = maybeColumn.get();
            Either<Column, String> eitherColumnOrError =
                viewModel.deleteColumn(board, column);
            if (eitherColumnOrError.isGood()) {
                io.printf("Column %s deleted successfully\n",
                    eitherColumnOrError.ok().name());
            } else {
                io.printf(eitherColumnOrError.error() + "\n");
            }
        } else {
            io.printf("Column not found: %s\n", input);
        }
    }

    private void showColumnDetails(
        BoardViewModel viewModel, Column column, Board board) {

        while (true) {
            displayColumnDetails(io, viewModel, column, board);

            String input = io.readLine().trim();
            if (input.isEmpty())
                return;
            else if (isNext(input)) {
                final Column next = Column
                    .next(viewModel.boardColumns(board), column);
                showColumnDetails(viewModel, next, board);
                return;
            } else if (isPrevious(input)) {
                final Column previous = Column
                    .previous(viewModel.boardColumns(board), column);
                showColumnDetails(viewModel, previous, board);
                return;
            } else if (isListColumnsShort(input)) {
                showBoard(viewModel, board);
                return;
            } else if (isListColumnsFull(input)) {
                showBoardFull(viewModel, board);
                return;
            } else if(consumeCardArgCommand(
                    io, input, viewModel, board, column)) {
                continue;
            }

            Optional<Card> maybeCard =
                Card.findCardByNameOrId(
                    viewModel.cardsAll(board), input.trim());
            if (maybeCard.isPresent()) {
                Card card = maybeCard.get();
                showCardDetails(io, viewModel, card, board);
            } else {
                io.printf("Card not found: %s\n", input.trim());
                return;
            }
        }
    }
}
