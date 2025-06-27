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
public class BoardRepository {
    private final JdbcClient jdbcClient;
    private final TransactionTemplate transactionTemplate;

    public BoardRepository(JdbcClient jdbcClient, TransactionTemplate transactionTemplate) {
        this.jdbcClient = jdbcClient;
        this.transactionTemplate = transactionTemplate;
    }

    public List<Board> getAll() {
        return jdbcClient.sql("SELECT " +
                        "BOARD_ID as `id`, " +
                        "NAME " +
                        "FROM BOARD;"
                ).query(Board.class)
                .list();
    }

    public boolean deleteBoard(Board board) {
        return jdbcClient.sql("DELETE FROM BOARD WHERE BOARD_ID = ?")
                .param(board.id())
                .update() == 1;

    }

    public Optional<Board> save(Board board) {
        if (board == null || !Board.validName(board.name())) {
            return Optional.empty();
        }

        return transactionTemplate.execute(status -> {
            try {
                // Create the board
                KeyHolder keyHolder = new GeneratedKeyHolder();
                int affectedRows = jdbcClient.sql("INSERT INTO BOARD (NAME) VALUES (?)")
                        .param(board.name())
                        .update(keyHolder, "board_id");

                if (affectedRows == 0) {
                    status.setRollbackOnly();
                    return Optional.empty();
                }

                Long boardId = keyHolder.getKeyAs(Long.class);

                // Add required columns to the board
                Column initial = Column.initial(boardId);
                String inserColumnSql = "INSERT INTO COLUMN (NAME, COLUMN_TYPE, BOARD_ID, COLUMN_ORDER) VALUES (?, ?, ?, ?)";
                int initialColumnRows = jdbcClient.sql(inserColumnSql)
                        .param(initial.name())
                        .param(initial.type().name())
                        .param(initial.boardId())
                        .param(initial.order())
                        .update();
                Column finall = Column.finall(boardId);

                int finalColumnRows = jdbcClient.sql(inserColumnSql)
                        .param(finall.name())
                        .param(finall.type().name())
                        .param(finall.boardId())
                        .param(finall.order())
                        .update();

                Column canceled = Column.canceled(boardId);

                int canceledColumnRows = jdbcClient.sql(inserColumnSql)
                        .param(canceled.name())
                        .param(canceled.type().name())
                        .param(canceled.boardId())
                        .param(canceled.order())
                        .update();

                if (initialColumnRows == 0 || finalColumnRows == 0 || canceledColumnRows == 0) {
                    status.setRollbackOnly();
                    return Optional.empty();
                }

                return Optional.of(new Board(boardId, board.name()));
            } catch (Exception e) {
                status.setRollbackOnly();
                e.printStackTrace();
                return Optional.empty();
            }
        });
    }
}