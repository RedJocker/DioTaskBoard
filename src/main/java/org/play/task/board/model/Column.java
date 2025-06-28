package org.play.task.board.model;

import org.play.task.board.util.Either;

import java.util.List;
import java.util.Optional;

public class Column  {

    public static final int INITIAL_ORDER = 0;
    public static final int FINAL_ORDER = Integer.MAX_VALUE - 1;
    public static final int CANCELED_ORDER = Integer.MAX_VALUE;


    public static boolean validName(String cardName) {
        if (cardName == null || cardName.isBlank())
            return false;
        final String nameNorm = cardName.trim().toLowerCase();
        return !nameNorm.equals("initial") && !nameNorm.equals("final") && !nameNorm.equals("canceled");
    }

    public static Column withId(Long id, Column column) {
        return new Column(
                id,
                column.type,
                column.name,
                column.order,
                column.boardId
        );
    }

    public static Column next(List<Column> boardColumns, Column column) {
        Optional<Column> next = boardColumns.stream()
                .dropWhile(col -> col.order() <= column.order())
                .findFirst();
        return next.orElse(column);
    }

    public static Column previous(List<Column> boardColumns, Column column) {
        List<Column> previousList = boardColumns.stream()
                .takeWhile(col -> col.order() < column.order())
                .toList();

        return previousList.isEmpty() ? column : previousList.getLast();
    }

    public static Column findColumnById(List<Column> boardColumns, Long id) {
        return boardColumns.stream()
                .filter(col -> col.columnId().equals(id))
                .findFirst().orElseThrow();
    }

    public enum Type {
        INITIAL,
        PENDING,
        FINAL,
        CANCELED;

    }
    final private Long columnId;

    final private Type type;
    final private String name;
    final private int order;

    final private long boardId;

    public Column(Long columnId, Type type, String name, int order, long boardId) {
        this.columnId = columnId;
        this.type = type;
        this.name = name;
        this.order = order;
        this.boardId = boardId;
    }

    public static Optional<Column> findColumnByNameOrId(List<Column> columns, String nameOrId) {
        return columns.stream()
                .filter(col -> col.name().equalsIgnoreCase(nameOrId)
                        || col.columnId().toString().equals(nameOrId))
                .findFirst();
    }

    public static Column initial(long boardId) {
        return new Column(null, Type.INITIAL, "initial", INITIAL_ORDER, boardId);
    }

    public static Column finall(long boardId) {
        return new Column(null, Type.FINAL, "final", FINAL_ORDER, boardId);
    }

    public static Column canceled(long boardId) {
        return new Column(null, Type.CANCELED, "canceled", CANCELED_ORDER, boardId);
    }

    public static Either<Column, String> pending(String name, int order, long boardId) {
        final String nameNorm = name.trim().toLowerCase();
        if (nameNorm.equals("initial") || nameNorm.equals("final") || nameNorm.equals("canceled"))
            return Either.bad("pending column cannot have reserved column name " + nameNorm);
        if (order < 0)
            return Either.bad("invalid column order " + order + " must be greater than 0");
        if (order == 0 || order >= Integer.MAX_VALUE - 1) {
            return Either.bad("invalid column order" + order + " is reserved value");
        }
        return Either.good(new Column(null, Type.PENDING, nameNorm, order, boardId));
    }

    @Override
    public String toString() {
        return "Column{" +
                "columnId=" + columnId +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", order=" + order +
                ", id=" + boardId +
                '}';
    }

    public Long columnId() {
        return columnId;
    }

    public Type type() {
        return type;
    }

    public String name() {
        return name;
    }

    public int order() {
        return order;
    }

    public long boardId() {
        return boardId;
    }
}
