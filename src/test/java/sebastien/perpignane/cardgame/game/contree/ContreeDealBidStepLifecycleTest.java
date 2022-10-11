package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.buildPlayers;

import static org.junit.jupiter.api.Assertions.*;

public class ContreeDealBidStepLifecycleTest {

    @DisplayName("First bid, none, deal step is still BID")
    @Test
    public void testFirstBid_None() {

        var players = buildPlayers();

        ContreePlayer biddingPlayer = players.get(0);

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();

        var noneBid = new ContreeBid(biddingPlayer);
        deal.placeBid(noneBid);

        assertTrue(deal.isBidStep());

    }

    @DisplayName("First bid, eighty")
    @Test
    public void testFirstBid_EIGHTY() {

        var players = buildPlayers();

        ContreePlayer biddingPlayer = players.get(0);

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();

        var noneBid = new ContreeBid(biddingPlayer, ContreeBidValue.EIGHTY, CardSuit.DIAMONDS);
        deal.placeBid(noneBid);

        assertTrue(deal.isBidStep());

    }

    @DisplayName("First bid is capot, next player double, bidding step is over after three next passed")
    @Test
    public void testFirstBid_CAPOT() {

        var players = buildPlayers();

        var player1 = players.get(0);
        var player2 = players.get(1);
        var player3 = players.get(2);
        var player4 = players.get(3);

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();
        deal.placeBid(new ContreeBid(player1, ContreeBidValue.CAPOT, CardSuit.HEARTS));
        deal.placeBid(new ContreeBid(player2, ContreeBidValue.DOUBLE, null));
        deal.placeBid(new ContreeBid(player3));
        deal.placeBid(new ContreeBid(player4));
        deal.placeBid(new ContreeBid(player1));
        assertSame(deal.highestBid().orElseThrow().cardSuit(), CardSuit.HEARTS);

        assertTrue(deal.isPlayStep());

    }

    @DisplayName("First bid, no overbid, deal is ready to be played")
    @Test
    public void testNoOverBid_readyToPlay() {

        var players = buildPlayers();

        ContreePlayer biddingPlayer = players.get(0);

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();

        var bid = new ContreeBid(biddingPlayer, ContreeBidValue.HUNDRED, CardSuit.DIAMONDS);
        deal.placeBid(bid);
        deal.placeBid(new ContreeBid(players.get(1)));
        deal.placeBid(new ContreeBid(players.get(2)));
        deal.placeBid(new ContreeBid(players.get(3)));

        assertTrue(deal.isPlayStep());

    }



    @DisplayName("Deal is over when all players bid NONE")
    @Test
    public void testFourNoneBids_endOfDeal() {
        var players = buildPlayers();

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();

        players.forEach(p -> deal.placeBid(new ContreeBid(p)));

        assertTrue(deal.isOver());

    }

    @DisplayName("When bidding step is over, placing a new bid throws an exception")
    @Test
    public void testFourNoneBids_FifthBidThrowsException() {
        var players = buildPlayers();

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();

        players.forEach(p -> deal.placeBid(new ContreeBid(p)));

        assertThrows(
                IllegalStateException.class,
                () -> deal.placeBid(new ContreeBid(players.get(0)))
        );

    }

