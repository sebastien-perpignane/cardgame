package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.buildPlayers;

import java.util.List;

import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

public class ContreeTrickLifeCycleTest {

    @Test
    public void testTrickIsNotOverAfterOnePlayedCard() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));

        ContreeTrick trick = new ContreeTrick(players, CardSuit.DIAMONDS);
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);

        assertFalse(trick.isEndOfTrick());
        assertNull(trick.getWinner());

    }

    @Test
    public void testTrickIsNotOverAfterTwoPlayedCards() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));

        ContreeTrick trick = new ContreeTrick(players, CardSuit.DIAMONDS);
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);

        assertFalse(trick.isEndOfTrick());
        assertNull(trick.getWinner());

    }

    @Test
    public void testTrickIsNotOverAfterThreePlayedCards() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_CLUB));

        ContreeTrick trick = new ContreeTrick(players, CardSuit.DIAMONDS);
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_CLUB);

        assertFalse(trick.isEndOfTrick());
        assertNull(trick.getWinner());

    }

    @Test
    public void testTrickIsOverAfterFourPlayedCards() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_CLUB));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.JACK_CLUB));

        ContreeTrick trick = new ContreeTrick(players, CardSuit.DIAMONDS);
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_CLUB);
        trick.playerPlays(players.get(3), ClassicalCard.JACK_CLUB);

        assertTrue(trick.isEndOfTrick());
        assertNotNull(trick.getWinner());

    }



}
