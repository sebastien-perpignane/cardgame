package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.BlockingQueueGameObserver;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.player.contree.ContreeBotPlayer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ContreeGameIntegrationTest {

    // TODO Test when a new deal is started after only NONE bids
    // TODO Test joining a game when there are at least 1 bot
    // TODO Test joining a full game (no bots)

    @DisplayName("Running a game with bot players, including one always bidding 80 HEART. The game must end without error, whoever wins.")
    @Test
    public void testRunGameWithBotsPlayingRandomCards() {
        ContreeGame game = new ContreeGame(GameTextDisplayer.getInstance());
        ContreeBotPlayer player1 = new TestBiddingContreePlayer(ContreeBidValue.EIGHTY, CardSuit.HEARTS);

        game.joinGame(player1);
        game.joinGame(new ContreeBotPlayer());
        game.joinGame(new ContreeBotPlayer());
        game.joinGame(new ContreeBotPlayer());
        game.startGame();
        assertTrue(true);
        var endOfGame = waitForEndOfGameEvent(game);
        assertTrue(endOfGame);

    }

    private boolean waitForEndOfGameEvent(ContreeGame game) {
        boolean endOfGame = false;
        try {
            BlockingQueue<String> msgQueue = new ArrayBlockingQueue<>(1);
            game.registerAsGameObserver(new BlockingQueueGameObserver(msgQueue));


            String msg = msgQueue.poll(10, TimeUnit.SECONDS);
            if ("END_OF_GAME".equals(msg)) {
                endOfGame = true;
            }
        }
        catch(Exception e) {
            System.err.println("Fuck");
        }

        return endOfGame;
    }

}
