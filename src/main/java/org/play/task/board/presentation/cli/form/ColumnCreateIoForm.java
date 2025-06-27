package org.play.task.board.presentation.cli.form;


import org.play.task.board.model.Board;
import org.play.task.board.model.Column;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.play.task.board.util.Either;
import org.play.task.board.util.Pair;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Scope("singleton")
public class ColumnCreateIoForm extends IoForm<Optional<Column>, Pair<Board, List<Column>>> {

    protected ColumnCreateIoForm(IoAdapter ioAdapter) {
        super(ioAdapter);
    }

    @Override
    public Optional<Column> collect(Pair<Board, List<Column>> args) {
        final Board board = args.fst();
        final List<Column> columns = args.snd();

        io.printf("New Column:\n");
        while (true){
            io.printf("\tColumn name:\n\t");
            final String columnName = io.readLine().trim();

            if (!Column.validName(columnName)) {
                io.printf("\tInvalid Column name: '%s'\n", columnName);
                if (tryAgain()) {
                    continue ;
                }
                else
                    break ;
            }

            io.printf("\tAfter Which Column?:\n");
            columns.stream()
                    .takeWhile(col -> col.type() != Column.Type.FINAL && col.type() != Column.Type.CANCELED)
                    .forEach(col -> io.printf("\t\t\t- (%d) %s\n", col.columnId(), col.name()));
            final String columnBeforeEitherNameOrId = io.readLine().trim();
            final Optional<Column> maybeColumnBefore = Column
                    .findColumnByNameOrId(columns, columnBeforeEitherNameOrId);

            if (maybeColumnBefore.isEmpty()) {
                io.printf("\tWas not found column with name or id: '%s'\n", columnBeforeEitherNameOrId);
                if (tryAgain()) {
                    continue ;
                }
                else
                    break ;
            }
            Either<Column, String> newColumn = Column.pending(
                    columnName, maybeColumnBefore.get().order() + 1, board.id()
            );
            if (newColumn.isBad()) {
                io.printf("\t%s\n", newColumn.error());
                if (tryAgain()) {
                    continue ;
                }
                else
                    break ;
            } else {
                return Optional.of(newColumn.ok());
            }
        }
        return Optional.empty();
    }
}
