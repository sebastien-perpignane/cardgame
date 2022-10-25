package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContreeDealBidsTest extends TestCasesManagingPlayers {

    private ContreeBidPlayers bidPlayers;

    private ContreeDealBids dealBids;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();

    }

    @BeforeEach
    public void setup() {
        bidPlayers = mock(ContreeBidPlayers.class);
        dealBids = new ContreeDealBids(bidPlayers);

        configureBidPlayersForNbBidTurns(1);
    }

    private void configureBidPlayersForNbBidTurns(int nbTurns) {
        List<ContreePlayer> multipliedPlayers = loopingPlayers(nbTurns);
        when(bidPlayers.getCurrentBidder()).thenAnswer(AdditionalAnswers.returnsElementsOf(multipliedPlayers));
    }

    @DisplayName("State of the bids is as expected just after bids are started")
    @Test
    void testStartBids() {

        dealBids.startBids();

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

        dealBids.startBids();

        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player4, ContreeBidValue.NONE));

        assertTrue(dealBids.hasOnlyNoneBids());
        assertTrue(dealBids.bidsAreOver());


    }

    @DisplayName("After first bid, highest bid state is available")
    @Test
    void testBidsAfterFirstBid() {

        dealBids.startBids();
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

        dealBids.startBids();
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

        dealBids.startBids();
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

        configureBidPlayersForNbBidTurns(2);

        dealBids.startBids();
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

    @DisplayName("If valid redouble, bidding is over")
    @Test
    void testRedoubleEndsBids() {

        dealBids.startBids();
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
        dealBids.startBids();
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.NONE));
        dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.CAPOT, CardSuit.DIAMONDS));

        assertTrue(dealBids.isAnnouncedCapot());
    }

    @DisplayName("A player cannot double if only NONE bids before")
    @Test
    public void testDoubleIfNoValuedBidMustFail() {
        dealBids.startBids();

        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.NONE));
        assertThrows(
                RuntimeException.class,
                () -> dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.DOUBLE))
        );

    }

    @DisplayName("First bid cannot be DOUBLE")
    @Test
    public void testFirstBidIsDoubleFails() {
        dealBids.startBids();

        assertThrows(
                RuntimeException.class,
                () -> dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.DOUBLE))
        );

    }

    @DisplayName("If same player bids 2 times, exception")
    @Test
    void testBidFromUnexpectedPlayer_samePlayerBids2times() {

        dealBids.startBids();
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        assertThrows(
                RuntimeException.class,
                () -> dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.NONE))
        );

    }

    @DisplayName("Same player bids 2 times must trigger exception")
    @Test
    void testBidFromUnexpectedPlayer_3rdPlayerPlacesSecondBid() {

        dealBids.startBids();
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        assertThrows(
                RuntimeException.class,
                () -> dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.NONE))
        );

    }

    @DisplayName("A player cannot double against his team mate")
    @Test
    void testDoubleAgainstTeamMate() {

        dealBids.startBids();
        dealBids.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        dealBids.placeBid(new ContreeBid(player2, ContreeBidValue.NONE));
        assertThrows(
                RuntimeException.class,
                () -> dealBids.placeBid(new ContreeBid(player3, ContreeBidValue.DOUBLE))
        );

    }

    @DisplayName("Exception if a player tries to double before any valued bid")
    @Test
    public void testExceptionWhenDoubleBeforeAnyValuedBid() {
        dealBids.startBids();
        dealBids.placeBid(new ContreeBid(players.get(0), ContreeBidValue.NONE, null));

        assertThrows(
                IllegalStateException.class,
                () -> dealBids.placeBid(new ContreeBid(players.get(1), ContreeBidValue.DOUBLE, null))
        );

    }

    @DisplayName("Exception if a player tries to double before any bid")
    @Test
    public void testExceptionWhenDoubleBeforeAnyBid() {
        dealBids.startBids();

        assertThrows(
                IllegalStateException.class,
                () -> dealBids.placeBid(new ContreeBid(players.get(0), ContreeBidValue.DOUBLE, null))
        );

    }

    @DisplayName("Exception if a player tries to redouble before a double")
    @Test
    public void testExceptionWhenRedoubleBeforeDouble() {
        dealBids.startBids();
        dealBids.placeBid(new ContreeBid(players.get(0), ContreeBidValue.EIGHTY, CardSuit.HEARTS));
        dealBids.placeBid(new ContreeBid(players.get(1), ContreeBidValue.NINETY, CardSuit.SPADES));

        assertThrows(
                IllegalStateException.class,
                () -> dealBids.placeBid(new ContreeBid(players.get(2), ContreeBidValue.REDOUBLE, null))
        );

    }

    @DisplayName("Exception if a player tries to redouble before any bid")
    @Test
    public void testExceptionWhenRedoubleBeforeAnyBid() {
        dealBids.startBids();
        dealBids.placeBid(new ContreeBid(players.get(0), ContreeBidValue.EIGHTY, CardSuit.HEARTS));
        dealBids.placeBid(new ContreeBid(players.get(1), ContreeBidValue.NINETY, CardSuit.SPADES));

        assertThrows(
                IllegalStateException.class,
                () -> dealBids.placeBid(new ContreeBid(players.get(2), ContreeBidValue.REDOUBLE, null))
        );

    }


    @DisplayName("A player cannot redouble if double was announced by his team mate")
    @Test
    public void testRedoubleAgainstTeamMate() {
        dealBids.startBids();

        dealBids.placeBid(new ContreeBid(players.get(0), ContreeBidValue.HUNDRED_FORTY, CardSuit.HEARTS));
        dealBids.placeBid(new ContreeBid(players.get(1), ContreeBidValue.DOUBLE));
        dealBids.placeBid(new ContreeBid(players.get(2), ContreeBidValue.NONE));
        assertThrows(
                IllegalStateException.class,
                () -> dealBids.placeBid(new ContreeBid(players.get(3), ContreeBidValue.REDOUBLE))
        );

    }

}
