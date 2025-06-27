package org.play.task.board.util;

public class Pair<F, S> {
    private final F fst;
    private final S snd;

    public Pair(F fst, S snd) {
        this.fst = fst;
        this.snd = snd;
    }

    public F fst() {
        return fst;
    }

    public S snd() {
        return snd;
    }
}
