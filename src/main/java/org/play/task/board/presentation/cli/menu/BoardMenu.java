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
import java.util.Objects;
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
    public static final String LIST_ITEM_MS1 = "\t- (%d) %s\n";
    public static final String BOARD_DETAILS_TITLE = "Columns (%s):\n";

    public static final String COLUMN_DETAILS_TITLE = "Column (%s):\n";

    enum Choice {
        EXIT(0, "Go Back"),
        CREATE_CARD(1, "Create Card"),
        CREATE_COLUMN(2, "Create Column"),
        SHOW_COLUMNS_SHORT(3, "Show Columns List"),
        SHOW_COLUMNS_FULL(4, "Show Columns Full")
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
                builder.append(String.format(LIST_ITEM_MS1, choice.menuId, choice.menuStr));
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
            if (choice == EXIT.menuId) {
                break;
            }

            if (choice == CREATE_CARD.menuId) {
                createCard(viewModel, board);
            } else if (choice == CREATE_COLUMN.menuId) {
                createColumn(viewModel, board);
            } else if (choice == SHOW_COLUMNS_SHORT.menuId) {
                showColumnsShort(viewModel, board);
            }

            if (choice == SHOW_COLUMNS_FULL.menuId) {
                showColumnsFull(viewModel, board);
            }
        }
    }

    private void showColumnDetails(BoardViewModel viewModel, Column column, Board board) {
        while (true) {
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

            String input = io.readLine().trim();
            if (input.isEmpty())
                return;
            else if (input.equals("n") || input.equals("next")) {
                final Column next = Column.next(viewModel.boardColumns(board), column);
                showColumnDetails(viewModel, next, board);
                return;
            } else if (input.equals("p") || input.equals("prev") || input.equals("previous")) {
                final Column previous = Column.previous(viewModel.boardColumns(board), column);
                showColumnDetails(viewModel, previous, board);
                return;
            } else if (input.equals("l") || input.equals("list")) {
                showColumnsShort(viewModel, board);
                return;
            } else if (input.equals("f") || input.equals("full")) {
                showColumnsFull(viewModel, board);
                return;
            } else if (input.matches("^move \\d+$")) {
                String[] parts = input.split(" ");

                Optional<Card> maybeCard = Card.findCardByNameOrId(viewModel.cardsAll(board), parts[1]);
                if (maybeCard.isPresent() && Objects.equals(maybeCard.get().columnId(), column.columnId())) {
                    Card card = maybeCard.get();
                    Column nextColumn = Column.next(viewModel.boardColumns(board), column);
                    moveCard(viewModel, card, column, nextColumn);
                    continue;
                }
            }



            Optional<Card> maybeCard =
                    Card.findCardByNameOrId(viewModel.cardsAll(board), input.trim());

            if (maybeCard.isPresent()) {
                Card card = maybeCard.get();
                showCardDetails(viewModel, card, board);
            } else {
                io.printf("Card not found: %s\n", input.trim());
                return;
            }
        }
    }

    private void moveCard(BoardViewModel viewModel, Card card, Column column, Column nextColumn) {
        if (column.type() == Column.Type.FINAL
                || column.type() == Column.Type.CANCELED) {
            return;
        }
        if (card.isBlocked()) {
            io.printf("Card %s is blocked and cannot be moved\n", card.name());
        } else if (viewModel.moveCard(card, nextColumn)) {
            io.printf("Card %s moved to column %s\n", card.name(), nextColumn.name());
        } else {
            io.printf("Failed to move card %s to column %s\n", card.name(), nextColumn.name());
        }
    }

    private void showColumnsFull(BoardViewModel viewModel, Board board) {
        // retrieve columns with cards

        while (true) {
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
            String input = io.readLine();
            if (input.trim().isEmpty())
                return ;
            else if(input.charAt(0) == '\t') {
                Optional<Card> maybeCard =
                        Card.findCardByNameOrId(viewModel.cardsAll(board), input.trim());
                if (maybeCard.isPresent()) {
                    Card card = maybeCard.get();
                    showCardDetails(viewModel, card, board);
                } else {
                    io.printf("Card not found: %s\n", input.trim());
                    return;
                }
            } else {
                if (input.matches("^move \\d+$")) {
                    String[] parts = input.split(" ");

                    Optional<Card> maybeCard = Card.findCardByNameOrId(viewModel.cardsAll(board), parts[1]);
                    if (maybeCard.isPresent()) {
                        Card card = maybeCard.get();
                        Optional<Column> maybeColumn = Column.findColumnById(viewModel.boardColumns(board), card.columnId());
                        if (maybeColumn.isPresent()) {
                            Column nextColumn = Column.next(viewModel.boardColumns(board), maybeColumn.get());
                            moveCard(viewModel, card, maybeColumn.get(), nextColumn);
                            continue;
                        }
                    }
                } else if (input.equals("l") || input.equals("list")) {
                    showColumnsShort(viewModel, board);
                    return;
                }
                Optional<Column> columnByNameOrId =
                        Column.findColumnByNameOrId(viewModel.boardColumns(board), input.trim());
                if (columnByNameOrId.isPresent()) {
                    showColumnDetails(viewModel, columnByNameOrId.get(), board);
                } else {
                    io.printf("Column not found: %s\n", input.trim());
                    return;
                }
            }
        }

    }

    private void showCardDetails(BoardViewModel viewModel, Card card, Board board) {

        io.printf("Card Details:\n");
        Optional<Column> maybeColumn = Column.findColumnById(viewModel.boardColumns(board), card.columnId());
        if(maybeColumn.isPresent()) {
            io.printf("\tColumn Name: %s\n", maybeColumn.get().name());
        }
        io.printf("\tID: %d\n", card.cardId());
        io.printf("\tName: %s\n", card.name());
        io.printf("\tDescription: %s\n", card.description());
        io.printf("\tCreated At: %s\n", card.createdAt());
        io.printf("\tBlocked: %s\n", card.isBlocked() ? "Yes" : "No");

        String input = io.readLine();
        if (input.isBlank())
            return;
        if (input.equals("b") || input.equals("block")) {
            if(cardBlock(viewModel, card, board)) {
                Optional<Card> maybeBlockedCard = Card
                        .findCardByNameOrId(viewModel.cardsAll(board), card.name());
                if (maybeBlockedCard.isPresent()) {
                    showCardDetails(viewModel, maybeBlockedCard.get(), board);
                    return;
                }
            }
        }
        showCardDetails(viewModel, card, board);
    }

    private boolean cardBlock(BoardViewModel viewModel, Card card, Board board) {
        if (card.isBlocked()) {
            io.printf("Card %s is already blocked\n", card.name());
            return false;
        }
        boolean wasBlocked = viewModel.blockCard(board, card);
        if (wasBlocked) {
            io.printf("Card %s blocked successfully\n", card.name());
        } else {
            io.printf("Failed to block card %s\n", card.name());
        }
        return wasBlocked;
    }

    private void showColumnsShort(BoardViewModel viewModel, Board board) {
        io.printf(BOARD_DETAILS_TITLE, board.name());
        viewModel.boardColumns(board).forEach(column -> {
            io.printf(LIST_ITEM_MS1, column.columnId(), column.name());
        });
        io.printf("\n");

        String input = io.readLine();
        if (input.trim().isEmpty())
            return ;

        if (input.isEmpty())
            return;
        else if (input.equals("f") || input.equals("full")) {
            showColumnsFull(viewModel, board);
            return;
        }

        Optional<Column> columnByNameOrId =
                Column.findColumnByNameOrId(viewModel.boardColumns(board), input.trim());
        if (columnByNameOrId.isPresent()) {
            showColumnDetails(viewModel, columnByNameOrId.get(), board);
        } else {
            io.printf("Column not found: %s\n", input.trim());
            return;
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
        maybeNewCard = viewModel.onCreateCard(maybeNewCard.get(), initialColumn);
        if (maybeNewCard.isPresent()) {
            io.printf("Card created: %s\n", maybeNewCard.get().toString());
        } else {
            io.printf("Card creation failed\n");
        }
    }

    private void createColumn(BoardViewModel viewModel, Board board) {

        Optional<Column> maybeNewColumn = columnCreateIoForm.collect(new Pair<>(board, viewModel.boardColumns(board)));
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
}