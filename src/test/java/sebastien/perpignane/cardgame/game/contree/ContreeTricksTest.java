package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContreeTricksTest extends TestCasesManagingPlayers {

    private ContreeTrickPlayers trickPlayers;

    private ContreeTricks tricks;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    /**
     * Contract is always 80 HEART
     * Mocked PlayableCardsFilter allows any card to simplify tests
     */
    @BeforeEach
    public void setUp() {

        trickPlayers = mock(ContreeTrickPlayers.class);

        ContreeGameEventSender gameEventSender = mock(ContreeGameEventSender.class);

        var deal = MockDealBuilder.builder()
                .withDealContractBid(
                        new ContreeBid(players.get(0), ContreeBidValue.EIGHTY, CardSuit.HEARTS)
                )
                .withTrumpSuit(CardSuit.HEARTS)
                .withGameEventSender(gameEventSender)
                .build();

        // All cards are allowed, it simplifies tests
        PlayableCardsFilter playableCardsFilter = mock(PlayableCardsFilter.class);
        when(playableCardsFilter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        tricks = new ContreeTricks(deal, trickPlayers, playableCardsFilter);

        configureTrickPlayersForNumberOfTricks(1);


    }

    private void configureTrickPlayersForNumberOfTricks(int nbTricks) {

        List<ContreePlayer> multipliedPlayers = loopingPlayers(nbTricks);

        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(multipliedPlayers));

    }

    @DisplayName("Test state of not started tricks")
    @Test
    public void testNotStartedTricks() {

        assert(tricks.lastTrick().isEmpty());
        assertEquals(0, tricks.nbOverTricks());
        assertEquals(0, tricks.nbOngoingTricks());
        assertFalse(tricks.isMaxNbOverTricksReached());

    }

    @DisplayName("When tricks are started, there is one ongoing trick and none over")
    @Test
    public void testStartTricks() {

        tricks.startTricks();

        assertEquals(0, tricks.nbOverTricks());
        assertEquals(1, tricks.nbOngoingTricks());



        tricks.playerPlays(player1, ClassicalCard.JACK_CLUB);
        tricks.playerPlays(player2, ClassicalCard.JACK_CLUB);

    }

    @DisplayName("When 4 cards are played on the current trick, the current trick is over and a new one is started")
    @Test
    public void testWhen4CardsArePlayed() {

        configureTrickPlayersForNumberOfTricks(2);

        tricks.startTricks();

        tricks.playerPlays(player1, ClassicalCard.JACK_HEART);
        tricks.playerPlays(player2, ClassicalCard.JACK_CLUB);
        tricks.playerPlays(player3, ClassicalCard.JACK_CLUB);
        tricks.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertEquals(1, tricks.nbOverTricks());
        assertEquals(1, tricks.nbOngoingTricks());

        tricks.playerPlays(player1, ClassicalCard.JACK_HEART);
        tricks.playerPlays(player2, ClassicalCard.JACK_CLUB);
        tricks.playerPlays(player3, ClassicalCard.JACK_CLUB);
        tricks.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertEquals(2, tricks.nbOverTricks());
        assertEquals(1, tricks.nbOngoingTricks());

    }

    @DisplayName("All tricks are won by team 1, team 1 makes capot")
    @Test
    public void testTeamDoingCapotWhenCapot() {
        configureTrickPlayersForNumberOfTricks(8);

        tricks.startTricks();

        int i = 0;
        while( i < ContreeTricks.NB_TRICKS_PER_DEAL ) {
            tricks.playerPlays(player1, ClassicalCard.JACK_HEART);
            tricks.playerPlays(player2, ClassicalCard.JACK_CLUB);
            tricks.playerPlays(player3, ClassicalCard.JACK_CLUB);
            tricks.playerPlays(player4, ClassicalCard.JACK_CLUB);
            i++;
        }

        assertTrue(tricks.isMaxNbOverTricksReached());
        assertTrue(tricks.isCapot());
        assertTrue(tricks.teamDoingCapot().isPresent());
        assertSame(ContreeTeam.TEAM1, tricks.teamDoingCapot().get());

        assertEquals(8, tricks.nbOverTricks());
        assertEquals(0, tricks.nbOngoingTricks());

    }

    @DisplayName("Not all tricks are won by the same team, no team doing capot")
    @Test
    public void testTeamDoingCapotWhenNoCapot() {

        configureTrickPlayersForNumberOfTricks(8);

        tricks.startTricks();

        // First 7 tricks are won by team 1
        int i = 0;
        while( i < ContreeTricks.NB_TRICKS_PER_DEAL - 1 ) {
            tricks.playerPlays(player1, ClassicalCard.JACK_HEART);
            tricks.playerPlays(player2, ClassicalCard.JACK_CLUB);
            tricks.playerPlays(player3, ClassicalCard.JACK_CLUB);
            tricks.playerPlays(player4, ClassicalCard.JACK_CLUB);
            i++;
        }

        // Final trick is won by team 2
        tricks.playerPlays(player1, ClassicalCard.JACK_CLUB);
        tricks.playerPlays(player2, ClassicalCard.JACK_HEART);
        tricks.playerPlays(player3, ClassicalCard.JACK_CLUB);
        tricks.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertTrue(tricks.isMaxNbOverTricksReached());
        assertTrue(tricks.teamDoingCapot().isEmpty());

        assertEquals(8, tricks.nbOverTricks());
        assertEquals(0, tricks.nbOngoingTricks());

    }

    @DisplayName("Tricks cannot be started multiple times")
    @Test
    public void testStartTricksMultipleTimesFails() {
        tricks.startTricks();

        assertThrows(
                RuntimeException.class,
                () -> tricks.startTricks()
        );

    }

    @DisplayName("teamDoingCapot cannot be called if all tricks were not played yet")
    @Test
    public void testComputeTeamDoingCapotFailsIfTricksAreNotOver() {

        configureTrickPlayersForNumberOfTricks(2);

        tricks.startTricks();

        tricks.playerPlays(player1, ClassicalCard.JACK_HEART);
        tricks.playerPlays(player2, ClassicalCard.JACK_CLUB);
        tricks.playerPlays(player3, ClassicalCard.JACK_CLUB);
        tricks.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertThrows(
                RuntimeException.class,
                () -> tricks.teamDoingCapot()
        );

    }

}
