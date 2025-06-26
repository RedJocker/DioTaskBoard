package org.play.task.board.presentation.cli.form;

import org.play.task.board.model.Card;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@Scope("singleton")
public class CardCreateIoForm extends IoForm<Optional<Card>, Void> {

    protected CardCreateIoForm(IoAdapter ioAdapter) {
        super(ioAdapter);
    }

    @Override
    public Optional<Card> collect(Void args) {

        io.printf("New Card:\n");
        while (true){
            io.printf("\tCard name:\n\t");
            final String cardName = io.readLine().trim();

            if (!Card.validName(cardName)) {
                io.printf("\tInvalid card name: '%s'\n", cardName);
                if (tryAgain()) {
                    continue ;
                }
                else
                    break ;
            }

            io.printf("\tCard description:\n\t");
            final String description = io.readLine().trim();

            if (!Card.validDescription(description)) {
                io.printf("\tInvalid card description: '%s'\n", description);
                if (tryAgain()) {
                    continue ;
                }
                else
                    break ;
            }
            return Optional.of(new Card(null,
                    cardName ,
                    description,
                    OffsetDateTime.now(),
                    false
            ));
        }
        return Optional.empty();
    }
}
