package org.play.task.board.presentation.cli.form;

public interface Form<R, A> {
    R collect(A args);
}