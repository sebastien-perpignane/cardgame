package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardDealer;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*

 ContreDeal responsibilities are :
    * manage that all conditions are met to start a deal *
    * manage steps of a deal *
    * prevents actions that are now allowed during the current step *
    * define the trump suit of the deal at the end of the bid step *

  (!) Method coverage % will be low as a lot of methods are "delegate" methods in ContreeDeal

 */
public class ContreeDealTest extends TestCasesManagingPlayers {

    private ContreeDealPlayers dealPlayers;

    private ContreeDealBids bids;


    private ContreeTricks tricks;

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

        bids = mock(ContreeDealBids.class);
        tricks = mock(ContreeTricks.class);
        ContreeDealScore score = mock(ContreeDealScore.class);

        CardDealer cardDealer = mock(CardDealer.class);
        when(cardDealer.dealCards(anyList(), anyInt())).thenReturn(List.of(
                new ArrayList<>(CardSet.GAME_32.allOf(CardSuit.SPADES)),
                new ArrayList<>(CardSet.GAME_32.allOf(CardSuit.HEARTS)),
                new ArrayList<>(CardSet.GAME_32.allOf(CardSuit.CLUBS)),
                new ArrayList<>(CardSet.GAME_32.allOf(CardSuit.DIAMONDS))
        ));

        deal = new ContreeDeal(bids, tricks, cardDealer, score, eventSender);

    }

    @DisplayName("When a deal is started without players, starting the deal fails")
    @Test
    public void testStartDealFailsWhenNoPlayers() {
        when(dealPlayers.getNumberOfPlayers()).thenReturn(0);
        when(dealPlayers.getCurrentDealPlayers()).thenReturn(Collections.emptyList());

        assertThrows(
                RuntimeException.class,
                () -> deal.startDeal("TEST", dealPlayers)
        );

    }

    @DisplayName("When a deal is started with a number of players not allowing fair card distribution, starting the deal fails")
    @Test
    public void testStartDealFailsWhenBadNumberOfPlayers() {
        when(dealPlayers.getNumberOfPlayers()).thenReturn(3);
        when(dealPlayers.getCurrentDealPlayers()).thenReturn(List.of(player1, player2, player3));

        assertThrows(
                RuntimeException.class,
                () -> deal.startDeal("TEST", dealPlayers)
        );

    }

    @DisplayName("After deal.startDeal(), the deal is in bid step")
    @Test
    public void testDealIsInBidStepAfterStart() {
        deal.startDeal("TEST", dealPlayers);

        assertTrue( deal.isBidStep() );
        assertFalse( deal.isPlayStep() );
    }

    @DisplayName("When bids are over, the deal is in play step. Trump suit is defined.")
    @Test
    public void testDealIsInPlayStepWhenBidsAreOver() {
        deal.startDeal("TEST", dealPlayers);
        ContreeBid bid = new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS);
        when(bids.bidsAreOver()).thenReturn(true);
        when(bids.highestBid()).thenReturn(Optional.of(bid));

        deal.placeBid(bid);

        assertFalse( deal.isBidStep() );
        assertTrue( deal.isPlayStep() );
        assertNotNull( deal.getTrumpSuit() );

    }


    @DisplayName("Players cannot play a card in BID step ")
    @Test
    public void testExceptionWhenPlayingWhileBidStep() {
        deal.startDeal("TEST", dealPlayers);

        assertTrue(deal.isBidStep());

        assertThrows(
            RuntimeException.class,
            () -> deal.playerPlays(player1, ClassicalCard.JACK_DIAMOND)
        );

    }

    @DisplayName("Players cannot place a bid in PLAY step")
    @Test
    public void testExceptionWhenPlacingBidDuringPlayStep() {

        // Given

        deal.startDeal("TEST", dealPlayers);

        ContreeBid bid = new ContreeBid(player1, ContreeBidValue.HUNDRED, CardSuit.DIAMONDS);
        when(bids.bidsAreOver()).thenReturn(true);
        when(bids.highestBid()).thenReturn(Optional.of(bid));// When bids are over, startPlay method is called and gets the trump suit.
        deal.placeBid(new ContreeBid( player1 ));

        // When
        Executable placeBidAction = () -> deal.placeBid(new ContreeBid( player2 ));

        // Then
        assertTrue(deal.isPlayStep());
        var ise = assertThrows(
                RuntimeException.class,
                placeBidAction
        );
        assertTrue(ise.getMessage().contains("A bid cannot be placed during PLAY step"));
    }

    @DisplayName("One play step reached, deal is over when tricks are over")
    @Test
    public void testPlayerPlaysWhenTricksAreOver() {

        goToPlayStep();

        when(tricks.tricksAreOver()).thenReturn(true);

        deal.playerPlays(player1, ClassicalCard.JACK_DIAMOND);

        assertTrue(deal.isOver());

    }

    @DisplayName("One play step reached, deal is not over when tricks are not over")
    @Test
    public void testPlayerPlaysWhenTricksAreNotOver() {

        goToPlayStep();

        when(tricks.tricksAreOver()).thenReturn(false);

        deal.playerPlays(player1, ClassicalCard.JACK_DIAMOND);

        assertFalse(deal.isOver());

    }

    private void goToPlayStep() {
        ContreeBid bid = new ContreeBid(player1, ContreeBidValue.HUNDRED, CardSuit.DIAMONDS);
        when(bids.bidsAreOver()).thenReturn(true);
        when(bids.highestBid()).thenReturn(Optional.of(bid));// When bids are over, startPlay method is called and gets the trump suit.

        deal.startDeal("TEST", dealPlayers);

        deal.placeBid(new ContreeBid( player1 ));

        assertTrue(deal.isPlayStep());
    }

    @DisplayName("When only bid is 180 HEART, the deal is not doubled nor redoubled")
    @Test
    public void testDealIsNotDoubleNorRedouble() {

        deal.startDeal("TEST", dealPlayers);

        deal.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.HEARTS));

        assertFalse(deal.isDoubleBidExists());
        assertFalse(deal.isRedoubleBidExists());
        assertTrue(deal.isBidStep());
    }

    @DisplayName("getCurrentPlayer behavior is consistent with tricks returned values")
    @Test
    public void testGetCurrentPlayer_isConsistentWithTricks() {

        when(tricks.getCurrentPlayer()).thenReturn(Optional.empty());

        assertTrue(deal.getCurrentPlayer().isEmpty());

        when(tricks.getCurrentPlayer()).thenReturn(Optional.of(player1));

        assertTrue(deal.getCurrentPlayer().isPresent());

        when(tricks.getCurrentPlayer()).thenReturn(Optional.of(player1));

        when(tricks.tricksAreOver()).thenReturn(true);
        assertTrue(deal.getCurrentPlayer().isEmpty());

    }

    @DisplayName("getCurrentBidder behavior is consistent with bids returned values")
    @Test
    public void testGetCurrentBidder_isConsistentWithBids() {

        when(bids.getCurrentBidder()).thenReturn(Optional.empty());

        assertTrue(deal.getCurrentBidder().isEmpty());

        when(bids.getCurrentBidder()).thenReturn(Optional.of(player1));

        assertTrue(deal.getCurrentBidder().isPresent());

    }

}
