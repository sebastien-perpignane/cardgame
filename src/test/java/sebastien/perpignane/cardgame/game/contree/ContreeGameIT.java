package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.BlockingQueueGameObserver;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.player.contree.ContreeBotPlayer;
import sebastien.perpignane.cardgame.player.contree.refactor.ContreeBotPlayerEventHandler;
import sebastien.perpignane.cardgame.player.contree.refactor.RefactoredContreePlayer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("integration-test")
public class ContreeGameIT {

    @DisplayName("Running a game with bot players, including one always bidding 80 HEART. The game must end without error, whoever wins.")
    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
    public void testRunGameWithBotsPlayingRandomCards() throws InterruptedException {
        ContreeGame game = ContreeGameFactory.createGame(500);
        game.registerAsGameObserver(GameTextDisplayer.getInstance());
        ContreeBotPlayer player1 = new BiddingContreeBotPlayer(ContreeBidValue.EIGHTY, CardSuit.HEARTS);

        game.joinGame(player1);
        game.joinGame(new RefactoredContreePlayer(new ContreeBotPlayerEventHandler()));
        game.joinGame(new RefactoredContreePlayer(new ContreeBotPlayerEventHandler()));
        game.joinGame(new RefactoredContreePlayer(new ContreeBotPlayerEventHandler()));
        var endOfGame = waitForEndOfGameEvent(game);
        assertTrue(endOfGame);

    }

    private boolean waitForEndOfGameEvent(ContreeGame game) throws InterruptedException {
        boolean endOfGame = false;

        BlockingQueue<String> msgQueue = new ArrayBlockingQueue<>(1);
        game.registerAsGameObserver(new BlockingQueueGameObserver(msgQueue));


        String msg = msgQueue.poll(10, TimeUnit.SECONDS);
        if ("END_OF_GAME".equals(msg)) {
            endOfGame = true;
        }

        return endOfGame;
    }

}
