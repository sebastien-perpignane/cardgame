package sebastien.perpignane.cardgame.player.contree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static sebastien.perpignane.cardgame.card.ClassicalCard.*;

class ContreePlayerImplTest {

    private ContreePlayerImpl contreePlayer;

    @BeforeEach
    public void setUp() {
        ContreePlayerEventHandler eventHandler = mock(ContreePlayerEventHandler.class);
        contreePlayer = new ContreePlayerImpl(eventHandler);
    }

    @Test
    public void testReceiveHand() {

        var hand = List.of(ACE_SPADE, JACK_HEART);
        contreePlayer.receiveHand(hand);

        assertFalse(contreePlayer.hasNoMoreCard());
        assertEquals(hand, contreePlayer.getHand().stream().toList());

    }

    @Test
    public void hasNoMoreCard() {
    }

    @DisplayName("receiveNewCards is not supported")
    @Test
    public void testReceiveNewCardsFails() {
        assertThrows(
            UnsupportedOperationException.class,
            () -> contreePlayer.receiveNewCards(Collections.emptyList())
        );
    }

    @Test
    public void onGameStarted() {
    }

    @Test
    void onGameOver() {
    }

    @Test
    void onPlayerTurn() {
    }

    @Test
    void onGameEjection() {
    }

    @Test
    public void testNbAvailableCards() {

        assertEquals(0, contreePlayer.nbAvailableCards());

        contreePlayer.receiveHand(List.of(TEN_SPADE, NINE_DIAMOND));
        assertEquals(2, contreePlayer.nbAvailableCards());

    }

    @Test
    public void testRemoveCardFromHand() {

        contreePlayer.receiveHand(new ArrayList<>(List.of(JACK_SPADE, ACE_HEART, NINE_HEART)));
        assertTrue(contreePlayer.getHand().contains(ACE_HEART));

        contreePlayer.removeCardFromHand(ACE_HEART);

        assertFalse(contreePlayer.getHand().contains(ACE_HEART));

    }

    @Test
    public void onPlayerTurnToBid() {
    }

    @Test
    void testOnPlayerTurn() {
    }

    @Test
    void sameTeam() {
    }

    @Test
    void playCard() {
    }

    @Test
    void placeBid() {
    }

    @Test
    void leaveGame() {
    }

    @Test
    public void testToState() {

        var state = contreePlayer.toState();

        assertNotNull(state);
        assertNotNull(state.getName());
        assertNotNull(state.getStatus());

    }

    @Test
    public void toFullState() {

        var fullState = contreePlayer.toFullState();

        assertNotNull(fullState);
        assertNotNull(fullState.getName());
        assertNotNull(fullState.getStatus());
        assertNotNull(fullState.getId());
        assertNotNull(fullState.getHand());

    }
}