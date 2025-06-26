package org.play.task.board.presentation.cli.menu;

import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.io.IoAdapter;

import static org.play.task.board.presentation.cli.menu.BoardMenu.Choices.CREATE_CARD;
import static org.play.task.board.presentation.cli.menu.BoardMenu.Choices.EXIT;


public class BoardMenu extends Menu<Void> {

    enum Choices {
        EXIT(0),
        CREATE_CARD(1);

        private final int menuId;
        Choices(int menuId) {
            this.menuId = menuId;
        }
    }

    private static final String startMenu =
            "Board Menu:\n\t- Exit (0)\n\t- Create Card (1)\n";


    BoardMenu(IoAdapter ioAdapter) {
        super(ioAdapter);
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

            choice = this.promptMenuChoice();
            if(choice == EXIT.menuId) {
                break;
            } else if (choice == CREATE_CARD.menuId) {

            }
        }

        System.exit(0);
    }
}