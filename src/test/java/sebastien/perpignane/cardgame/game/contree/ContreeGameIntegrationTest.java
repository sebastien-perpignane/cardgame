package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.BlockingQueueGameObserver;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.player.contree.ContreeBotPlayer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContreeGameIntegrationTest {

    @DisplayName("Running a game with bot players, including one always bidding 80 HEART. The game must end without error, whoever wins.")
    @Test
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    @Disabled
    public void testRunGameWithBotsPlayingRandomCards() throws InterruptedException {

        ContreeGameEventSender eventSender = new ContreeGameEventSender();
        ContreeGamePlayers players = new ContreeGamePlayersImpl();
        ContreeGameConfig config = new ContreeGameConfig();
        ContreeGameScore gameScore = new ContreeGameScore(config.maxScore());
        PlayableCardsFilter filter = new PlayableCardsFilter();
        DealScoreCalculator dealScoreCalculator = new DealScoreCalculator();
        ContreeDeals deals = new ContreeDeals(gameScore, dealScoreCalculator, filter, eventSender);
        ContreeGame game = new ContreeGame(players, deals, eventSender);
        game.registerAsGameObserver(GameTextDisplayer.getInstance());
        ContreeBotPlayer player1 = new TestBiddingContreePlayer(ContreeBidValue.EIGHTY, CardSuit.HEARTS);

        game.joinGame(player1);
        game.joinGame(new ContreeBotPlayer());
        game.joinGame(new ContreeBotPlayer());
        game.joinGame(new ContreeBotPlayer());
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
