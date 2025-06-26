package org.play.task.board.presentation.cli.menu;

import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.springframework.stereotype.Component;

import static org.play.task.board.presentation.cli.menu.MainMenu.Choices.BOARD;
import static org.play.task.board.presentation.cli.menu.MainMenu.Choices.DEBUG;
import static org.play.task.board.presentation.cli.menu.MainMenu.Choices.EXIT;



@Component
public class MainMenu extends Menu<Void> {

    enum Choices {
        EXIT(0),
        BOARD(1),
        DEBUG(2);

        private final int menuId;

        Choices(int menuId) {
            this.menuId = menuId;
        }
    }
    private static final String welcome = "\n\tT  A  S  K -> B  O  A  R  D\n\n";
    private static final String startMenu =
            "Main Menu:\n\t- (0) Exit\n\t- (1) Board\n\t- (2) Debug\n";
    private static final String bye = "\n\n\tBye\n\n";

    private final BoardMenu boardMenu;

    MainMenu(IoAdapter ioAdapter, BoardMenu boardMenu) {
        super(ioAdapter);
        this.boardMenu = boardMenu;
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
            } else if (choice == BOARD.menuId) {
                boardMenu.loop(viewModel, null);
            } else if (choice == DEBUG.menuId) {
                viewModel.onDebug();
            }

        }
        this.displayBye();
        System.exit(0);
    }
}