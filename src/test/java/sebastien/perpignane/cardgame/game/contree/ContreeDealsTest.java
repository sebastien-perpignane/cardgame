package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// TODO [Unit test]

class ContreeDealsTest extends TestCasesManagingPlayers {

    private ContreeDeals deals;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {

        ContreeGameEventSender eventSender = mock(ContreeGameEventSender.class);

        ContreeGame game = mock(ContreeGame.class);
        when(game.getEventSender()).thenReturn(eventSender);

        ContreeBidPlayers bidPlayers = mock(ContreeBidPlayers.class);
        when(bidPlayers.getCurrentBidder()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        ContreeDealPlayers dealPlayers = mock(ContreeDealPlayers.class);
        when(dealPlayers.getNumberOfPlayers()).thenReturn(ContreePlayers.NB_PLAYERS);
        when(dealPlayers.buildBidPlayers()).thenReturn(bidPlayers);

        deals = new ContreeDeals(game, dealPlayers);

    }

    @DisplayName("When deals are started, a current deal is available")
    @Test
    void getCurrentDeal() {

        deals.startDeals();

        assertNotNull(deals.getCurrentDeal());
        assertEquals(1, deals.getNbDeals());
        assertEquals(0, deals.nbOverDeals());
        assertEquals(1, deals.nbOngoingDeals());

    }

    @DisplayName("When one deal is over, 2 deals exist, one is over and one is in progress")
    @Test
    void testNbDeals() {

        deals.startDeals();

        deals.placeBid(player1, ContreeBidValue.NONE, null);
        deals.placeBid(player2, ContreeBidValue.NONE, null);
        deals.placeBid(player3, ContreeBidValue.NONE, null);
        deals.placeBid(player4, ContreeBidValue.NONE, null);

        assertEquals(2, deals.getNbDeals());

        assertEquals(1, deals.nbOverDeals());
        assertEquals(1, deals.nbOngoingDeals());

    }
}