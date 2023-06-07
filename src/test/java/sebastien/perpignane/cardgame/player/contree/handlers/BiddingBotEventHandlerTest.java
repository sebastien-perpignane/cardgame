package sebastien.perpignane.cardgame.player.contree.handlers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Set;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

class BiddingBotEventHandlerTest {

    @DisplayName("Always bid 80 HEART, whatever contains allowedBidValues")
    @Test
    void testOnPlayerTurnToBid() {

        ContreePlayer mockPlayer = mock(ContreePlayer.class);
        BiddingBotEventHandler handler = new BiddingBotEventHandler();
        handler.setPlayer(mockPlayer);

        handler.onGameStarted();
        handler.onPlayerTurnToBid(Set.of(ContreeBidValue.PASS));
        await().atMost(500, MILLISECONDS)
                .untilAsserted(
                        () -> verify(mockPlayer, times(1)).placeBid(ContreeBidValue.EIGHTY, CardSuit.HEARTS)
                );

    }

}