package sebastien.perpignane.cardgame.game.war;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSetShuffler;
import sebastien.perpignane.cardgame.game.BlockingQueueGameObserver;
import sebastien.perpignane.cardgame.game.GameState;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.game.WarPlayer1WinShuffler;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.war.WarBotPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class WarGameIntegrationTest {

    @Test
    @DisplayName("Player 1 has only superior cards and must win the game")
    public void testPlayer1HasBestCardsAndWins() throws InterruptedException {

        Player player1 = new WarBotPlayer();
        Player player2 = new WarBotPlayer();

        WarGame warGame = new WarGame();
        warGame.joinGame(player1);
        warGame.joinGame(player2);

        Assertions.assertEquals(GameState.INITIALIZED, warGame.getState());

        CardSetShuffler shuffler = new WarPlayer1WinShuffler();
        List<ClassicalCard> cards = shuffler.shuffle(CardSet.GAME_32);
        warGame.startGame(cards);

        boolean endOfGame = waitForEndOfGameEvent(warGame);

        Assertions.assertTrue(endOfGame);
        Assertions.assertSame(player1, warGame.getWinner());

    }

    @Test
    @DisplayName("Player 1 wins 1st trick but loses the game")
    public void testPlayer1Win1TrickButLoses() throws InterruptedException {

        Player player1 = new WarBotPlayer();
        Player player2 = new WarBotPlayer();

        WarGame warGame = new WarGame();
        warGame.joinGame(player1);
        warGame.joinGame(player2);

        Assertions.assertEquals(GameState.INITIALIZED, warGame.getState());
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
        Assertions.assertTrue(endOfGame);
        Assertions.assertSame(player2, warGame.getWinner());

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
