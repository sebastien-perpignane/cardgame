package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.buildPlayers;

public class ContreeTrickWinnerTest {

    @DisplayName("non trump trick where player playing ACE must win")
    @Test
    public void testExpectedWinner_noTrump_ace() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_CLUB));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.JACK_CLUB));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.DIAMONDS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_CLUB);
        trick.playerPlays(players.get(3), ClassicalCard.JACK_CLUB);

        assertTrue(trick.isEndOfTrick());
        assertNotNull(trick.getWinner());
        assertSame(players.get(0), trick.getWinner());

    }

    @DisplayName("non trump trick where player playing TEN must win")
    @Test
    public void testExpectedWinner_noTrump_ten() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.TEN_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.EIGHT_CLUB));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.JACK_CLUB));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.DIAMONDS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.TEN_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.EIGHT_CLUB);
        trick.playerPlays(players.get(3), ClassicalCard.JACK_CLUB);

        assertTrue(trick.isEndOfTrick());
        assertNotNull(trick.getWinner());
        assertSame(players.get(0), trick.getWinner());

    }

    @DisplayName("Trump trick where player playing JACK must win")
    @Test
    public void testExpectedWinner_trump_jack() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_CLUB));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.JACK_CLUB));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.CLUBS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_CLUB);
        trick.playerPlays(players.get(3), ClassicalCard.JACK_CLUB);

        assertTrue(trick.isEndOfTrick());
        assertNotNull(trick.getWinner());
        assertSame(players.get(3), trick.getWinner());

    }

    @DisplayName("Trump trick where player playing NINE must win")
    @Test
    public void testExpectedWinner_trump_nine() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_CLUB));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.NINE_CLUB));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.CLUBS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_CLUB);
        trick.playerPlays(players.get(3), ClassicalCard.NINE_CLUB);

        assertTrue(trick.isEndOfTrick());
        assertNotNull(trick.getWinner());
        assertSame(players.get(3), trick.getWinner());

    }

    @DisplayName("Trump trick where player playing ACE must win")
    @Test
    public void testExpectedWinner_trump_ace() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_CLUB));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.EIGHT_CLUB));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.CLUBS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_CLUB);
        trick.playerPlays(players.get(3), ClassicalCard.EIGHT_CLUB);

        assertTrue(trick.isEndOfTrick());
        assertNotNull(trick.getWinner());
        assertSame(players.get(0), trick.getWinner());

    }

    @DisplayName("Trump trick where player playing TEN must win")
    @Test
    public void testExpectedWinner_trump_ten() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.QUEEN_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_CLUB));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.EIGHT_CLUB));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.CLUBS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.QUEEN_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_CLUB);
        trick.playerPlays(players.get(3), ClassicalCard.EIGHT_CLUB);

        assertTrue(trick.isEndOfTrick());
        assertNotNull(trick.getWinner());
        assertSame(players.get(2), trick.getWinner());

    }

    @DisplayName("Trump trick where player playing KING must win")
    @Test
    public void testExpectedWinner_trump_king() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.QUEEN_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.KING_CLUB));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.EIGHT_CLUB));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.CLUBS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.QUEEN_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.KING_CLUB);
        trick.playerPlays(players.get(3), ClassicalCard.EIGHT_CLUB);

        assertTrue(trick.isEndOfTrick());
        assertNotNull(trick.getWinner());
        assertSame(players.get(2), trick.getWinner());

    }

    @DisplayName("Trumped trick where trumping player must win")
    @Test
    public void testExpectedWinner_trumpedTrick() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_DIAMOND));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.JACK_CLUB));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.DIAMONDS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_DIAMOND);
        trick.playerPlays(players.get(3), ClassicalCard.JACK_CLUB);

        assertTrue(trick.isEndOfTrick());
        assertNotNull(trick.getWinner());
        assertSame(players.get(2), trick.getWinner());

    }

    @DisplayName("Trump trick where player playing highest trump must win")
    @Test
    public void testExpectedWinner_multiTrumpedTrick() {

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_DIAMOND));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_DIAMOND));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.ACE_DIAMOND));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.DIAMONDS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_DIAMOND);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_DIAMOND);
        trick.playerPlays(players.get(3), ClassicalCard.ACE_DIAMOND);

        assertTrue(trick.isEndOfTrick());
        assertNotNull(trick.getWinner());
        assertSame(players.get(3), trick.getWinner());

    }

}
