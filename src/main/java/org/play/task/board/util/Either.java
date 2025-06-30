package org.play.task.board.util;

public class Either<Good, Bad> {
    private Good ok;
    private Bad error;

    private Either(Good ok, Bad error) {
        this.ok = ok;
        this.error = error;
    }

    public static <Good, Bad> Either<Good, Bad> good(Good ok) {
        return new Either<>(ok, null);
    }

    public static <Good, Bad> Either<Good, Bad> bad(Bad error) {
        return new Either<>(null, error);
    }

    public Good ok() {
        return ok;
    }

    public Bad error() {
        return error;
    }

    public boolean isGood() {
        return ok != null;
    }

    public boolean isBad() {
        return error != null;
    }
}
