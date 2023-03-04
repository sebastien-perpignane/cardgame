package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.util.PlayerSlot;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static sebastien.perpignane.cardgame.game.contree.ContreeDealBidsAssert.assertThat;

class ContreeDealBidsTest extends TestCasesManagingPlayers {

    private BiddableValuesFilter biddableValuesFilter;

    private ContreeDealBids dealBids;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    public void setUp() {
        biddableValuesFilter = mock(BiddableValuesFilter.class);
        when(biddableValuesFilter.biddableValues(any(), any())).thenReturn(new BiddableValuesFilter.BidFilterResult());
        ContreeBidPlayers bidPlayers = mock(ContreeBidPlayers.class);
        dealBids = new ContreeDealBids(biddableValuesFilter);

        List<PlayerSlot<ContreePlayer>> multipliedPlayerSlots = loopingPlayerSlots(5);

        when(bidPlayers.getCurrentBidderSlot()).thenAnswer(AdditionalAnswers.returnsElementsOf(multipliedPlayerSlots));
        dealBids.startBids(bidPlayers);
    }

    @DisplayName("State of dealBids is as expected just after bids are started : not over, highestBid not available, ne special bid")
    @Test
    void testBidsJustAfterStart() {

        assertThat(dealBids.bidsAreOver()).isFalse();
        assertThat(dealBids.highestBid()).isEmpty();
        assertThat(dealBids.findDealContractBid()).isEmpty();
        assertThat(dealBids.isDoubleBidExists()).isFalse();
        assertThat(dealBids.isRedoubleBidExists()).isFalse();
        assertThat(dealBids.isAnnouncedCapot()).isFalse();
        assertThat(dealBids.hasOnlyPassBids()).isFalse();

    }

