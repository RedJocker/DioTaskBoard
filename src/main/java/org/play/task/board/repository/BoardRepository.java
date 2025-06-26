package org.play.task.board.repository;

import org.play.task.board.model.Board;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("singleton")
public class BoardRepository {
    private final JdbcClient jdbcClient;

    public BoardRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
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
}