package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardDealer;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContreeDealsTest extends TestCasesManagingPlayers {

    private ContreeDealPlayers dealPlayers;

    private ContreeGameScore gameScore;

    private ContreeDeals deals;

    private ContreePlayer newPlayer;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {

        ContreeGameEventSender eventSender = mock(ContreeGameEventSender.class);

        ContreeBidPlayers bidPlayers = mock(ContreeBidPlayers.class);
        when(bidPlayers.getCurrentBidder()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        dealPlayers = mock(ContreeDealPlayers.class);
        when(dealPlayers.getNumberOfPlayers()).thenReturn(ContreePlayers.NB_PLAYERS);
        when(dealPlayers.buildBidPlayers()).thenReturn(bidPlayers);

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);
        List<ContreePlayer> multipliedPlayers = loopingPlayers(8);
        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(multipliedPlayers));
        when(dealPlayers.buildTrickPlayers()).thenReturn(trickPlayers);

        BiddableValuesFilter biddableValuesFilter = mock(BiddableValuesFilter.class);

        PlayableCardsFilter filter = mock(PlayableCardsFilter.class);
        when(filter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        gameScore = mock(ContreeGameScore.class);
        DealScoreCalculator dealScoreCalculator = mock(DealScoreCalculator.class);
        when(dealScoreCalculator.computeDealScores(any())).thenReturn(new DealScoreResult(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap()));

        CardDealer cardDealer = mock(CardDealer.class);

        when(biddableValuesFilter.biddableValues(any(), any())).thenReturn(new BiddableValuesFilter.BidFilterResult(
                new HashSet<>(Arrays.stream(ContreeBidValue.values()).toList()),
                Collections.emptyMap()
        ));

        newPlayer = mock(ContreePlayer.class);

        deals = new ContreeDeals(gameScore, dealScoreCalculator, biddableValuesFilter, filter, cardDealer, eventSender);

    }

    @DisplayName("When deals are started, there is an ongoing deal")
    @Test
    void getCurrentDeal() {

        deals.startDeals("TEST", dealPlayers);

        assertEquals(1, deals.getNbDeals());
        assertEquals(0, deals.nbOverDeals());
        assertEquals(1, deals.nbOngoingDeals());

    }

    @DisplayName("When one deal is over, 2 deals exist, one is over and one is in progress")
    @Test
    void testNbDeals() {

        deals.startDeals("TEST", dealPlayers);

        deals.placeBid(player1, ContreeBidValue.PASS, null);
        deals.placeBid(player2, ContreeBidValue.PASS, null);
        deals.placeBid(player3, ContreeBidValue.PASS, null);
        deals.placeBid(player4, ContreeBidValue.PASS, null);

        assertEquals(2, deals.getNbDeals());

        assertEquals(1, deals.nbOverDeals());
        assertEquals(1, deals.nbOngoingDeals());

    }

    @DisplayName("When maximum score is reached, cards cannot be played anymore")
    @Test
    public void testCannotPlayWhenMaxScoreReached() {

        when(gameScore.isMaximumScoreReached()).thenReturn(true);
        when(gameScore.getWinner()).thenReturn(Optional.of(ContreeTeam.TEAM1));
        assertTrue(deals.isMaximumScoreReached());


        assertThrows(
            RuntimeException.class,
            () -> deals.playCard(player1, ClassicalCard.JACK_DIAMOND)
        );
        assertTrue(deals.isMaximumScoreReached());
        assertTrue(deals.getWinner().isPresent());

    }

    @DisplayName("When a deal is over, the score of the deal is added to the score of the game")
    @Test
    public void testScoreIsUpdatedAtEndOfDeal() {

        boolean[] calledFlag = {false};
        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(gameScore).addDealScore(any());

        deals.startDeals("TEST", dealPlayers);

        playFullDeal();

        assertTrue(calledFlag[0]);
        assertTrue(deals.getWinner().isEmpty());

    }

    private void playFullDeal() {

        deals.placeBid(player1, ContreeBidValue.EIGHTY, CardSuit.HEARTS);
        deals.placeBid(player2, ContreeBidValue.PASS, null);
        deals.placeBid(player3, ContreeBidValue.PASS, null);
        deals.placeBid(player4, ContreeBidValue.PASS, null);

        int i = 0;

        while (i < 8) {

            deals.playCard(player1, ClassicalCard.JACK_HEART);
            deals.playCard(player2, ClassicalCard.ACE_SPADE);
            deals.playCard(player3, ClassicalCard.ACE_HEART);
            deals.playCard(player4, ClassicalCard.ACE_CLUB);

            i++;
        }
    }

    @DisplayName("No exception when managing a leaving player on not started deals")
    @Test
    public void testManageLeavingPlayer_notStartedDeals() {

        boolean exception = false;

        when(player1.isBot()).thenReturn(false);

        try {
            deals.manageLeavingPlayer(player1, newPlayer);
        }
        catch(Exception e) {
            exception = true;
        }

        assertFalse(exception);

    }

    @DisplayName("No exception when managing a leaving player on started deals")
    @Test
    public void testManageLeavingPlayer_startedDeals() {

        boolean exception = false;

        when(player1.isBot()).thenReturn(false);

        deals.startDeals("TEST", dealPlayers);

        try {
            deals.manageLeavingPlayer(player1, newPlayer);
        }
        catch(Exception e) {
            exception = true;
        }

        assertFalse(exception);

    }

    @DisplayName("No exception when managing a leaving player on started deals")
    @Test
    public void testManageLeavingPlayer_startedDealsAndPlayStepStarted() {

        boolean exception = false;

        when(player1.isBot()).thenReturn(false);

        deals.startDeals("TEST", dealPlayers);
        deals.placeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS);
        deals.placeBid(player2, ContreeBidValue.PASS, null);
        deals.placeBid(player3, ContreeBidValue.PASS, null);
        deals.placeBid(player4, ContreeBidValue.PASS, null);


        try {
            deals.manageLeavingPlayer(player1, newPlayer);
        }
        catch(Exception e) {
            exception = true;
        }

        assertFalse(exception);

    }

}