    @DisplayName("4 PASS bids, bids are over")
    @Test
    void testOnlyPassBids() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player4, ContreeBidValue.PASS));

        assertThat(dealBids.hasOnlyPassBids()).isTrue();
        assertThat(dealBids.bidsAreOver()).isTrue();
    }

    @DisplayName("Exception when placing a bid after bids are over")
    @Test
    void testExceptionWhenPlacingBidAfterBidsAreOver() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player4, ContreeBidValue.PASS));

        assertThat(dealBids).bidsAreOver();

        var exception = catchThrowableOfType(
            () -> dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.PASS)),
            ContreeDealBids.BidNotAllowedException.class
        );
        assertThat( exception ).isNotNull();

        assertThat(
            exception.isSuspectedCheat()
        ).isTrue();

    }

    @DisplayName("After first bid, highest bid data is available, the state of dealBids is consistent")
    @Test
    void testBidsAfterFirstBid() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));

        assertThat(
            dealBids.highestBid()
        ).isPresent();

        assertThat(
            dealBids
        ).hasHighestBidValueAs(ContreeBidValue.EIGHTY);

        assertThat(
            dealBids
        ).hasHighestBidSuitAs(CardSuit.DIAMONDS);

        assertThat(
            dealBids
        ).bidsAreNotOver();

        assertThat(
            dealBids
        ).hasNoDealContractBidFound();

        assertThat(
            dealBids.isDoubleBidExists()
        ).isFalse();

        assertThat(
            dealBids.isRedoubleBidExists()
        ).isFalse();

        assertThat(
            dealBids.isAnnouncedCapot()
        ).isFalse();

    }

    @DisplayName("After a first valued bid and 3 PASS bids, highest bid data is available, bids are over and contract bid is available")
    @Test
    void testBidsAfterFirstValuedBidAnd3PassBids() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player4, ContreeBidValue.PASS));

        assertThat(
            dealBids.highestBid()
        ).isPresent();

        assertThat(
            dealBids.highestBid().get().bidValue()
        ).isSameAs(ContreeBidValue.EIGHTY);

        assertThat(
            dealBids.highestBid().get().cardSuit()
        ).isSameAs(CardSuit.DIAMONDS);

        assertThat(
            dealBids.bidsAreOver()
        ).isTrue();

        assertThat(
            dealBids
        ).hasDealContractBidFound();

        assertThat(
            dealBids.findDealContractBid().get().bidValue()
        ).isSameAs(ContreeBidValue.EIGHTY);

        assertThat(
            dealBids.findDealContractBid().get().cardSuit()
        ).isSameAs(CardSuit.DIAMONDS);

        assertThat(
            dealBids.isDoubleBidExists()
        ).isFalse();

        assertThat(
            dealBids.isRedoubleBidExists()
        ).isFalse();

        assertThat(
            dealBids.isAnnouncedCapot()
        ).isFalse();

    }

    @DisplayName("After multiple valued bids but only 4 bids, highest bid state is available, bids are NOT over and contract bid is NOT available")
    @Test
    void testBidsAfterMultipleValuedBids_partialBids() {

        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.NINETY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player4, ContreeBidValue.PASS));

        assertThat(dealBids)
                .hasHighestBidValueAs(ContreeBidValue.NINETY)
                .hasHighestBidSuitAs(CardSuit.DIAMONDS)
                .bidsAreNotOver()
                .hasNoDealContractBidFound()
                .doubleBidDoesNotExist()
                .redoubleBidDoesNotExist()
                .hasNotAnnouncedCapot();

    }

    @DisplayName("After multiple valued bids followed by expected nbr of PASS bids, highest bid data is available, bids are over and contract bid is available")
    @Test
    void testBidsAfterMultipleValuedBids_completeBids() {

        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.NINETY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player4, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.PASS));

        assertThat(dealBids)
                .hasHighestBidValueAs(ContreeBidValue.NINETY)
                .hasHighestBidSuitAs(CardSuit.DIAMONDS)
                .bidsAreOver()
                .hasDealContractBidFound()
                .hasContractBidValueAs(ContreeBidValue.NINETY)
                .hasContractBidSuitAs(CardSuit.DIAMONDS)
                .doubleBidDoesNotExist()
                .redoubleBidDoesNotExist()
                .hasNotAnnouncedCapot();

    }

    @DisplayName("First bid is capot, next player double, bidding step is over after three next players passed")
    @Test
    public void testFirstBidCapotThenDoubleAndPasses() {

        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.CAPOT, CardSuit.HEARTS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.DOUBLE));

        assertThat(
            dealBids.bidsAreOver()
        ).isFalse();

        dealBids.placeBid(new ContreeBid(player3));
        dealBids.placeBid(new ContreeBid(player4));
        dealBids.placeBid(new ContreeBid(player1));

        assertThat(
            dealBids.bidsAreOver()
        ).isTrue();

    }

    @DisplayName("If valid redouble, bidding is over")
    @Test
    void testRedoubleEndsBids() {

        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.DOUBLE));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.REDOUBLE));

        assertThat(dealBids.highestBid()).isPresent();
        assertThat(ContreeBidValue.EIGHTY).isSameAs(dealBids.highestBid().get().bidValue());
        assertThat(CardSuit.DIAMONDS).isSameAs(dealBids.highestBid().get().cardSuit());
        assertThat(dealBids.bidsAreOver()).isTrue();
        assertThat(dealBids.findDealContractBid()).isPresent();
        assertThat(ContreeBidValue.EIGHTY).isSameAs(dealBids.findDealContractBid().get().bidValue());
        assertThat(CardSuit.DIAMONDS).isSameAs(dealBids.findDealContractBid().get().cardSuit());
        assertThat(dealBids.isDoubleBidExists()).isTrue();
        assertThat(dealBids.isRedoubleBidExists()).isTrue();

        assertThat(dealBids.isAnnouncedCapot()).isFalse();


    }

    @DisplayName("Announced capot is true when a player bids 'capot' ")
    @Test
    public void testAnnouncedCapot() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.PASS));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.CAPOT, CardSuit.DIAMONDS));

        assertThat(dealBids).hasAnnouncedCapot();

    }

    @DisplayName("Exception if current bid value is present in the exclusion cause map returned by the biddable values filter")
    @Test
    public void testNoOverBidWhenExpected() {
        dealBids.placeBid(new ContreeBid( player1, ContreeBidValue.HUNDRED, CardSuit.DIAMONDS ));


        BiddableValuesFilter.BidFilterResult bidFilterResult = mock(BiddableValuesFilter.BidFilterResult.class);
        when(bidFilterResult.biddableValues()).thenReturn(Collections.emptySet());
        when(bidFilterResult.exclusionCauseByBidValue()).thenReturn(Map.of(ContreeBidValue.NINETY, "Why do you bid shit, player 2 ????"));

        when(biddableValuesFilter.biddableValues(any(), any())).thenReturn(bidFilterResult);

        dealBids.placeBid(new ContreeBid( player2 ));

        var e = assertThrows(
            ContreeDealBids.BidNotAllowedException.class,
            () -> dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.NINETY, CardSuit.DIAMONDS))
        );

        assertThat(e.isSuspectedCheat()).isFalse();

    }

    @DisplayName("When a player other than the current bidder places a bid, an exception is thrown")
    @Test
    void testBidFromUnexpectedPlayer_3rdPlayerPlacesSecondBid() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        var e = assertThrows(
            ContreeDealBids.BidNotAllowedException.class,
            () -> dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.PASS))
        );

        assertThat(e.isSuspectedCheat()).isTrue();

    }

    @DisplayName("if no double or redouble bid is placed correctly, deal wont be considered doubled nor redoubled")
    @Test
    public void testDealIsNotDoubleNorRedouble() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.HEARTS));

        assertThat(dealBids.isDoubleBidExists()).isFalse();
        assertThat(dealBids.isRedoubleBidExists()).isFalse();
    }

    @DisplayName("When bids are in progress, there is a current bidder")
    @Test
    void testGetCurrentBidder_bidsInProgress() {

        dealBids.placeBid(new ContreeBid(player1));

        assertThat(dealBids).hasCurrentBidderAs(player2);

    }

    @DisplayName("When bids are over, there is no current bidder")
    @Test
    void testGetCurrentBidder_bidsAreOver() {

        dealBids.placeBid(new ContreeBid(player1));
        dealBids.placeBid(new ContreeBid(player2));
        dealBids.placeBid(new ContreeBid(player3));
        dealBids.placeBid(new ContreeBid(player4));

        assertThat(dealBids).bidsAreOver();
        assertThat(dealBids.getCurrentBidder()).isEmpty();

    }

}
