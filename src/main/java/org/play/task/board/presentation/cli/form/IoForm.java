package org.play.task.board.presentation.cli.form;

import org.play.task.board.presentation.cli.io.IoAdapter;


public abstract class IoForm<R, A> implements Form<R, A>{
    protected final IoAdapter io;

    protected IoForm(IoAdapter console) {
        this.io = console;
    }

    protected boolean tryAgain() {
        final String tryAgainStr =
            io.readLine("Quit (0) TryAgain (Any)\n");
        return tryAgainStr != null && !tryAgainStr.equals("0");
    }
}