    @DisplayName("Bidding step does not end at 4 bids if multiple valued bids are placed")
    @Test
    public void testMultipleValuedBids_noPrematuredEndOfBid() {

        var players = buildPlayers();

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();

        deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.EIGHTY, CardSuit.DIAMONDS));
        deal.placeBid(new ContreeBid(players.get(1)));
        deal.placeBid(new ContreeBid(players.get(2), ContreeBidValue.NINETY, CardSuit.DIAMONDS));
        deal.placeBid(new ContreeBid(players.get(3)));

        assertTrue(deal.isBidStep());

    }

    @DisplayName("Exception if current bid does not overbid the last valued bid")
    @Test
    public void testNoOverBidWhenExpected() {

        var players = buildPlayers();

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();

        deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.HUNDRED, CardSuit.DIAMONDS));
        deal.placeBid(new ContreeBid(players.get(1)));

        assertThrows(
                IllegalArgumentException.class,
                () -> deal.placeBid(new ContreeBid(players.get(2), ContreeBidValue.NINETY, CardSuit.DIAMONDS))
        );

        assertTrue(deal.isBidStep());

    }

    @DisplayName("if no double or redouble bid is placed correctly, game is not double not redouble")
    @Test
    public void testDealIsNotDoubleNorRedouble() {

        var players = buildPlayers();

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();

        deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.EIGHTY, CardSuit.HEARTS));

        assertFalse(deal.isDoubleBidExists());
        assertFalse(deal.isRedoubleBidExists());
        assertTrue(deal.isBidStep());

    }

    @DisplayName("deal 'is double' if a redouble bid is placed correctly")
    @Test
    public void testDealIsDouble() {

        var players = buildPlayers();

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();

        deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.EIGHTY, CardSuit.HEARTS));
        deal.placeBid(new ContreeBid(players.get(1), ContreeBidValue.DOUBLE, null));

        assertTrue(deal.isDoubleBidExists());
        assertTrue(deal.isBidStep());

    }

    @DisplayName("deal 'is redouble' if a redouble bid is placed correctly")
    @Test
    public void testDealIsRedouble() {

        var players = buildPlayers();

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();

        deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.EIGHTY, CardSuit.HEARTS));
        deal.placeBid(new ContreeBid(players.get(1), ContreeBidValue.DOUBLE));
        deal.placeBid(new ContreeBid(players.get(2), ContreeBidValue.REDOUBLE));

        assertTrue(deal.isRedoubleBidExists());
        assertTrue(deal.isPlayStep());

    }

    @DisplayName("Exception if a player tries to double before any valued bid")
    @Test
    public void testExceptionWhenDoubleBeforeAnyValuedBid() {

        var players = buildPlayers();

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();
        deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.NONE, null));

        assertThrows(
            IllegalStateException.class,
            () -> deal.placeBid(new ContreeBid(players.get(1), ContreeBidValue.DOUBLE, null))
        );

    }

    @DisplayName("Exception if a player tries to double before any bid")
    @Test
    public void testExceptionWhenDoubleBeforeAnyBid() {

        var players = buildPlayers();

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();

        assertThrows(
            IllegalStateException.class,
            () -> deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.DOUBLE, null))
        );

    }

    @DisplayName("Exception if a player tries to redouble before a double")
    @Test
    public void testExceptionWhenRedoubleBeforeDouble() {

        var players = buildPlayers();

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();
        deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.EIGHTY, CardSuit.HEARTS));
        deal.placeBid(new ContreeBid(players.get(1), ContreeBidValue.NINETY, CardSuit.SPADES));

        assertThrows(
                IllegalStateException.class,
                () -> deal.placeBid(new ContreeBid(players.get(2), ContreeBidValue.REDOUBLE, null))
        );

    }

    @DisplayName("Exception if a player tries to redouble before any bid")
    @Test
    public void testExceptionWhenRedoubleBeforeAnyBid() {

        var players = buildPlayers();

        ContreeDeal deal = new ContreeDeal("TEST", players, new ContreeGameEventSender());
        deal.startDeal();
        deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.EIGHTY, CardSuit.HEARTS));
        deal.placeBid(new ContreeBid(players.get(1), ContreeBidValue.NINETY, CardSuit.SPADES));

        assertThrows(
                IllegalStateException.class,
                () -> deal.placeBid(new ContreeBid(players.get(2), ContreeBidValue.REDOUBLE, null))
        );

    }


    @DisplayName("A player cannot redouble if double was announced by his team mate")
    @Test
    public void testRedoubleAgainstTeamMate() {

        var players = buildPlayers();
        var deal = new ContreeDeal("test", players, new ContreeGameEventSender());
        deal.startDeal();

        deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.HUNDRED_FOURTY, CardSuit.HEARTS));
        deal.placeBid(new ContreeBid(players.get(1), ContreeBidValue.DOUBLE));
        deal.placeBid(new ContreeBid(players.get(2), ContreeBidValue.NONE));
        assertThrows(
                IllegalStateException.class,
                () -> deal.placeBid(new ContreeBid(players.get(3), ContreeBidValue.REDOUBLE))
        );

    }

}
