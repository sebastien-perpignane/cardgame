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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContreeDealsTest extends TestCasesManagingPlayers {

    private ContreeDealPlayers dealPlayers;

    private ContreeGameScore gameScore;

    private ContreeDeals deals;

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

        PlayableCardsFilter filter = mock(PlayableCardsFilter.class);
        when(filter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        gameScore = mock(ContreeGameScore.class);
        DealScoreCalculator dealScoreCalculator = mock(DealScoreCalculator.class);


        deals = new ContreeDeals(gameScore, dealScoreCalculator, filter, eventSender);

    }

    @DisplayName("When deals are started, a current deal is available")
    @Test
    void getCurrentDeal() {

        deals.startDeals("TEST", dealPlayers);

        assertNotNull(deals.getCurrentDeal());
        assertEquals(1, deals.getNbDeals());
        assertEquals(0, deals.nbOverDeals());
        assertEquals(1, deals.nbOngoingDeals());

    }

    @DisplayName("When one deal is over, 2 deals exist, one is over and one is in progress")
    @Test
    void testNbDeals() {

        deals.startDeals("TEST", dealPlayers);

        deals.placeBid(player1, ContreeBidValue.NONE, null);
        deals.placeBid(player2, ContreeBidValue.NONE, null);
        deals.placeBid(player3, ContreeBidValue.NONE, null);
        deals.placeBid(player4, ContreeBidValue.NONE, null);

        assertEquals(2, deals.getNbDeals());

        assertEquals(1, deals.nbOverDeals());
        assertEquals(1, deals.nbOngoingDeals());

    }

    @DisplayName("When maximum score is reached, cards cannot be played anymore")
    @Test
    public void testCannotPlayWhenMaxScoreReached() {

        when(gameScore.isMaximumScoreReached()).thenReturn(true);
        assertTrue(deals.isMaximumScoreReached());

        assertThrows(
            RuntimeException.class,
            () -> deals.playCard(player1, ClassicalCard.JACK_DIAMOND)
        );

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

        deals.placeBid(player1, ContreeBidValue.EIGHTY, CardSuit.HEARTS);
        deals.placeBid(player2, ContreeBidValue.NONE, null);
        deals.placeBid(player3, ContreeBidValue.NONE, null);
        deals.placeBid(player4, ContreeBidValue.NONE, null);

        int i = 0;

        while (i < 8) {

            deals.playCard(player1, ClassicalCard.JACK_HEART);
            deals.playCard(player2, ClassicalCard.ACE_SPADE);
            deals.playCard(player3, ClassicalCard.ACE_HEART);
            deals.playCard(player4, ClassicalCard.ACE_CLUB);

            i++;
        }

        assertTrue(calledFlag[0]);

    }

}