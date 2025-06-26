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
        // TODO select board with user input
        io.printf("Boards: %s\n", boards);
        return Optional.of(boards.getFirst());
    }
}