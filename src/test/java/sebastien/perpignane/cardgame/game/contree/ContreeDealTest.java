package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
 ContreDeal responsibilities are :
    * manage that all conditions are met to start a deal *
    * manage steps of a deal *
    * prevents actions that are now allowed during the current step *
    * define the trump suit of the deal at the end of the bid step *
 */
public class ContreeDealTest extends TestCasesManagingPlayers {

    private ContreeDealPlayers dealPlayers;

    private ContreeDeal deal;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    public void setUp() {

        ContreeBidPlayers bidPlayers = mock(ContreeBidPlayers.class);
        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        when(bidPlayers.getCurrentBidder()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));
        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        dealPlayers = mock(ContreeDealPlayers.class);
        when(dealPlayers.buildBidPlayers()).thenReturn(bidPlayers);
        when(dealPlayers.buildTrickPlayers()).thenReturn(trickPlayers);
        when(dealPlayers.getNumberOfPlayers()).thenReturn(4);

        ContreeGameEventSender eventSender = mock(ContreeGameEventSender.class);

        ContreeGame game = mock(ContreeGame.class);
        when(game.getGameId()).thenReturn("TEST");
        when(game.getNbDeals()).thenReturn(-1);
        when(game.getEventSender()).thenReturn(eventSender);

        deal = new ContreeDeal(game, dealPlayers);

    }

    @DisplayName("When a deal is started without players, starting the deal fails")
    @Test
    public void testStartDealFailsWhenNoPlayers() {
        when(dealPlayers.getNumberOfPlayers()).thenReturn(0);
        when(dealPlayers.getCurrentDealPlayers()).thenReturn(Collections.emptyList());

        assertThrows(
                RuntimeException.class,
                () -> deal.startDeal()
        );

    }

    @DisplayName("When a deal is started with a number of players not allowing fair card distribution, starting the deal fails")
    @Test
    public void testStartDealFailsWhenBadNumberOfPlayers() {
        when(dealPlayers.getNumberOfPlayers()).thenReturn(3);
        when(dealPlayers.getCurrentDealPlayers()).thenReturn(List.of(player1, player2, player3));

        assertThrows(
                RuntimeException.class,
                () -> deal.startDeal()
        );

    }

    @DisplayName("When only NONE bids, dealStep is not PLAY, deal is over")
    @Test
    public void testOnlyNoneBids() {

        deal.startDeal();

        deal.placeBid( new ContreeBid(player1, ContreeBidValue.NONE) );
        deal.placeBid( new ContreeBid(player2, ContreeBidValue.NONE) );
        deal.placeBid( new ContreeBid(player3, ContreeBidValue.NONE) );
        deal.placeBid( new ContreeBid(player4, ContreeBidValue.NONE) );

        assertTrue(deal.hasOnlyNoneBids());
        assertTrue(deal.isOver());
        assertFalse(deal.isPlayStep());

    }

    @DisplayName("After deal.startDeal(), the deal is in bid step")
    @Test
    public void testDealIsInBidStepAfterStart() {
        deal.startDeal();

        assertTrue( deal.isBidStep() );
        assertFalse( deal.isPlayStep() );
    }

    @DisplayName("When bids are over, the deal is in play step. Trump suit is defined.")
    @Test
    public void testDealIsInPlayStepWhenBidsAreOver() {
        deal.startDeal();

        placeBidsWithPlayer1BiddingForHeartAndOthersPass(deal);

        assertFalse( deal.isBidStep() );
        assertTrue( deal.isPlayStep() );
        assertNotNull( deal.getTrumpSuit() );

    }


    @DisplayName("Players cannot play a card in BID step ")
    @Test
    public void testExceptionWhenPlayingWhileBidStep() {
        deal.startDeal();

        assertTrue(deal.isBidStep());

        assertThrows(
            RuntimeException.class,
            () -> deal.playerPlays(players.get(0), ClassicalCard.JACK_DIAMOND)
        );

    }

    @DisplayName("Players cannot place a bid in PLAY step")
    @Test
    public void testExceptionWhenPlacingBidDuringPlayStep() {

        deal.startDeal();

        placeBidsWithPlayer1BiddingForHeartAndOthersPass(deal);

        assertTrue( deal.isPlayStep() );

        assertThrows(
            RuntimeException.class,
            () -> deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.NONE, null))
        );
    }

    private void placeBidsWithPlayer1BiddingForHeartAndOthersPass(ContreeDeal deal) {
        deal.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.HEARTS));
        deal.placeBid(new ContreeBid(player2, ContreeBidValue.NONE, null));
        deal.placeBid(new ContreeBid(player3, ContreeBidValue.NONE, null));
        deal.placeBid(new ContreeBid(player4, ContreeBidValue.NONE, null));
    }

}
