package org.play.task.board.presentation.cli.menu;

import org.play.task.board.model.Card;
import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.form.CardCreateIoForm;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.play.task.board.presentation.cli.menu.BoardMenu.Choices.CREATE_CARD;
import static org.play.task.board.presentation.cli.menu.BoardMenu.Choices.EXIT;


@Component
@Scope("singleton")
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

    private final CardCreateIoForm cardCreateIoForm;


    BoardMenu(IoAdapter ioAdapter, CardCreateIoForm cardCreateIoForm) {
        super(ioAdapter);
        this.cardCreateIoForm = cardCreateIoForm;
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
                Card newCard = cardCreateIoForm.collect(null);
                io.printf("%s\n", newCard.toString());
            }
        }
    }
}