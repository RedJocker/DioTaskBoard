package org.play.task.board.repository;

import org.play.task.board.model.Board;
import org.play.task.board.model.Column;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("singleton")
public class ColumnRepository {
    private final JdbcClient jdbcClient;

    public ColumnRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Column> getAll() {
        return jdbcClient.sql("SELECT " +
                            "column_id, " +
                            "column_type as type, " +
                            "name, " +
                            "created_at, " +
                            "column_order as 'order', " +
                            "board_id " +
                        "FROM COLUMN ORDER BY column_order"
                ).query(Column.class)
                .list();
    }

    public List<Column> getAllFromBoard(Board board) {
        return jdbcClient.sql("SELECT " +
                        "column_id, " +
                        "column_type as `type`, " +
                        "name, " +
                        "created_at, " +
                        "column_order as `order`, " +
                        "board_id " +
                        "FROM `COLUMN` " +
                        "WHERE board_id = ? " +
                        "ORDER BY column_order"
                ).param(board.boardId())
                .query(Column.class)
                .list();
    }
}
