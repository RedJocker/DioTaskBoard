package org.play.task.board.presentation.cli.menu;

import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.io.IoAdapter;

abstract class Menu<A> {
    protected static final String invalidChoice = "Invalid Choice\n";
    protected final IoAdapter io;

    protected Menu(IoAdapter ioAdapter) {
        this.io = ioAdapter;
    }

    abstract String getMenuString();

    abstract Integer getMenuSize();

    abstract void loop(BoardViewModel viewModel, A args);

    protected int promptMenuChoice() {
        int option;

        while (true) {
            io.printf(this.getMenuString());
            option = io.readNumberUnsigned();
            if (option < 0 || option >= this.getMenuSize()) {
                io.printf(invalidChoice);
            } else {
                return option;
            }
        }
    }
}