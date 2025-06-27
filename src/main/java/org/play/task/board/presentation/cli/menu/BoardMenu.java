package org.play.task.board.presentation.cli.menu;

import org.play.task.board.model.Board;
import org.play.task.board.model.Card;
import org.play.task.board.model.Column;
import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.form.CardCreateIoForm;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.play.task.board.presentation.cli.menu.BoardMenu.Choice.CREATE_CARD;
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
        SHOW_COLUMNS_SHORT(2, "Show Columns"),
        SHOW_COLUMNS_FULL(3, "Show Columns Detailed"),
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


    BoardMenu(IoAdapter ioAdapter, CardCreateIoForm cardCreateIoForm) {
        super(ioAdapter);
        this.cardCreateIoForm = cardCreateIoForm;
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
        List<Column> boardColumns = viewModel.getColumns(board);
        io.printf("%s\n", boardColumns);

        List<Card> cardsAll = null;

        int choice = -1;
        while (choice != 0) {

            choice = this.promptMenuChoice();
            if (choice == EXIT.menuId) {
                break;
            } else if (choice == CREATE_CARD.menuId) {
                Optional<Card> maybeNewCard = cardCreateIoForm.collect(null);
                if (maybeNewCard.isEmpty()) {
                    io.printf("Card creation cancelled\n");
                    continue;
                }

                Column initialColumn = boardColumns.getFirst();
                maybeNewCard = viewModel.onCreateCard(maybeNewCard.get(), initialColumn);
                if (maybeNewCard.isPresent()) {
                    io.printf("Card created: %s\n", maybeNewCard.get().toString());
                    cardsAll = null; // reset cardsAll to force reloading
                } else {
                    io.printf("Card creation failed\n");
                }
            } else if (choice == SHOW_COLUMNS_SHORT.menuId) {
                io.printf("Columns (%s):\n", board.name());
                boardColumns.forEach(column -> {
                    io.printf("\t- (%d) %s\n", column.columnId(), column.name());
                });
                io.printf("\n");
            } else if (choice == SHOW_COLUMNS_FULL.menuId) {

                // retrieve columns with cards
                if (cardsAll == null) {
                    cardsAll = viewModel.getCardsForColumns(boardColumns);
                }
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
        }
    }
}