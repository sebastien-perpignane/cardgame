package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.buildPlayers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContreeDealTest {

    // TODO Test exception if redouble bid done by same team who doubled

    @DisplayName("Exception if we build a ContreeDeal with less than 4 players")
    @Test
    public void testExceptionIfLessThanFourPlayers() {

        var players = buildPlayers();

        assertThrows(
            IllegalArgumentException.class,
            () -> new ContreeDeal("test", players.subList(0, 3), new ContreeGameEventSender())
        );

    }

    @DisplayName("Exception if we build a ContreeDeal with more than 4 players")
    @Test
    public void testExceptionIfMoreThanFourPlayers() {

        var players = new ArrayList<>(buildPlayers());
        players.add(mock(ContreePlayer.class));

        assertThrows(
                IllegalArgumentException.class,
                () -> new ContreeDeal("test", players, new ContreeGameEventSender())
        );

    }


    @DisplayName("Players cannot play a card in BID step ")
    @Test
    public void testExceptionWhenPlayingWhileBidStep() {
        var players = buildPlayers();
        var deal = new ContreeDeal("test", players, new ContreeGameEventSender());
        deal.startDeal();

        assertSame(DealStep.BID, deal.getDealStep());

        assertThrows(
                IllegalStateException.class,
                () -> deal.playerPlays(players.get(0), ClassicalCard.JACK_DIAMOND)
        );
    }

    @DisplayName("Players cannot place a bid in PLAY step")
    @Test
    public void testExceptionWhenPlacingBidDuringPlayStep() {
        var players = buildPlayers();
        var deal = new ContreeDeal("test", players, new ContreeGameEventSender());
        deal.startDeal();

        placeBidsWithPlayer1BiddingForHeartAndOthersPass(deal, players);

        assertSame(DealStep.PLAY, deal.getDealStep());

        assertThrows(
                IllegalStateException.class,
                () -> deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.NONE, null))
        );
    }

    @DisplayName("Only current player can play a card")
    @Test
    public void testExceptionWhenNotCurrentPlayerPlays() {
        var players = buildPlayers();
        var deal = new ContreeDeal("test", players, new ContreeGameEventSender());
        deal.startDeal();

        placeBidsWithPlayer1BiddingForHeartAndOthersPass(deal, players);

        assertSame(DealStep.PLAY, deal.getDealStep());

        assertThrows(
            IllegalArgumentException.class,
                () -> deal.playerPlays(players.get(1), ClassicalCard.JACK_DIAMOND)
        );
    }

    private void placeBidsWithPlayer1BiddingForHeartAndOthersPass(ContreeDeal deal, List<ContreePlayer> players) {
        deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.EIGHTY, CardSuit.HEARTS));
        deal.placeBid(new ContreeBid(players.get(1), ContreeBidValue.NONE, null));
        deal.placeBid(new ContreeBid(players.get(2), ContreeBidValue.NONE, null));
        deal.placeBid(new ContreeBid(players.get(3), ContreeBidValue.NONE, null));
    }

    @Test
    public void testTeamDoingCapotWhenCapotAchieved() {

        var hand1 = new LinkedList<>(List.of(
                ClassicalCard.JACK_HEART,
                ClassicalCard.NINE_HEART,
                ClassicalCard.ACE_HEART,
                ClassicalCard.TEN_HEART,
                ClassicalCard.KING_HEART,
                ClassicalCard.QUEEN_HEART,
                ClassicalCard.EIGHT_HEART,
                ClassicalCard.SEVEN_HEART
        ));
        var hand2 = new LinkedList<>(List.of(
                ClassicalCard.JACK_SPADE,
                ClassicalCard.NINE_SPADE,
                ClassicalCard.ACE_SPADE,
                ClassicalCard.TEN_SPADE,
                ClassicalCard.KING_SPADE,
                ClassicalCard.QUEEN_SPADE,
                ClassicalCard.EIGHT_SPADE,
                ClassicalCard.SEVEN_SPADE
        ));
        var hand3 = new LinkedList<>(List.of(
                ClassicalCard.JACK_DIAMOND,
                ClassicalCard.NINE_DIAMOND,
                ClassicalCard.ACE_DIAMOND,
                ClassicalCard.TEN_DIAMOND,
                ClassicalCard.KING_DIAMOND,
                ClassicalCard.QUEEN_DIAMOND,
                ClassicalCard.EIGHT_DIAMOND,
                ClassicalCard.SEVEN_DIAMOND
        ));
        var hand4 = new LinkedList<>(List.of(
                ClassicalCard.JACK_CLUB,
                ClassicalCard.NINE_CLUB,
                ClassicalCard.ACE_CLUB,
                ClassicalCard.TEN_CLUB,
                ClassicalCard.KING_CLUB,
                ClassicalCard.QUEEN_CLUB,
                ClassicalCard.EIGHT_CLUB,
                ClassicalCard.SEVEN_CLUB
        ));

        var players = buildPlayersWithHand(
            hand1,
            hand2,
            hand3,
            hand4
        );

        var deal = new ContreeDeal("test", players, new ContreeGameEventSender());
        deal.startDeal();

        placeBidsWithPlayer1BiddingForHeartAndOthersPass(deal, players);

        assertSame(DealStep.PLAY, deal.getDealStep());

        while (!deal.isOver()) {
            deal.playerPlays(players.get(0), hand1.pop());
            deal.playerPlays(players.get(1), hand2.pop());
            deal.playerPlays(players.get(2), hand3.pop());
            deal.playerPlays(players.get(3), hand4.pop());
        }

        assertTrue(players.get(0).getTeam().isPresent());
        assertTrue(deal.teamDoingCapot().isPresent());
        assertSame(deal.teamDoingCapot().get(), players.get(0).getTeam().get());

    }

    @Test
    public void testTeamDoingCapotWhenCapotNotAchieved() {

        // Player 1 has all trumps except jack -> capot is impossible
        var hand1 = new LinkedList<>(List.of(
            ClassicalCard.NINE_HEART,
            ClassicalCard.ACE_HEART,
            ClassicalCard.TEN_HEART,
            ClassicalCard.KING_HEART,
            ClassicalCard.QUEEN_HEART,
            ClassicalCard.EIGHT_HEART,
            ClassicalCard.SEVEN_HEART,
            ClassicalCard.ACE_DIAMOND
        ));
        var hand2 = new LinkedList<>(List.of(
            ClassicalCard.JACK_HEART, // trump jack
            ClassicalCard.JACK_SPADE,
            ClassicalCard.NINE_SPADE,
            ClassicalCard.TEN_SPADE,
            ClassicalCard.KING_SPADE,
            ClassicalCard.QUEEN_SPADE,
            ClassicalCard.EIGHT_SPADE,
            ClassicalCard.SEVEN_SPADE
        ));
        var hand3 = new LinkedList<>(List.of(
            ClassicalCard.JACK_DIAMOND,
            ClassicalCard.ACE_SPADE,
            ClassicalCard.NINE_DIAMOND,
            ClassicalCard.TEN_DIAMOND,
            ClassicalCard.KING_DIAMOND,
            ClassicalCard.QUEEN_DIAMOND,
            ClassicalCard.EIGHT_DIAMOND,
            ClassicalCard.SEVEN_DIAMOND
        ));
        var hand4 = new LinkedList<>(List.of(
            ClassicalCard.JACK_CLUB,
            ClassicalCard.NINE_CLUB,
            ClassicalCard.ACE_CLUB,
            ClassicalCard.TEN_CLUB,
            ClassicalCard.KING_CLUB,
            ClassicalCard.QUEEN_CLUB,
            ClassicalCard.EIGHT_CLUB,
            ClassicalCard.SEVEN_CLUB
        ));

        var players = buildPlayersWithHand(hand1,hand2,hand3,hand4);

        var deal = new ContreeDeal("test", players, new ContreeGameEventSender());
        deal.startDeal();

        placeBidsWithPlayer1BiddingForHeartAndOthersPass(deal, players);

        assertTrue(deal.isPlayStep());

        int nbTricks = 1;
        while (!deal.isOver()) {

            System.err.printf("Trick #%d%n", nbTricks);

            if (nbTricks == 2) {
                deal.playerPlays(players.get(1), hand2.pop());
                deal.playerPlays(players.get(2), hand3.pop());
                deal.playerPlays(players.get(3), hand4.pop());
                deal.playerPlays(players.get(0), hand1.pop());
            }
            else {
                deal.playerPlays(players.get(0), hand1.pop());
                deal.playerPlays(players.get(1), hand2.pop());
                deal.playerPlays(players.get(2), hand3.pop());
                deal.playerPlays(players.get(3), hand4.pop());
            }

            nbTricks++;
        }

        assertTrue(players.get(0).getTeam().isPresent());
        assertTrue(deal.teamDoingCapot().isEmpty());
        assertFalse(deal.isAnnouncedCapot());

    }

    private List<ContreePlayer> buildPlayersWithHand(
            List<ClassicalCard> player1Hand,
            List<ClassicalCard> player2Hand,
            List<ClassicalCard> player3Hand,
            List<ClassicalCard> player4Hand
    ) {

        var players = buildPlayers();

        var player1 = players.get(0);
        var player2 = players.get(1);
        var player3 = players.get(2);
        var player4 = players.get(3);

        when(player1.getHand()).thenReturn(player1Hand);
        when(player1.toString()).thenReturn("Player 1");

        when(player2.getHand()).thenReturn(player2Hand);
        when(player2.toString()).thenReturn("Player 2");

        when(player3.getHand()).thenReturn(player3Hand);
        when(player3.toString()).thenReturn("Player 3");

        when(player4.getHand()).thenReturn(player4Hand);
        when(player4.toString()).thenReturn("Player 4");

        return players;

    }

}
