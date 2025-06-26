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

    private Column (Long columnId, Type type, String name, int order) {
        this.columnId = columnId;
        this.type = type;
        this.name = name;
        this.order = order;
    }

    public static Column initial() {
        return new Column(null, Type.INITIAL, "initial", 0);
    }

    public static Column finall() {
        return new Column(null, Type.FINAL, "final", Integer.MAX_VALUE - 1);
    }

    public static Column canceled() {
        return new Column(null, Type.CANCELED, "canceled", Integer.MAX_VALUE);
    }

    public static Either<Column, String> pending(String name, int order) {
        final String nameNorm = name.trim().toLowerCase();
        if (nameNorm.equals("initial") || nameNorm.equals("final") || nameNorm.equals("canceled"))
            return Either.bad("pending column cannot have reserved column name " + nameNorm);
        if (order < 0)
            return Either.bad("invalid column order " + order + " must be greater than 0");
        if (order == 0 || order >= Integer.MAX_VALUE - 1) {
            return Either.bad("invalid column order" + order + " is reserved value");
        }
        return Either.good(new Column(null, Type.PENDING, nameNorm, order));
    }

    @Override
    public String toString() {
        return "Column{" +
                "boardId=" + columnId +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", order=" + order +
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

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }
}
