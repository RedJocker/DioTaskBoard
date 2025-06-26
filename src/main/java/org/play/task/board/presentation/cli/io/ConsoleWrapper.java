package org.play.task.board.presentation.cli.io;

import java.io.Console;

public class ConsoleWrapper implements IoAdapter {

    final Console console;

    public ConsoleWrapper(Console console) {
        this.console = console;
    }

    @Override
    public IoAdapter printf(String format, Object ... args) {
        console.printf(format, args);
        return this;
    }
    @Override
    public String readLine(String fmt, Object ... args) {
        return console.readLine(fmt, args);
    }
    @Override
    public String readLine() {
        return console.readLine();
    }
    @Override
    public String readPassword(String fmt, Object ... args) {
        char[] arr = console.readPassword(fmt, args);
        if (arr == null)
            return null;
        return String.valueOf(arr);
    }
    @Override
    public String readPassword() {
        char[] arr = console.readPassword();
        if (arr == null)
            return null;
        return String.valueOf(arr);
    }
}
