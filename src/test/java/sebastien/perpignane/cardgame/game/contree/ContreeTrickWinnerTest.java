package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.buildPlayers;

public class ContreeTrickWinnerTest extends TestCasesManagingPlayers {

    private ContreeTrickPlayers trickPlayers;

    private ContreeTrick trick;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    public void setUp() {
        trickPlayers = mock(ContreeTrickPlayers.class);
        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        var deal = MockDealBuilder.builder().withMockedGameEventSender().withTrumpSuit(CardSuit.CLUBS)
                .build();

        PlayableCardsFilter playableCardsFilter = mock(PlayableCardsFilter.class);

        when(playableCardsFilter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        trick = new ContreeTrick(deal, "TEST", trickPlayers, playableCardsFilter);
    }

    @DisplayName("non trump trick where player playing ACE must win")
    @Test
    public void testExpectedWinner_noTrump_ace() {
        trick.startTrick();

        trick.playerPlays(player1, ClassicalCard.ACE_DIAMOND);
        trick.playerPlays(player2, ClassicalCard.SEVEN_DIAMOND);
        trick.playerPlays(player3, ClassicalCard.TEN_DIAMOND);
        trick.playerPlays(player4, ClassicalCard.JACK_DIAMOND);

        assertTrue(trick.isOver());
        assertNotNull(trick.getWinner());
        assertSame(player1, trick.getWinner());

    }


    @DisplayName("non trump trick where player playing TEN must win")
    @Test
    public void testExpectedWinner_noTrump_ten() {
        trick.startTrick();

        trick.playerPlays(player1, ClassicalCard.TEN_DIAMOND);
        trick.playerPlays(player2, ClassicalCard.SEVEN_DIAMOND);
        trick.playerPlays(player3, ClassicalCard.EIGHT_DIAMOND);
        trick.playerPlays(player4, ClassicalCard.JACK_DIAMOND);

        assertTrue(trick.isOver());
        assertNotNull(trick.getWinner());
        assertSame(player1, trick.getWinner());

    }

    @DisplayName("Trump trick where player playing JACK must win")
    @Test
    public void testExpectedWinner_trump_jack() {

        trick.startTrick();

        trick.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trick.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(player3, ClassicalCard.TEN_CLUB);
        trick.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertTrue(trick.isOver());
        assertNotNull(trick.getWinner());
        assertSame(player4, trick.getWinner());

    }

    @DisplayName("Trump trick where player playing NINE must win")
    @Test
    public void testExpectedWinner_trump_nine() {

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_CLUB));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.NINE_CLUB));

        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        ContreeTrick trick = new ContreeTrick("TEST", trickPlayers, CardSuit.CLUBS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_CLUB);
        trick.playerPlays(players.get(3), ClassicalCard.NINE_CLUB);

        assertTrue(trick.isOver());
        assertNotNull(trick.getWinner());
        assertSame(players.get(3), trick.getWinner());

    }

    @DisplayName("Trump trick where player playing ACE must win")
    @Test
    public void testExpectedWinner_trump_ace() {

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_CLUB));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.EIGHT_CLUB));

        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        ContreeTrick trick = new ContreeTrick("TEST", trickPlayers, CardSuit.CLUBS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_CLUB);
        trick.playerPlays(players.get(3), ClassicalCard.EIGHT_CLUB);

        assertTrue(trick.isOver());
        assertNotNull(trick.getWinner());
        assertSame(players.get(0), trick.getWinner());

    }

    @DisplayName("Trump trick where player playing TEN must win")
    @Test
    public void testExpectedWinner_trump_ten() {

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.QUEEN_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_CLUB));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.EIGHT_CLUB));

        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        ContreeTrick trick = new ContreeTrick("TEST", trickPlayers, CardSuit.CLUBS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.QUEEN_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_CLUB);
        trick.playerPlays(players.get(3), ClassicalCard.EIGHT_CLUB);

        assertTrue(trick.isOver());
        assertNotNull(trick.getWinner());
        assertSame(players.get(2), trick.getWinner());

    }

    @DisplayName("Trump trick where player playing KING must win")
    @Test
    public void testExpectedWinner_trump_king() {

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.QUEEN_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.KING_CLUB));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.EIGHT_CLUB));

        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        ContreeTrick trick = new ContreeTrick("TEST", trickPlayers, CardSuit.CLUBS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.QUEEN_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.KING_CLUB);
        trick.playerPlays(players.get(3), ClassicalCard.EIGHT_CLUB);

        assertTrue(trick.isOver());
        assertNotNull(trick.getWinner());
        assertSame(players.get(2), trick.getWinner());

    }

    @DisplayName("Trumped trick where trumping player must win")
    @Test
    public void testExpectedWinner_trumpedTrick() {

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_CLUB));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_DIAMOND));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.JACK_CLUB));

        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        ContreeTrick trick = new ContreeTrick("TEST", trickPlayers, CardSuit.DIAMONDS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_DIAMOND);
        trick.playerPlays(players.get(3), ClassicalCard.JACK_CLUB);

        assertTrue(trick.isOver());
        assertNotNull(trick.getWinner());
        assertSame(players.get(2), trick.getWinner());

    }

    @DisplayName("Trump trick where player playing highest trump must win")
    @Test
    public void testExpectedWinner_multiTrumpedTrick() {

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        var players = buildPlayers();

        when(players.get(0).getHand()).thenReturn(List.of(ClassicalCard.ACE_CLUB));
        when(players.get(1).getHand()).thenReturn(List.of(ClassicalCard.SEVEN_DIAMOND));
        when(players.get(2).getHand()).thenReturn(List.of(ClassicalCard.TEN_DIAMOND));
        when(players.get(3).getHand()).thenReturn(List.of(ClassicalCard.ACE_DIAMOND));

        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        ContreeTrick trick = new ContreeTrick("TEST", trickPlayers, CardSuit.DIAMONDS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(players.get(0), ClassicalCard.ACE_CLUB);
        trick.playerPlays(players.get(1), ClassicalCard.SEVEN_DIAMOND);
        trick.playerPlays(players.get(2), ClassicalCard.TEN_DIAMOND);
        trick.playerPlays(players.get(3), ClassicalCard.ACE_DIAMOND);

        assertTrue(trick.isOver());
        assertNotNull(trick.getWinner());
        assertSame(players.get(3), trick.getWinner());

    }

}
