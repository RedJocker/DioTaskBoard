package org.play.task.board.presentation.cli.form;

import org.play.task.board.model.Board;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Scope("singleton")
public class SelectBoardIoForm extends IoForm<Optional<Board>, List<Board>> {

    protected SelectBoardIoForm(IoAdapter ioAdapter) {
        super(ioAdapter);
    }


    @Override
    public Optional<Board> collect(List<Board> boards) {

        for (Board board : boards) {
            io.printf("\n\n\t- (%d) %s\n", board.id(), board.name());
        }
        io.printf("\t Choose a board: \n");

        final String input = io.readLine().trim();
        return boards.stream()
                .filter(b ->b.name().equalsIgnoreCase(input) || b.id().toString().equals(input))
                .findFirst();
    }
}