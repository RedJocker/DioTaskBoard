package org.play.task.board.repository;

import org.play.task.board.model.Board;
import org.play.task.board.model.Column;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

@Component
@Scope("singleton")
public class ColumnRepository {
    private final JdbcClient jdbcClient;
    private final TransactionTemplate transactionTemplate;

    public ColumnRepository(JdbcClient jdbcClient, TransactionTemplate transactionTemplate) {
        this.jdbcClient = jdbcClient;
        this.transactionTemplate = transactionTemplate;
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
        try {
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
                    ).param(board.id())
                    .query(Column.class)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public Optional<Column> createPending(Column column) {
        if (column == null
                || column.name() == null
                || column.name().isBlank()
                || column.type() != Column.Type.PENDING

        ) {
            return Optional.empty();
        }

        return transactionTemplate.execute(status -> {
            try {
                KeyHolder keyHolder = new GeneratedKeyHolder();
                jdbcClient.sql("UPDATE COLUMN SET COLUMN_ORDER = COLUMN_ORDER + 1 " +
                                "WHERE BOARD_ID = ? AND COLUMN_ORDER >= ? AND COLUMN_ORDER < ?")
                        .param(column.boardId())
                        .param(column.order())
                        .param(Column.FINAL_ORDER)
                        .update();
                int affectedRows = jdbcClient
                        .sql("INSERT INTO COLUMN (NAME, COLUMN_TYPE, BOARD_ID, COLUMN_ORDER) " +
                                "VALUES (?, ?, ?, ?)")
                        .param(column.name())
                        .param(column.type().name())
                        .param(column.boardId())
                        .param(column.order())
                        .update(keyHolder, "column_id");
                if (affectedRows == 0) {
                    status.setRollbackOnly();
                    return Optional.empty();
                }
                Long newColId = keyHolder.getKeyAs(Long.class);
                return Optional.of(Column.withId(newColId, column));
            } catch (Exception e) {
                status.setRollbackOnly();
                e.printStackTrace();
                return Optional.empty();
            }
        });
    }
}
