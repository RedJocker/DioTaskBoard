package org.play.task.board.presentation.cli.menu;

import org.play.task.board.model.Board;
import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.form.SelectBoardIoForm;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.play.task.board.presentation.cli.menu.MainMenu.Choice.EXCLUDE_BOARD;
import static org.play.task.board.presentation.cli.menu.MainMenu.Choice.SELECT_BOARD;
import static org.play.task.board.presentation.cli.menu.MainMenu.Choice.DEBUG;
import static org.play.task.board.presentation.cli.menu.MainMenu.Choice.EXIT;
import static org.play.task.board.presentation.cli.menu.MainMenu.Choice.choices;


@Component
public class MainMenu extends Menu<Void> {

    enum Choice {
        EXIT(0, "Exit"),
        SELECT_BOARD(1, "Select Board"),
        EXCLUDE_BOARD(2, "Exclude Board"),
        DEBUG(3, "Debug");

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
    private static final String welcome = "\n\n\tT  A  S  K -> B  O  A  R  D\n\n";
    private static final String startMenu =
            "Main Menu:" + choices() + "\n";
    private static final String bye = "\n\n\tBye\n\n";

    private final BoardMenu boardMenu;

    private final SelectBoardIoForm selectBoardIoform;

    MainMenu(IoAdapter ioAdapter, BoardMenu boardMenu, SelectBoardIoForm selectBoardIoform) {
        super(ioAdapter);
        this.boardMenu = boardMenu;
        this.selectBoardIoform = selectBoardIoform;
    }

    public void displayWelcome() {
        io.printf(welcome);
    }

    public void displayBye() {
        io.printf(bye);
    }

    @Override
    public Integer getMenuSize() {
        return DEBUG.menuId + 1;
    }

    @Override
    public String getMenuString() {
        return startMenu;
    }

    @Override
    public void loop(BoardViewModel viewModel, Void args) {

        int choice = -1;
        while (choice != 0) {
            this.displayWelcome();
            choice = this.promptMenuChoice();
            if(choice == EXIT.menuId) {
                break;
            } else if (choice == EXCLUDE_BOARD.menuId) {
                final List<Board> boards = viewModel.getBoards();
                final Optional<Board> maybeBoard = selectBoardIoform.collect(boards);

                if (maybeBoard.isPresent()) {
                    if (viewModel.excludeBoard(maybeBoard.get())) {
                        io.printf("Board excluded successfully");
                    } else {
                        io.printf("Failed to exclude board " + maybeBoard.get().name());
                    }
                } else {
                    io.printf("Board not found");
                }

            } else if (choice == SELECT_BOARD.menuId) {
                final List<Board> boards =  viewModel.getBoards();
                final Optional<Board> maybeBoard = selectBoardIoform.collect(boards);

                if (maybeBoard.isPresent()) {
                    boardMenu.loop(viewModel, maybeBoard.get());
                } else {
                    io.printf("Board not found");
                }

            } else if (choice == DEBUG.menuId) {
                viewModel.onDebug();
            }

        }
        this.displayBye();
        System.exit(0);
    }
}