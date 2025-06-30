package org.play.task.board.presentation.cli.form;

import org.play.task.board.model.Board;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Scope("singleton")
public class CreateBoardIoForm extends IoForm<Optional<Board>, Void> {

    protected CreateBoardIoForm(IoAdapter ioAdapter) {
        super(ioAdapter);
    }

    @Override
    public Optional<Board> collect(Void ignore) {
        while (true) {
            io.printf("\tBoard name:\n\t");
            final String boardName = io.readLine().trim();

            if (!Board.validName(boardName)) {
                io.printf("\tInvalid card name: '%s'\n", boardName);
                if (tryAgain()) {
                    continue;
                } else
                    break;
            }

            return Optional.of(new Board(
                    null,
                    boardName
                ));
        }

        return Optional.empty();
    }
}
