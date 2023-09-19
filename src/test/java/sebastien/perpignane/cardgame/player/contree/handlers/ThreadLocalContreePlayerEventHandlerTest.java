package sebastien.perpignane.cardgame.player.contree.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Set;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

class ContreeBotPlayerEventHandlerTest {

    private ContreePlayer mockPlayer;

    private ContreeBotPlayerEventHandler handler;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(ContreePlayer.class);
        handler = new ContreeBotPlayerEventHandler();
        handler.setPlayer(mockPlayer);
        handler.onGameStarted();
    }

    @DisplayName("The bot plays a card when onPlayerTurn event is triggered")
    @Test
    void testOnPlayerTurn() {
        handler.onPlayerTurn(Set.of(ClassicalCard.ACE_SPADE, ClassicalCard.JACK_SPADE));
        await().atMost(500, MILLISECONDS)
                .untilAsserted(
                    () -> verify(mockPlayer).playCard(any())
                );
    }

    @DisplayName("When game is over, handler internal thread is terminated")
    @Test
    void testOnGameOver() {
        handler.onGameOver();
        await().atMost(500, MILLISECONDS)
                .until(
                    () -> handler.handlerThread.getState() == Thread.State.TERMINATED
                );
    }

    @DisplayName("The bot thread is stopped when the bot is ejected from the game")
    @Test
    void testOnEjection() {
        handler.onEjection();
        await().atMost(500, MILLISECONDS)
                .until(
                        () -> handler.handlerThread.getState() == Thread.State.TERMINATED
                );
    }

    @DisplayName("The bot places a bid when onPlayerTurnToBid event is triggered")
    @Test
    void testOnPlayerTurnToBid() {
        handler.onPlayerTurnToBid(Set.of(ContreeBidValue.PASS));
        await().atMost(500, MILLISECONDS)
                .untilAsserted(
                    () -> verify(mockPlayer).placeBid(any(), any())
                );
    }

    @DisplayName("Interrupted state is kept")
    @Test
    void testManageInterruptedException()  {
        handler.handlerThread.interrupt();
        handler.onPlayerTurnToBid(Set.of(ContreeBidValue.PASS));

        await().atMost(5, SECONDS)
                .untilAsserted(
                    () -> assertThat(handler.handlerThread.isInterrupted()).isTrue()
                );
    }

}