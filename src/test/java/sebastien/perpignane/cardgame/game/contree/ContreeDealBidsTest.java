package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        when(biddableValuesFilter.biddableValues(any(), any())).thenReturn(new BiddableValuesFilter.BidFilterResult(Collections.emptySet(), Collections.emptyMap()));
        ContreeBidPlayers bidPlayers = mock(ContreeBidPlayers.class);
        dealBids = new ContreeDealBids(biddableValuesFilter);
        List<ContreePlayer> multipliedPlayers = loopingPlayers(5);
        when(bidPlayers.getCurrentBidder()).thenAnswer(AdditionalAnswers.returnsElementsOf(multipliedPlayers));
        dealBids.startBids(bidPlayers);


    }

    @DisplayName("State of dealBids is as expected just after bids are started : not over, highestBid not available, ne special bid")
    @Test
    void testBidsJustAfterStart() {

        assertFalse(dealBids.bidsAreOver());
        assertTrue(dealBids.highestBid().isEmpty());
        assertTrue(dealBids.findDealContractBid().isEmpty());
        assertFalse(dealBids.isDoubleBidExists());
        assertFalse(dealBids.isRedoubleBidExists());
        assertFalse(dealBids.isAnnouncedCapot());
        assertFalse(dealBids.hasOnlyNoneBids());

    }

    @DisplayName("Only NONE bids, bids are over")
    @Test
    void testOnlyNoneBids() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player4, ContreeBidValue.NONE));

        assertTrue(dealBids.hasOnlyNoneBids());
        assertTrue(dealBids.bidsAreOver());
    }

    @DisplayName("Exception when placing a bid after  bids are over")
    @Test
    void testExceptionWhenPlacingBidAfterBidsAreOver() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player4, ContreeBidValue.NONE));

        assertTrue(dealBids.bidsAreOver());

        var e = assertThrows(
            ContreeDealBids.BidNotAllowedException.class,
            () -> dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.NONE))
        );
        assertTrue(e.isSuspectedCheat());
    }

    @DisplayName("After first bid, highest bid state is available")
    @Test
    void testBidsAfterFirstBid() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));

        assertTrue(dealBids.highestBid().isPresent());
        assertSame(dealBids.highestBid().get().bidValue(), ContreeBidValue.EIGHTY);
        assertSame(dealBids.highestBid().get().cardSuit(), CardSuit.DIAMONDS);

        assertFalse(dealBids.bidsAreOver());
        assertTrue(dealBids.findDealContractBid().isEmpty());
        assertFalse(dealBids.isDoubleBidExists());
        assertFalse(dealBids.isRedoubleBidExists());
        assertFalse(dealBids.isAnnouncedCapot());

    }

    @DisplayName("After a first valued bid and 3 none bids, highest bid state is available, bids are over and contract bid is available")
    @Test
    void testBidsAfterFirstValuedBidAnd3NoneBids() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player4, ContreeBidValue.NONE));

        assertTrue(dealBids.highestBid().isPresent());
        assertSame(dealBids.highestBid().get().bidValue(), ContreeBidValue.EIGHTY);
        assertSame(dealBids.highestBid().get().cardSuit(), CardSuit.DIAMONDS);
        assertTrue(dealBids.bidsAreOver());
        assertTrue(dealBids.findDealContractBid().isPresent());
        assertSame(dealBids.findDealContractBid().get().bidValue(), ContreeBidValue.EIGHTY);
        assertSame(dealBids.findDealContractBid().get().cardSuit(), CardSuit.DIAMONDS);


        assertFalse(dealBids.isDoubleBidExists());
        assertFalse(dealBids.isRedoubleBidExists());
        assertFalse(dealBids.isAnnouncedCapot());

    }

    @DisplayName("After multiple valued bids but only 4 bids, highest bid state is available, bids are NOT over and contract bid is NOT available")
    @Test
    void testBidsAfterMultipleValuedBids_partialBids() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.NINETY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player4, ContreeBidValue.NONE));

        assertTrue(dealBids.highestBid().isPresent());
        assertSame(dealBids.highestBid().get().bidValue(), ContreeBidValue.NINETY);
        assertSame(dealBids.highestBid().get().cardSuit(), CardSuit.DIAMONDS);
        assertFalse(dealBids.bidsAreOver());
        assertTrue(dealBids.findDealContractBid().isEmpty());

        assertFalse(dealBids.isDoubleBidExists());
        assertFalse(dealBids.isRedoubleBidExists());
        assertFalse(dealBids.isAnnouncedCapot());

    }

    @DisplayName("After multiple valued bids followed by expected nbr of NONE bids, highest bid state is available, bids are over and contract bid is available")
    @Test
    void testBidsAfterMultipleValuedBids_completeBids() {

        //configureBidPlayersForNbBidTurns(2);

        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.NINETY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player4, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.NONE));

        assertTrue(dealBids.highestBid().isPresent());
        assertSame(dealBids.highestBid().get().bidValue(), ContreeBidValue.NINETY);
        assertSame(dealBids.highestBid().get().cardSuit(), CardSuit.DIAMONDS);
        assertTrue(dealBids.bidsAreOver());
        assertTrue(dealBids.findDealContractBid().isPresent());
        assertSame(dealBids.findDealContractBid().get().bidValue(), ContreeBidValue.NINETY);
        assertSame(dealBids.findDealContractBid().get().cardSuit(), CardSuit.DIAMONDS);

        assertFalse(dealBids.isDoubleBidExists());
        assertFalse(dealBids.isRedoubleBidExists());
        assertFalse(dealBids.isAnnouncedCapot());

    }

    @DisplayName("First bid is capot, next player double, bidding step is over after three next players passed")
    @Test
    public void testFirstBidCapotThenDoubleAndNones() {

        //configureBidPlayersForNbBidTurns(2);

        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.CAPOT, CardSuit.HEARTS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.DOUBLE));

        assertFalse(dealBids.bidsAreOver());

        dealBids.placeBid(new ContreeBid(player3));
        dealBids.placeBid(new ContreeBid(player4));
        dealBids.placeBid(new ContreeBid(player1));

        assertTrue(dealBids.bidsAreOver());

    }

    @DisplayName("If valid redouble, bidding is over")
    @Test
    void testRedoubleEndsBids() {

        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.DOUBLE));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.REDOUBLE));

        assertTrue(dealBids.highestBid().isPresent());
        assertSame(dealBids.highestBid().get().bidValue(), ContreeBidValue.EIGHTY);
        assertSame(dealBids.highestBid().get().cardSuit(), CardSuit.DIAMONDS);
        assertTrue(dealBids.bidsAreOver());
        assertTrue(dealBids.findDealContractBid().isPresent());
        assertSame(dealBids.findDealContractBid().get().bidValue(), ContreeBidValue.EIGHTY);
        assertSame(dealBids.findDealContractBid().get().cardSuit(), CardSuit.DIAMONDS);
        assertTrue(dealBids.isDoubleBidExists());
        assertTrue(dealBids.isRedoubleBidExists());

        assertFalse(dealBids.isAnnouncedCapot());


    }

    @DisplayName("Announced capot is true when a player bids 'capot' ")
    @Test
    public void testAnnouncedCapot() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.CAPOT, CardSuit.DIAMONDS));

        assertTrue(dealBids.isAnnouncedCapot());
    }

    @DisplayName("Exception if current bid value is present in the exclusion cause map returned by the biddable values filter")
    @Test
    public void testNoOverBidWhenExpected() {
        dealBids.placeBid(new ContreeBid( player1, ContreeBidValue.HUNDRED, CardSuit.DIAMONDS ));

        when(biddableValuesFilter.biddableValues(any(), any())).thenReturn(
                new BiddableValuesFilter.BidFilterResult(
                        Set.of(
                        ),
                        Map.of(ContreeBidValue.NINETY, "Why do you bid shit, player 3 ????")
                )
        );

        dealBids.placeBid(new ContreeBid( player2 ));

        var e = assertThrows(
                ContreeDealBids.BidNotAllowedException.class,
                () -> dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.NINETY, CardSuit.DIAMONDS))
        );

        assertFalse(e.isSuspectedCheat());

    }

    @DisplayName("Same player bids 2 times must trigger exception")
    @Test
    void testBidFromUnexpectedPlayer_3rdPlayerPlacesSecondBid() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        var e = assertThrows(
                ContreeDealBids.BidNotAllowedException.class,
                () -> dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.NONE))
        );

        assertTrue(e.isSuspectedCheat());

    }

    @DisplayName("if no double or redouble bid is placed correctly, deal wont be considered doubled nor redoubled")
    @Test
    public void testDealIsNotDoubleNorRedouble() {
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.HEARTS));

        assertFalse(dealBids.isDoubleBidExists());
        assertFalse(dealBids.isRedoubleBidExists());
    }

}
