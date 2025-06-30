package org.play.task.board.presentation.cli.io;

import java.util.Optional;

public interface IoAdapter {
    public IoAdapter printf(String format, Object... args);
    public String readLine(String fmt, Object ... args);
    public String readLine();
    public String readPassword(String fmt, Object ... args);
    public String readPassword();

    default int readNumberUnsigned() {
        String inputLine;
        int option;

        inputLine = this.readLine();
        if (inputLine == null)
            return 0;
        try {
            option = Integer.parseInt(inputLine);
            if (option < 0) {
                option = -1;
            }
        } catch (NumberFormatException ex) {
            option = -1;
        }
        return option;
    }

    default Optional<Double> readDoublerUnsigned() {
        String inputLine;
        Optional<Double> maybeNumber;

        inputLine = this.readLine();
        if (inputLine == null)
            return Optional.empty();
        try {
            double number = Double.parseDouble(inputLine);
            if (number < 0) {
                maybeNumber = Optional.empty();
            } else {
                maybeNumber = Optional.of(number);
            }
        } catch (NumberFormatException ex) {
            maybeNumber = Optional.empty();
        }
        return maybeNumber;
    }
}
