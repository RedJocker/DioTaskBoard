package org.play.task.board;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Scanner;

record Card(Long id, String name, String description, OffsetDateTime createdAt) {}

@SpringBootApplication
public class BoardApplication implements CommandLineRunner {

	public BoardApplication(JdbcClient jdbcClient, Scanner scanner) {
		this.jdbcClient = jdbcClient;
        this.scanner = scanner;
    }

	final JdbcClient jdbcClient;
	final Scanner scanner;

	public static void main(String[] args) {
		SpringApplication.run(BoardApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Hello");
		while (scanner.hasNext()) {
			scanner.nextLine();
			final List<Card> cards = jdbcClient.sql("SELECT * FROM CARD")
					.query(Card.class)
					.list();
			System.out.println(cards);
		}
	}

	@Bean
	public static Scanner scanner() {
		return new Scanner(System.in);
	}
}
