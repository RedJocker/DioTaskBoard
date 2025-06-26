package org.play.task.board.presentation.cli.form;

import org.play.task.board.model.Card;
import org.play.task.board.presentation.cli.io.IoAdapter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@Scope("singleton")
public class CardCreateIoForm extends IoForm<Card, Void> {

    protected CardCreateIoForm(IoAdapter ioAdapter) {
        super(ioAdapter);
    }

    @Override
    public Card collect(Void args) {

        io.printf("New Card:\n");
//        while (true){
//            io.printf("How much have you deposited:\n");
//            final Optional<Double> maybeDepositAmount =
//                    io.readDoublerUnsigned();
//
//            if (!maybeDepositAmount.isPresent()) {
//                io.printf("Invalid deposit\n");
//                if (tryAgain()) {
//                    continue ;
//                }
//                else
//                    break ;
//            }
//
//        }
        return new Card(null, "Todo task", "create card from input", OffsetDateTime.now(), false);
    }
}
