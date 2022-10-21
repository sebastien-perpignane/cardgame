package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;

import static org.mockito.Mockito.*;

public class ContreeTrickTest extends TestCasesManagingPlayers {

    // TODO Test playerPlays when trick is over
    // TODO Test playerPlays when card played is not allowed

    /*@DisplayName("if winner is player 0, player list for next trick is same as players")
    @Test
    public void testBuildPlayerListFromWinner_Player1() {

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        var players = buildPlayers();
        var winner = players.get(0);

        ContreeTrick trick = new ContreeTrick("TEST", trickPlayers, CardSuit.HEARTS, new ContreeGameEventSender());
        trickPlayers.setCurrentTrick(trick);

        var result = trick.buildNextTrickPlayerListFromPreviousWinner(players, winner);

        assertEquals(players, result);

    }

    @DisplayName("if winner is player 2, player list for next trick is : player 2, player 3, player 4, player 1")
    @Test
    public void testBuildPlayerListFromWinner_Player2() {

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        var players = buildPlayers();
        var winner = players.get(1);

        ContreeTrick trick = new ContreeTrick("TEST", trickPlayers, CardSuit.HEARTS, new ContreeGameEventSender());

        var result = trick.buildNextTrickPlayerListFromPreviousWinner(players, winner);

        assertEquals(
                List.of(
                        players.get(1),
                        players.get(2),
                        players.get(3),
                        players.get(0)
                ),
                result
        );

        assertNotEquals(
                List.of(
                        players.get(0),
                        players.get(1),
                        players.get(2),
                        players.get(3)
                ),
                result
        );

    }

    @DisplayName("if winner is player 3, player list for next trick is : player 3, player 4, player 1, player 2")
    @Test
    public void testBuildPlayerListFromWinner_Player3() {

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        var players = buildPlayers();
        var winner = players.get(2);

        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        ContreeTrick trick = new ContreeTrick("TEST", trickPlayers, CardSuit.HEARTS, new ContreeGameEventSender());

        var result = trick.buildNextTrickPlayerListFromPreviousWinner(players, winner);

        assertEquals(
                List.of(
                        players.get(2),
                        players.get(3),
                        players.get(0),
                        players.get(1)
                ),
                result
        );

    }

    @DisplayName("if winner is player 4, player list for next trick is : player 4, player 1, player 2, player 3")
    @Test
    public void testBuildPlayerListFromWinner_Player4() {

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        var players = buildPlayers();
        var winner = players.get(3);

        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        ContreeTrick trick = new ContreeTrick("TEST", trickPlayers, CardSuit.HEARTS, new ContreeGameEventSender());

        var result = trick.buildNextTrickPlayerListFromPreviousWinner(players, winner);

        assertEquals(
                List.of(
                        players.get(3),
                        players.get(0),
                        players.get(1),
                        players.get(2)
                ),
                result
        );

    }*/

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

        var deal = MockDealBuilder.builder().withMockedGameEventSender().withTrumpSuit(CardSuit.HEARTS)
            .build();

        PlayableCardsFilter playableCardsFilter = mock(PlayableCardsFilter.class);

        when(playableCardsFilter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        trick = new ContreeTrick(deal, "TEST", trickPlayers, playableCardsFilter);
    }

}
