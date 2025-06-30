package org.play.task.board.presentation.cli.menu.boardMenu;

import org.play.task.board.model.Board;
import org.play.task.board.model.Card;
import org.play.task.board.model.Column;
import org.play.task.board.presentation.cli.BoardViewModel;
import org.play.task.board.presentation.cli.io.IoAdapter;

import java.util.Objects;
import java.util.Optional;

import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.displayCardDetail;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isBlock;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isBlockArg;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isCancel;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isCancelArg;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isMove;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isMoveArg;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isUnblock;
import static org.play.task.board.presentation.cli.menu.boardMenu.BoardMenuHelper.isUnblockArg;

class BoardMenuCardCommands {
    static boolean consumeCardArgCommand(
        IoAdapter io,
        String input,
        BoardViewModel viewModel,
        Board board,
        Column column
    ) {
        if (isMoveArg(input)) {
            String[] parts = input.split(" ");

            Optional<Card> maybeCard = Card.findCardByNameOrId(
                viewModel.cardsAll(board), parts[1]);
            if (maybeCard.isPresent() && Objects.equals(
                    maybeCard.get().columnId(), column.columnId())
            ) {
                Card card = maybeCard.get();
                Column nextColumn = Column
                    .next(viewModel.boardColumns(board), column);
                moveCard(io, viewModel, card, column, nextColumn);
                return true;
            }
        } else if (isBlockArg(input)) {
            String[] parts = input.split(" ");

            Optional<Card> maybeCard = Card.findCardByNameOrId(
                viewModel.cardsAll(board), parts[1]);
            if (maybeCard.isPresent()) {
                Card card = maybeCard.get();
                cardBlock(io, viewModel, card, board);
                showCardDetails(io, viewModel, card, board, true);
                return true;
            }
        } else if (isUnblockArg(input)) {
            String[] parts = input.split(" ");

            Optional<Card> maybeCard = Card.findCardByNameOrId(
                viewModel.cardsAll(board), parts[1]);
            if (maybeCard.isPresent()) {
                Card card = maybeCard.get();
                cardUnblock(io, viewModel, card, board);
                showCardDetails(io, viewModel, card, board, true);
                return true;
            }
        } else if (isCancelArg(input)) {
            String[] parts = input.split(" ");

            Optional<Card> maybeCard = Card.findCardByNameOrId(
                viewModel.cardsAll(board), parts[1]);
            if (maybeCard.isPresent()) {
                Card card = maybeCard.get();
                cardCancel(io, viewModel, card, board);
                showCardDetails(io, viewModel, card, board, true);
                return true;
            }
        }
        return false;
    }

    private static boolean cardCancel(
        IoAdapter io, BoardViewModel viewModel, Card card, Board board
    ) {
        Column column = Column.findColumnById(
            viewModel.boardColumns(board), card.columnId());
        if (column.type() == Column.Type.FINAL
	    || column.type() == Column.Type.CANCELED) {
            io.printf("Final and Canceled are permanent columns\n",
                card.name());
            return false;
        }
        if (card.isBlocked()) {
            io.printf("Card %s is blocked and cannot be moved\n", card.name());
            return false ;
        }

        Column canceledColumn = Column.findCanceled(
            viewModel.boardColumns(board));

        if (viewModel.moveCard(card, canceledColumn)) {
            io.printf("Card %s moved to column %s\n",
                card.name(), canceledColumn.name());
            return true;
        } else {
            io.printf("Failed to move card %s to column %s\n",
                card.name(), canceledColumn.name());
        }
        return false;
    }

    static boolean moveCard(
        IoAdapter io, BoardViewModel viewModel, Card card, Board board
    ) {
        Column column = Column.findColumnById(
            viewModel.boardColumns(board), card.columnId());
        Column next = Column.next(viewModel.boardColumns(board), column);
        return moveCard(io, viewModel, card, column, next);
    }


    static boolean moveCard(
        IoAdapter io,
        BoardViewModel viewModel,
        Card card,
        Column column,
        Column nextColumn
    ) {
        if (column.type() == Column.Type.FINAL
	    || column.type() == Column.Type.CANCELED) {
            io.printf("Final and Canceled are permanent columns\n",
                card.name());
            return false;
        }
        if (card.isBlocked()) {
            io.printf("Card %s is blocked and cannot be moved\n", card.name());
            return false;
        } else if (viewModel.moveCard(card, nextColumn)) {
            io.printf("Card %s moved to column %s\n",
                card.name(), nextColumn.name());
            return true;
        } else {
            io.printf("Failed to move card %s to column %s\n",
                card.name(), nextColumn.name());
        }
        return false;
    }

    static void showCardDetails(
        IoAdapter io,
        BoardViewModel viewModel,
        Card card,
        Board board,
        boolean shouldUpdateCard
    ) {
        if (shouldUpdateCard) {
            Card updatecard = Card
		.findCardById(viewModel.cardsAll(board), card.cardId());
            showCardDetails(io, viewModel, updatecard, board);
        } else {
            showCardDetails(io, viewModel, card, board);
        }
    }

    static void showCardDetails(
        IoAdapter io, BoardViewModel viewModel, Card card, Board board
    ) {
        displayCardDetail(io, viewModel, card, board);

        String input = io.readLine().trim();
        if (input.isBlank())
            return;
        boolean shouldUpdateCard = false;
        if (isBlock(input)) {
            shouldUpdateCard = cardBlock(io, viewModel, card, board);
        } else if (isUnblock(input)) {
            shouldUpdateCard = cardUnblock(io, viewModel, card, board);
        } else if (isCancel(input)) {
            shouldUpdateCard = cardCancel(io, viewModel, card, board);
        } else if(isMove(input)) {
            shouldUpdateCard = moveCard(io, viewModel, card, board);
        }

        showCardDetails(io, viewModel, card, board, shouldUpdateCard);
    }

    static boolean cardUnblock(
        IoAdapter io, BoardViewModel viewModel, Card card, Board board
    ) {
        if (!card.isBlocked()) {
            io.printf("Card %s is already unblocked\n", card.name());
            return false;
        }
        boolean wasUnblocked = viewModel.unblockCard(board, card);
        if (wasUnblocked) {
            io.printf("Card %s unblocked successfully\n", card.name());
        } else {
            io.printf("Failed to unblock card %s\n", card.name());
        }
        return wasUnblocked;
    }

    static boolean cardBlock(
        IoAdapter io, BoardViewModel viewModel, Card card, Board board
    ) {
        if (card.isBlocked()) {
            io.printf("Card %s is already blocked\n", card.name());
            return false;
        }
        boolean wasBlocked = viewModel.blockCard(board, card);
        if (wasBlocked) {
            io.printf("Card %s blocked successfully\n", card.name());
        } else {
            io.printf("Failed to block card %s\n", card.name());
        }
        return wasBlocked;
    }
}
