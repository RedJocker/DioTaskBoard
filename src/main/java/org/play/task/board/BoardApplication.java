package org.play.task.board;

import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.menu.MainMenu;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class BoardApplication implements CommandLineRunner {


    private final MainMenu mainMenu;
    private final BoardViewModel boardViewModel;

    public BoardApplication(MainMenu mainMenu, BoardViewModel boardViewModel) {
	this.mainMenu = mainMenu;
        this.boardViewModel = boardViewModel;
    }

    public static void main(String[] args) {
	new SpringApplicationBuilder(BoardApplication.class)
	    .headless(true)
	    .web(WebApplicationType.NONE)
	    .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
	mainMenu.loop(boardViewModel, null);
    }
}
