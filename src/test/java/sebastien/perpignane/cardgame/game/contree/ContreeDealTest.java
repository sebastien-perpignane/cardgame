package sebastien.perpignane.cardgame.game.contree;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardDealer;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
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
class ContreeDealTest extends TestCasesManagingPlayers {

    private ContreeDealPlayers dealPlayers;

    private ContreeDealBids bids;


    private ContreeTricks tricks;

    private ContreeDeal deal;


    @BeforeAll
    static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {

        ContreeBidPlayers bidPlayers = mock(ContreeBidPlayers.class);
        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        when(bidPlayers.getCurrentBidderSlot()).thenAnswer(AdditionalAnswers.returnsElementsOf(playerSlots));
        when(trickPlayers.getCurrentPlayerSlot()).thenAnswer(AdditionalAnswers.returnsElementsOf(playerSlots));

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
    void testStartDealFailsWhenNoPlayers() {
        when(dealPlayers.getNumberOfPlayers()).thenReturn(0);
        when(dealPlayers.getCurrentDealPlayers()).thenReturn(Collections.emptyList());

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
                () -> deal.startDeal(-1,"TEST", dealPlayers)
        );

    }

    @DisplayName("When a deal is started with a number of players not allowing fair card distribution, starting the deal fails")
    @Test
    void testStartDealFailsWhenBadNumberOfPlayers() {
        when(dealPlayers.getNumberOfPlayers()).thenReturn(3);
        when(dealPlayers.getCurrentDealPlayers()).thenReturn(List.of(player1, player2, player3));

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
                () -> deal.startDeal(-1,"TEST", dealPlayers)
        );

    }

    @DisplayName("After deal.startDeal(), the deal is in bid step")
    @Test
    void testDealIsInBidStepAfterStart() {
        deal.startDeal(-1,"TEST", dealPlayers);

        assertThat( deal.isBidStep() ).isTrue();
        assertThat( deal.isPlayStep() ).isFalse();
    }

    @DisplayName("When bids are over, the deal is in play step. Trump suit is defined.")
    @Test
    void testDealIsInPlayStepWhenBidsAreOver() {
        deal.startDeal(-1,"TEST", dealPlayers);
        ContreeBid bid = new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS);
        when(bids.bidsAreOver()).thenReturn(true);
        when(bids.highestBid()).thenReturn(Optional.of(bid));

        deal.placeBid(bid);

        assertThat( deal.isBidStep() ).isFalse();
        assertThat( deal.isPlayStep() ).isTrue();
        assertThat( deal.getTrumpSuit() ).isNotNull();

    }


    @DisplayName("Players cannot play a card in BID step ")
    @Test
    void testExceptionWhenPlayingWhileBidStep() {
        deal.startDeal(-1,"TEST", dealPlayers);

        assertThat( deal.isBidStep() ).isTrue();

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
            () -> deal.playerPlays(player1, ClassicalCard.JACK_DIAMOND)
        );

    }

    @DisplayName("Players cannot place a bid in PLAY step")
    @Test
    void testExceptionWhenPlacingBidDuringPlayStep() {

        // Given

        deal.startDeal(-1,"TEST", dealPlayers);

        ContreeBid bid = new ContreeBid(player1, ContreeBidValue.HUNDRED, CardSuit.DIAMONDS);
        when(bids.bidsAreOver()).thenReturn(true);
        when(bids.highestBid()).thenReturn(Optional.of(bid));// When bids are over, startPlay method is called and gets the trump suit.
        deal.placeBid(new ContreeBid( player1 ));

        // When
        ThrowableAssert.ThrowingCallable placeBidAction = () -> deal.placeBid(new ContreeBid( player2 ));

        // Then
        assertThat(deal.isPlayStep()).isTrue();
        var ise = catchThrowableOfType(placeBidAction, RuntimeException.class);
        assertThat(ise.getMessage()).contains("A bid cannot be placed during PLAY step");
    }

    @DisplayName("One play step reached, deal is over when tricks are over")
    @Test
    void testPlayerPlaysWhenTricksAreOver() {

        goToPlayStep();

        when(tricks.tricksAreOver()).thenReturn(true);

        deal.playerPlays(player1, ClassicalCard.JACK_DIAMOND);

        assertThat(deal.isOver()).isTrue();

    }

    @DisplayName("One play step reached, deal is not over when tricks are not over")
    @Test
    void testPlayerPlaysWhenTricksAreNotOver() {

        goToPlayStep();

        when(tricks.tricksAreOver()).thenReturn(false);

        deal.playerPlays(player1, ClassicalCard.JACK_DIAMOND);

        assertThat( deal.isOver() ).isFalse();

    }

    private void goToPlayStep() {
        ContreeBid bid = new ContreeBid(player1, ContreeBidValue.HUNDRED, CardSuit.DIAMONDS);
        when(bids.bidsAreOver()).thenReturn(true);
        when(bids.highestBid()).thenReturn(Optional.of(bid));// When bids are over, startPlay method is called and gets the trump suit.

        deal.startDeal(-1,"TEST", dealPlayers);
        deal.placeBid(new ContreeBid( player1 ));

        assertThat( deal.isPlayStep() ).isTrue();
    }

    @DisplayName("When only bid is 180 HEART, the deal is not doubled nor redoubled")
    @Test
    void testDealIsNotDoubleNorRedouble() {

        deal.startDeal(-1,"TEST", dealPlayers);

        deal.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.HEARTS));

        assertThat( deal.isDoubleBidExists() ).isFalse();
        assertThat( deal.isRedoubleBidExists() ).isFalse();
        assertThat( deal.isBidStep() ).isTrue();
    }

    @DisplayName("getCurrentPlayer behavior is consistent with values returned by tricks")
    @Test
    void testGetCurrentPlayer_isConsistentWithTricks() {

        when(tricks.getCurrentPlayer()).thenReturn(Optional.empty());

        assertThat( deal.getCurrentPlayer() ).isEmpty();

        when(tricks.getCurrentPlayer()).thenReturn(Optional.of(player1));

        assertThat( deal.getCurrentPlayer() ).isPresent();

        when(tricks.getCurrentPlayer()).thenReturn(Optional.of(player1));
        when(tricks.tricksAreOver()).thenReturn(true);

        assertThat( deal.getCurrentPlayer() ).isEmpty();

    }

    @DisplayName("getCurrentBidder behavior is consistent with values returned by bids")
    @Test
    void testGetCurrentBidder_isConsistentWithBids() {

        when(bids.getCurrentBidder()).thenReturn(Optional.empty());

        assertThat( deal.getCurrentBidder() ).isEmpty();

        when(bids.getCurrentBidder()).thenReturn(Optional.of(player1));

        assertThat( deal.getCurrentBidder() ).isPresent();

    }

}
