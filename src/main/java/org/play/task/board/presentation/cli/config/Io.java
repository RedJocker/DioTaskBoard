package org.play.task.board.presentation.cli.config;

import org.play.task.board.presentation.cli.io.ConsoleWrapper;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.play.task.board.presentation.cli.io.StreamWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class Io {

    @Bean
    public static IoAdapter ioAdapter() {
        if (System.console() == null) {
            return new StreamWrapper(System.out, System.in);
        } else {
            return new ConsoleWrapper(System.console());
        }
    }

    @Bean
    public static Scanner scanner() {
        return new Scanner(System.in);
    }
}
