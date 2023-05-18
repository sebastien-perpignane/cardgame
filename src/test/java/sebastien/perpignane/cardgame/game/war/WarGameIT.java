package sebastien.perpignane.cardgame.game.war;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSetShuffler;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.BlockingQueueGameObserver;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.game.WarPlayer1WinShuffler;
import sebastien.perpignane.cardgame.player.war.local.thread.AbstracLocalThreadWarPlayer;
import sebastien.perpignane.cardgame.player.war.local.thread.WarBotPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;

@Tag(("integration-test"))
public class WarGameIT {

    @Test
    @DisplayName("Player 1 has only superior cards and must win the game")
    public void testPlayer1HasBestCardsAndWins() throws InterruptedException {

        AbstracLocalThreadWarPlayer player1 = new WarBotPlayer("Player 1");
        AbstracLocalThreadWarPlayer player2 = new WarBotPlayer("Player 2");

        WarGame warGame = new WarGame();
        warGame.joinGame(player1);
        warGame.joinGame(player2);

        assertThat(warGame.isInitialized()).isTrue();

        CardSetShuffler shuffler = new WarPlayer1WinShuffler();
        List<ClassicalCard> cards = shuffler.shuffle(CardSet.GAME_32);
        warGame.startGame(cards);

        boolean endOfGame = waitForEndOfGameEvent(warGame);

        assertThat(endOfGame).isTrue();
        assertThat(warGame.getWinner()).isSameAs(player1);

    }

    @Test
    @DisplayName("Player 1 wins 1st trick but loses the game")
    public void testPlayer1Win1TrickButLoses() throws InterruptedException {

        AbstracLocalThreadWarPlayer player1 = new WarBotPlayer("Player 1");
        AbstracLocalThreadWarPlayer player2 = new WarBotPlayer("Player 2");

        WarGame warGame = new WarGame();
        warGame.joinGame(player1);
        warGame.joinGame(player2);

        assertThat(warGame.isInitialized()).isTrue();
        GameTextDisplayer observer = GameTextDisplayer.getInstance();
        warGame.registerAsGameObserver(observer);
        warGame.registerAsTrickObserver(observer);

        List<ClassicalCard> cards = Arrays.asList(
                ClassicalCard.SEVEN_DIAMOND,
                ClassicalCard.SEVEN_CLUB,
                ClassicalCard.SEVEN_HEART,
                ClassicalCard.EIGHT_SPADE,
                ClassicalCard.EIGHT_CLUB,
                ClassicalCard.SIX_CLUB,
                ClassicalCard.KING_CLUB,
                ClassicalCard.KING_DIAMOND,
                ClassicalCard.KING_HEART,
                ClassicalCard.KING_SPADE
        );

        warGame.startGame(cards);

        boolean endOfGame = waitForEndOfGameEvent(warGame);
        assertThat(endOfGame).isTrue();
        assertThat(warGame.getWinner()).isSameAs(player2);

    }

    private boolean waitForEndOfGameEvent(WarGame warGame) throws InterruptedException {
        BlockingQueue<String> msgQueue = new ArrayBlockingQueue<>(1);
        warGame.registerAsGameObserver(new BlockingQueueGameObserver(msgQueue));

        boolean endOfGame = false;
        String msg = msgQueue.take();
        if ("END_OF_GAME".equals(msg)) {
            endOfGame = true;
        }
        return endOfGame;
    }

}
