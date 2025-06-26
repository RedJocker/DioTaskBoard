package org.play.task.board.model;

import org.play.task.board.util.Either;

public class Column  {
    public enum Type {
        INITIAL,
        PENDING,
        FINAL,
        CANCELED;

    }
    private Long columnId;

    final private Type type;
    final private String name;
    final private int order;

    final private long boardId;

    private Column (Long columnId, Type type, String name, int order, long boardId) {
        this.columnId = columnId;
        this.type = type;
        this.name = name;
        this.order = order;
        this.boardId = boardId;
    }

    public static Column initial(long boardId) {
        return new Column(null, Type.INITIAL, "initial", 0, boardId);
    }

    public static Column finall(long boardId) {
        return new Column(null, Type.FINAL, "final", Integer.MAX_VALUE - 1, boardId);
    }

    public static Column canceled(long boardId) {
        return new Column(null, Type.CANCELED, "canceled", Integer.MAX_VALUE, boardId);
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
                ", boardId=" + boardId +
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
}
