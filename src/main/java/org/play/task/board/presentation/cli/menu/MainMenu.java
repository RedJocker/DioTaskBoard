package org.play.task.board.presentation.cli.menu;

import org.play.task.board.model.Card;
import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.form.CardCreateIoForm;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.springframework.stereotype.Component;

import static org.play.task.board.presentation.cli.menu.MainMenu.Choices.BOARD;
import static org.play.task.board.presentation.cli.menu.MainMenu.Choices.EXIT;
import static org.play.task.board.presentation.cli.menu.MainMenu.Choices.DEBUG;



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

    private final CardCreateIoForm cardCreateIoForm;

    MainMenu(IoAdapter ioAdapter, CardCreateIoForm cardCreateIoForm) {
        super(ioAdapter);
        this.cardCreateIoForm = cardCreateIoForm;
    }

    public void displayWelcome() {
        io.printf(this.welcome);
    }

    public void displayBye() {
        io.printf(this.bye);
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
                Card newCard = cardCreateIoForm.collect(null);
                io.printf("%s\n", newCard.toString());
            } else if (choice == DEBUG.menuId) {
                viewModel.onDebug();
            }

        }
        this.displayBye();
        System.exit(0);
    }
}