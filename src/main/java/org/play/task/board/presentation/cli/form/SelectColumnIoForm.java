package org.play.task.board.presentation.cli.form;

import org.play.task.board.model.Column;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.play.task.board.presentation.cli.menu.BoardMenu.LIST_ITEM_MS1;

@Component
@Scope("singleton")
public class SelectColumnIoForm extends IoForm<Optional<Column>, List<Column>> {

    protected SelectColumnIoForm(IoAdapter ioAdapter) {
        super(ioAdapter);
    }


    @Override
    public Optional<Column> collect(List<Column> columns) {
        io.printf("\nChoose a Column:\n");
        for (Column column : columns) {
            io.printf(LIST_ITEM_MS1, column.columnId(), column.name());
        }

        final String input = io.readLine().trim();
        return Column.findColumnByNameOrId(columns, input);
    }
}