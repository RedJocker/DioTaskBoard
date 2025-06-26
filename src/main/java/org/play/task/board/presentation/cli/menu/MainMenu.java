package org.play.task.board.presentation.cli.menu;

import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.springframework.stereotype.Component;

import static org.play.task.board.presentation.cli.menu.MainMenu.Choices.DEBUG;
import static org.play.task.board.presentation.cli.menu.MainMenu.Choices.EXIT;

@Component
public class MainMenu extends Menu<Void> {

    enum Choices {
        EXIT(0),
        DEBUG(1);

        private final int menuId;
        Choices(int menuId) {
            this.menuId = menuId;
        }
    }
    private static final String welcome = "\n\tT  A  S  K -> B  O  A  R  D\n\n";
    private static final String startMenu =
            "Main Menu:\n\t- Exit (0)\n\t- Debug (1)\n";
    private static final String bye = "\n\n\tBye\n\n";

    MainMenu(IoAdapter ioAdapter) {
        super(ioAdapter);
    }

    public void displayWelcome() {
        io.printf(this.welcome);
    }

    public void displayBye() {
        io.printf(this.bye);
    }

    @Override
    public Integer getMenuSize() {
        return 2;
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
            } else if (choice == DEBUG.menuId) {
                viewModel.onDebug();
            }
        }
        this.displayBye();
        System.exit(0);
    }
}