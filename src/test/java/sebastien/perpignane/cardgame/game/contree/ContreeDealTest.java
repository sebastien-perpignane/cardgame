package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.*;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.buildPlayers;

// TODO needs rework, to focus on ContreeDeal responsibility
public class ContreeDealTest extends TestCasesManagingPlayers {


    private ContreeBidPlayers bidPlayers;

    private ContreeTrickPlayers trickPlayers;

    private ContreeDealPlayers dealPlayers;

    private ContreeDeal deal;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    public void setUp() {

        bidPlayers = mock(ContreeBidPlayers.class);
        trickPlayers = mock(ContreeTrickPlayers.class);

        when(bidPlayers.getCurrentBidder()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));
        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        dealPlayers = mock(ContreeDealPlayers.class);
        when(dealPlayers.buildBidPlayers()).thenReturn(bidPlayers);
        when(dealPlayers.buildTrickPlayers()).thenReturn(trickPlayers);
        when(dealPlayers.getNumberOfPlayers()).thenReturn(4);



        ContreeGameEventSender eventSender = mock(ContreeGameEventSender.class);

        deal = new ContreeDeal("TEST", dealPlayers, eventSender);

    }

    @DisplayName("When a deal is started without players, starting the deal fails")
    @Test
    public void testStartDealWhenNoPlayers() {
        when(dealPlayers.getNumberOfPlayers()).thenReturn(0);
        when(dealPlayers.getCurrentDealPlayers()).thenReturn(Collections.emptyList());

        assertThrows(
                RuntimeException.class,
                () -> deal.startDeal()
        );

    }

    @DisplayName("When a deal is started with a number no allowing fair card distribution, starting the deal fails")
    @Test
    public void testStartDealWhenBadNumberOfPlayers() {
        when(dealPlayers.getNumberOfPlayers()).thenReturn(3);
        when(dealPlayers.getCurrentDealPlayers()).thenReturn(List.of(player1, player2, player3));

        assertThrows(
                RuntimeException.class,
                () -> deal.startDeal()
        );

    }

    private void configureDealPlayersWithNbTricks() {
        var multipliedPlayers = loopingPlayers(8);
        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(multipliedPlayers));
        when(bidPlayers.getCurrentBidder()).thenAnswer(AdditionalAnswers.returnsElementsOf(multipliedPlayers));

    }

    private ContreeDealPlayers buildDealPlayers(List<ContreePlayer> players) {
        ContreeBidPlayers bidPlayers = mock(ContreeBidPlayers.class);
        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        ContreeDealPlayers dealPlayers = mock(ContreeDealPlayers.class);
        when(dealPlayers.buildBidPlayers()).thenReturn(bidPlayers);
        when(dealPlayers.buildTrickPlayers()).thenReturn(trickPlayers);
        when(dealPlayers.getNumberOfPlayers()).thenReturn(4);

        when(bidPlayers.getCurrentBidder()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));
        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        return dealPlayers;
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


    @DisplayName("Players cannot play a card in BID step ")
    @Test
    public void testExceptionWhenPlayingWhileBidStep() {
        deal.startDeal();

        assertTrue(deal.isBidStep());

        assertThrows(
            IllegalStateException.class,
            () -> deal.playerPlays(players.get(0), ClassicalCard.JACK_DIAMOND)
        );

    }

    // TODO move to ContreeGameBids tests
    @DisplayName("Players cannot place a bid in PLAY step")
    @Test
    public void testExceptionWhenPlacingBidDuringPlayStep() {

        deal.startDeal();

        placeBidsWithPlayer1BiddingForHeartAndOthersPass(deal, players);

        assertTrue( deal.isPlayStep() );

        assertThrows(
            IllegalStateException.class,
            () -> deal.placeBid(new ContreeBid(players.get(0), ContreeBidValue.NONE, null))
        );
    }

    @DisplayName("Only current player can play a card")
    @Test
    public void testExceptionWhenNotCurrentPlayerPlays() {

        deal.startDeal();

        placeBidsWithPlayer1BiddingForHeartAndOthersPass(deal, players);

        assertTrue( deal.isPlayStep() );

        assertThrows(
            IllegalArgumentException.class,
            () -> deal.playerPlays(player2, ClassicalCard.JACK_DIAMOND)
        );
    }

    private void placeBidsWithPlayer1BiddingForHeartAndOthersPass(ContreeDeal deal, List<ContreePlayer> players) {
        deal.placeBid(new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.HEARTS));
        deal.placeBid(new ContreeBid(player2, ContreeBidValue.NONE, null));
        deal.placeBid(new ContreeBid(player3, ContreeBidValue.NONE, null));
        deal.placeBid(new ContreeBid(player4, ContreeBidValue.NONE, null));
    }

    // TODO to be tested in ContreeTricksTest
    @Test
    @Disabled
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

        players.forEach(p -> {
            when(p.getHand()).thenReturn(CardSet.GAME_32.getGameCards());
        });
        /*var players = buildPlayersWithHand(
            hand1,
            hand2,
            hand3,
            hand4
        );*/

        /*

        ContreeBidPlayers bidPlayers = mock(ContreeBidPlayers.class);
        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);

        ContreeDealPlayers dealPlayers = mock(ContreeDealPlayers.class);
        when(dealPlayers.buildBidPlayers()).thenReturn(bidPlayers);
        when(dealPlayers.buildTrickPlayers()).thenReturn(trickPlayers);
        when(dealPlayers.getNumberOfPlayers()).thenReturn(4);

        when(bidPlayers.getCurrentBidder()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));
         */
        //when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        //var deal = new ContreeDeal("test", dealPlayers, new ContreeGameEventSender());
        deal.startDeal();

        //when(trickPlayers.getCurrentPlayer()).thenReturn(players.get(0));
        placeBidsWithPlayer1BiddingForHeartAndOthersPass(deal, players);

        assertTrue( deal.isPlayStep() );

        //verify(trickPlayers, times(32)).getCurrentPlayer();
        /*while (!deal.isOver()) {

            deal.playerPlays(players.get(0), hand1.pop());

            deal.playerPlays(players.get(1), hand2.pop());
            deal.playerPlays(players.get(2), hand3.pop());
            deal.playerPlays(players.get(3), hand4.pop());

        }

        assertTrue(player1.getTeam().isPresent());
        assertTrue(deal.teamDoingCapot().isPresent());
        assertSame(deal.teamDoingCapot().get(), players.get(0).getTeam().get());*/

    }

    // TODO to be tested in ContreeTricksTes
    @Test
    @Disabled
    public void testTeamDoingCapotWhenCapotNotAchieved() {

        ContreeDealPlayers dealPlayers = mock(ContreeDealPlayers.class);

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

        var deal = new ContreeDeal("test", dealPlayers, new ContreeGameEventSender());
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
