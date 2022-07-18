package sebastien.perpignane.cardgame.game.war;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.Card;
import sebastien.perpignane.cardgame.game.PlayedCard;
import sebastien.perpignane.cardgame.game.WarTrick;
import sebastien.perpignane.cardgame.game.WarTrickObserver;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.WarBotPlayer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WarTrickTest {

    @Test
    @DisplayName("Trick is not over when one player plays one card at the beginning of a trick")
    public void testPlayCardOnEmptyTrick() {

        Player player1 = new WarBotPlayer();
        Player player2 = new WarBotPlayer();

        player1.receiveHand(new ArrayList<>(List.of(Card.SIX_CLUB)));
        player2.receiveHand(new ArrayList<>(List.of(Card.SIX_DIAMOND)));

        WarTrick trick = new WarTrick("test", List.of(player1, player2), new ArrayList<>());
        trick.playerPlay(new PlayedCard(player1, Card.SIX_CLUB));

        assertFalse(trick.isEndOfTrick());

        assertNull(trick.getWinner());

    }

    @Test
    @DisplayName("End of trick when two players play 1 card, if no war situation")
    public void testPlayEndOfTrick_no_war_condition() {

        Player player1 = new WarBotPlayer();
        Player player2 = new WarBotPlayer();

        player1.receiveHand(new ArrayList<>(List.of(Card.SIX_CLUB)));
        player2.receiveHand(new ArrayList<>(List.of(Card.TEN_DIAMOND)));

        WarTrick trick = new WarTrick("test", List.of(player1, player2), new ArrayList<>());
        trick.playerPlay(new PlayedCard(player1, Card.SIX_CLUB));
        trick.playerPlay(new PlayedCard(player2, Card.TEN_DIAMOND));

        assertTrue(trick.isEndOfTrick());

        assertEquals(player2, trick.getWinner());

    }

    @Test
    @DisplayName("End of trick when 2 players play 3 cards in simple war condition")
    public void testPlayEndOfTrick_war_condition() {

        Player player1 = new WarBotPlayer();
        Player player2 = new WarBotPlayer();

        player1.receiveHand(new ArrayList<>(Arrays.asList(Card.SIX_CLUB, Card.ACE_CLUB, Card.ACE_SPADE)));
        player2.receiveHand(new ArrayList<>(Arrays.asList(Card.SIX_DIAMOND, Card.TEN_DIAMOND, Card.TEN_SPADE)));

        WarTrick trick = new WarTrick("test", List.of(player1, player2), new ArrayList<>());
        trick.playerPlay(new PlayedCard(player1, Card.SIX_CLUB));
        trick.playerPlay(new PlayedCard(player2, Card.SIX_DIAMOND));

        assertFalse(trick.isEndOfTrick());

        trick.playerPlay(new PlayedCard(player1, Card.ACE_CLUB));
        trick.playerPlay(new PlayedCard(player2, Card.TEN_DIAMOND));

        trick.playerPlay(new PlayedCard(player1, Card.ACE_SPADE));
        trick.playerPlay(new PlayedCard(player2, Card.TEN_SPADE));

        assertTrue(trick.isEndOfTrick());

        assertSame(player1, trick.getWinner());

    }

    @Test
    @DisplayName("End of trick when 2 players play 5 cards in 2 successive war conditions")
    public void testPlayEndOfTrick_successive_war_conditions() {

        Player player1 = new WarBotPlayer();
        Player player2 = new WarBotPlayer();

        player1.receiveHand(new ArrayList<>(Arrays.asList(Card.SIX_CLUB, Card.ACE_CLUB, Card.ACE_SPADE, Card.EIGHT_CLUB, Card.NINE_CLUB)));
        player2.receiveHand(new ArrayList<>(Arrays.asList(Card.SIX_DIAMOND, Card.TEN_DIAMOND, Card.ACE_DIAMOND, Card.EIGHT_HEART, Card.TEN_HEART)));

        WarTrick trick = new WarTrick("test", List.of(player1, player2), new ArrayList<>());
        trick.playerPlay(new PlayedCard(player1, Card.SIX_CLUB));
        trick.playerPlay(new PlayedCard(player2, Card.SIX_DIAMOND));

        assertFalse(trick.isEndOfTrick());

        trick.playerPlay(new PlayedCard(player1, Card.ACE_CLUB));
        trick.playerPlay(new PlayedCard(player2, Card.TEN_DIAMOND));

        trick.playerPlay(new PlayedCard(player1, Card.ACE_SPADE));
        trick.playerPlay(new PlayedCard(player2, Card.ACE_DIAMOND));

        assertFalse(trick.isEndOfTrick());

        trick.playerPlay(new PlayedCard(player1, Card.EIGHT_CLUB));
        trick.playerPlay(new PlayedCard(player2, Card.EIGHT_HEART));

        trick.playerPlay(new PlayedCard(player1, Card.NINE_CLUB));
        trick.playerPlay(new PlayedCard(player2, Card.TEN_HEART));

        assertTrue(trick.isEndOfTrick());

        assertSame(player2, trick.getWinner());

    }

    @Test
    @DisplayName("Error if a player plays a card on a ended trick")
    public void testPlayAfterEndOfTrick() {

        Player player1 = new WarBotPlayer();
        Player player2 = new WarBotPlayer();

        player1.receiveHand(new ArrayList<>(List.of(Card.SIX_CLUB)));
        player2.receiveHand(new ArrayList<>(List.of(Card.TEN_DIAMOND)));

        WarTrick trick = new WarTrick("test", List.of(player1, player2), new ArrayList<>());
        trick.playerPlay(new PlayedCard(player1, Card.SIX_CLUB));
        trick.playerPlay(new PlayedCard(player2, Card.TEN_DIAMOND));

        assertTrue(trick.isEndOfTrick());

        IllegalStateException ise =
                assertThrows(
                        IllegalStateException.class,
                        () -> trick.playerPlay(new PlayedCard(player1, Card.TEN_CLUB))
                );

        assertNotNull(ise);
        assertEquals("This trick is over", ise.getMessage());

    }

    @Test
    @DisplayName("Trick stops if one player is out of cards during war")
    public void testPrematureEndOfTrickDuringWar() {

        Player player1 = new WarBotPlayer();
        player1.receiveHand(List.of(Card.SIX_CLUB, Card.ACE_SPADE, Card.KING_HEART));

        Player player2 = new WarBotPlayer();
        player2.receiveHand(List.of(Card.SIX_DIAMOND, Card.FIVE_SPADE));

        final boolean[] warHappened = new boolean[1];
        WarTrickObserver observer = new WarTrickObserver() {
            @Override
            public void onWar(List<PlayedCard> cards) {
                warHappened[0] = true;
            }
        };

        WarTrick trick = new WarTrick("test", List.of(player1, player2), List.of(observer));
        trick.playerPlay(new PlayedCard(player1, player1.play()));
        trick.playerPlay(new PlayedCard(player2, player2.play()));
        // WAR

        assertTrue(warHappened[0]);

        trick.playerPlay(new PlayedCard(player1, player1.play()));
        trick.playerPlay(new PlayedCard(player2, player2.play()));
        // Player 2 has no more card to play

        Assertions.assertTrue(player2.hasNoMoreCard());
        Assertions.assertTrue(trick.isEndOfTrick());
        Assertions.assertTrue(trick.isPrematureEndOfTrick());
        Assertions.assertSame(player1, trick.getWinner());

    }

}
