package org.play.task.board.presentation.cli.io;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class StreamWrapper implements IoAdapter {
    PrintStream out;
    Scanner in;

    public StreamWrapper(PrintStream out, InputStream in) {
        this.out = out;
        this.in = new Scanner(in);
    }

    @Override
    public IoAdapter printf(String format, Object ... args) {
        out.printf(format, args);
        return this;
    }
    @Override
    public String readLine(String fmt, Object ... args) {
        out.printf(fmt, args);
        return this.readLine();
    }
    @Override
    public String readLine() {
        return in.hasNextLine() ? in.nextLine() : null;
    }
    @Override
    public String readPassword(String fmt, Object ... args) {
        out.print("Warning: this is not a fully functional terminal\n");
        out.print("Password will not be hidden while typing it\n");
        out.printf(fmt, args);
        return this.readPassword();
    }

    @Override
    public String readPassword() {
        out.print("Warning: this is not a fully functional terminal\n");
        out.print("Password will not be hidden while typing it\n");
        return this.readLine();
    }
}
