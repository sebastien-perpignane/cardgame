package sebastien.perpignane.cardgame.game.war;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.*;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.war.WarBotPlayer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WarTrickTest {

    @Test
    @DisplayName("Trick is not over when one player plays one card at the beginning of a trick")
    public void testPlayCardOnEmptyTrick() {

        Player player1 = new WarBotPlayer();
        Player player2 = new WarBotPlayer();

        player1.receiveHand(new ArrayList<>(List.of(ClassicalCard.SIX_CLUB)));
        player2.receiveHand(new ArrayList<>(List.of(ClassicalCard.SIX_DIAMOND)));

        WarTrick trick = new WarTrick("test", Arrays.asList(player1, player2), new WarGameEventSender());
        trick.playerPlay(player1, ClassicalCard.SIX_CLUB);

        assertFalse(trick.isEndOfTrick());

        assertNull(trick.getWinner());

    }

    @Test
    @DisplayName("End of trick when two players play 1 card, if no war situation")
    public void testPlayEndOfTrick_no_war_condition() {

        final int nbTurns = 1;

        Player mockPlayer1 = mock(Player.class);
        when(mockPlayer1.play()).thenReturn(ClassicalCard.SIX_CLUB);
        when(mockPlayer1.hasNoMoreCard()).thenReturn(false);

        Player mockPlayer2 = mock(Player.class);
        when(mockPlayer2.play()).thenReturn(ClassicalCard.TEN_DIAMOND);
        when(mockPlayer2.hasNoMoreCard()).thenReturn(false);

        Trick trick = initAndPlayOnTrick(mockPlayer1, mockPlayer2, nbTurns);

        assertTrue(trick.isEndOfTrick());

        assertEquals(mockPlayer2, trick.getWinner());

    }

    @Test
    @DisplayName("End of trick when 2 players play 3 cards in simple war condition")
    public void testPlayEndOfTrick_war_condition() {

        final int nbTurns = 3;

        Player mockPlayer1 = mock(Player.class);
        when(mockPlayer1.play()).thenReturn(ClassicalCard.SIX_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE);
        when(mockPlayer1.hasNoMoreCard()).thenReturn(false);

        Player mockPlayer2 = mock(Player.class);
        when(mockPlayer2.play()).thenReturn(ClassicalCard.SIX_DIAMOND, ClassicalCard.TEN_DIAMOND, ClassicalCard.TEN_SPADE);
        when(mockPlayer2.hasNoMoreCard()).thenReturn(false);

        WarTrick trick = initAndPlayOnTrick(mockPlayer1, mockPlayer2, nbTurns);

        assertTrue(trick.isEndOfTrick());
        assertSame(mockPlayer1, trick.getWinner());

    }

    @Test
    @DisplayName("End of trick when 2 players play 5 cards in 2 successive war conditions")
    public void testPlayEndOfTrick_successive_war_conditions() {

        final int nbTurns = 5;

        Player mockPlayer1 = mock(Player.class);
        when(mockPlayer1.play()).thenReturn(ClassicalCard.SIX_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE, ClassicalCard.EIGHT_CLUB, ClassicalCard.NINE_CLUB);
        when(mockPlayer1.hasNoMoreCard()).thenReturn(false);

        Player mockPlayer2 = mock(Player.class);
        when(mockPlayer2.play()).thenReturn(ClassicalCard.SIX_DIAMOND, ClassicalCard.TEN_DIAMOND, ClassicalCard.ACE_DIAMOND, ClassicalCard.EIGHT_HEART, ClassicalCard.TEN_HEART);
        when(mockPlayer2.hasNoMoreCard()).thenReturn(false);

        WarTrick trick = initAndPlayOnTrick(mockPlayer1, mockPlayer2, nbTurns);

        assertTrue(trick.isEndOfTrick());

        assertSame(mockPlayer2, trick.getWinner());

    }

    private WarTrick initAndPlayOnTrick(Player player1, Player player2, int nbTurns) {
        WarTrick trick = new WarTrick("test", Arrays.asList(player1, player2), dummyEventSender());

        for (int i = 0; i < nbTurns;i++) {
            trick.playerPlay(player1, player1.play());
            trick.playerPlay(player2, player2.play());
        }
        return trick;
    }

    @Test
    @DisplayName("Error if a player plays a card on a ended trick")
    public void testPlayAfterEndOfTrick() {

        Player player1 = new WarBotPlayer();
        Player player2 = new WarBotPlayer();

        player1.receiveHand(new ArrayList<>(List.of(ClassicalCard.SIX_CLUB)));
        player2.receiveHand(new ArrayList<>(List.of(ClassicalCard.TEN_DIAMOND)));

        WarTrick trick = new WarTrick("test", Arrays.asList(player1, player2), dummyEventSender());
        trick.playerPlay(player1, ClassicalCard.SIX_CLUB);
        trick.playerPlay(player2, ClassicalCard.TEN_DIAMOND);

        assertTrue(trick.isEndOfTrick());

        IllegalStateException ise =
                assertThrows(
                        IllegalStateException.class,
                        () -> trick.playerPlay(player1, ClassicalCard.TEN_CLUB)
                );

        assertNotNull(ise);
        assertEquals("This trick is over", ise.getMessage());

    }

    @Test
    @DisplayName("Trick stops if one player is out of cards during war")
    public void testPrematureEndOfTrickDuringWar() {

        Player player1 = new WarBotPlayer();
        player1.receiveHand(Arrays.asList(ClassicalCard.SIX_CLUB, ClassicalCard.ACE_SPADE, ClassicalCard.KING_HEART));

        Player player2 = new WarBotPlayer();
        player2.receiveHand(Arrays.asList(ClassicalCard.SIX_DIAMOND, ClassicalCard.FIVE_SPADE));

        final boolean[] warHappened = new boolean[1];
        WarTrickObserver observer = cardsTriggeringWar -> warHappened[0] = true;

        WarTrick trick = new WarTrick("test", Arrays.asList(player1, player2), new WarGameEventSender(observer));
        trick.playerPlay(player1, player1.play());
        trick.playerPlay(player2, player2.play());
        // WAR

        assertTrue(warHappened[0]);

        trick.playerPlay(player1, player1.play());
        trick.playerPlay(player2, player2.play());
        // Player 2 has no more card to play

        assertTrue(player2.hasNoMoreCard());
        assertTrue(trick.isEndOfTrick());
        //assertTrue(trick.isPrematureEndOfTrick());
        assertSame(player1, trick.getWinner());

    }

    private WarGameEventSender dummyEventSender() {
        return new WarGameEventSender();
    }

}
