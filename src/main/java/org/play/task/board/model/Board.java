package org.play.task.board.model;

public record Board(Long id, String name) {
    public static boolean validName(String boardName) {
        return boardName != null
            && !boardName.isBlank()
            && boardName.length() <= 100;
    }
}
