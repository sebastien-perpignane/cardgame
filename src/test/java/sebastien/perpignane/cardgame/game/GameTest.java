package sebastien.perpignane.cardgame.game;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.WarBotPlayer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static sebastien.perpignane.cardgame.game.WarPlayer1WinShuffler.bestCardValues;
import static java.util.Collections.emptyList;

public class GameTest {

    @Test
    @DisplayName("Player 1 has only superior cards and must win the game")
    public void testPlayer1HasBestCardsAndWins() throws InterruptedException {

        Player player1 = new WarBotPlayer();
        Player player2 = new WarBotPlayer();

        Game game = new Game(CardSet.GAME_32, new WarPlayer1WinShuffler(), emptyList(), emptyList());
        game.joinGame(player1);
        game.joinGame(player2);

        Assertions.assertEquals(GameState.INITIALIZED, game.getState());

        Assertions.assertEquals(32, game.getCards().size());
        Assertions.assertTrue(bestCardValues.contains(game.getCards().get(1).getValue()));

        game.startGame();

        boolean endOfGame = waitForEndOfGameEvent(game);

        Assertions.assertTrue(endOfGame);
        Assertions.assertSame(game.getWinner(), player1);

    }

    @Test
    @DisplayName("Player 1 wins 1st trick but loses the game")
    public void testPlayer1Win1TrickButLoses() throws InterruptedException {

        Player player1 = new WarBotPlayer();
        Player player2 = new WarBotPlayer();

        Game game = new Game(CardSet.GAME_32, new WarPlayer2WinButLosesOneTrickShuffler(), emptyList(), emptyList());
        game.joinGame(player1);
        game.joinGame(player2);

        Assertions.assertEquals(GameState.INITIALIZED, game.getState());
        var observer = new GameTextDisplayer();
        game.registerAsGameObserver(observer);
        game.registerAsTrickObserver(observer);

        game.startGame();

        boolean endOfGame = waitForEndOfGameEvent(game);
        Assertions.assertTrue(endOfGame);
        Assertions.assertSame(game.getWinner(), player2);

    }

    private boolean waitForEndOfGameEvent(Game game) throws InterruptedException {
        BlockingQueue<String> msgQueue = new ArrayBlockingQueue<>(1);
        game.registerAsGameObserver(new BlockingQueueGameObserver(msgQueue));

        boolean endOfGame = false;
        String msg = msgQueue.take();
        if ("END_OF_GAME".equals(msg)) {
            endOfGame = true;
        }
        return endOfGame;
    }

}
