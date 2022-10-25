package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContreeDealPlayersImplTest extends TestCasesManagingPlayers {

    private ContreeDealPlayersImpl dealPlayers;

    private ContreeDeal dealWith80_club_contract_by_player1;

    private ContreeDeal dealWith80_club_contract_by_player2;


    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    public void setUp() {
        ContreeGamePlayers gamePlayers = mock(ContreeGamePlayers.class);
        when(gamePlayers.getGamePlayers()).thenReturn(players);

        dealPlayers =  new ContreeDealPlayersImpl(gamePlayers);

        dealWith80_club_contract_by_player1 = MockDealBuilder.builder()
                .withDealContractBid(
                        new ContreeBid(player1, ContreeBidValue.EIGHTY, CardSuit.CLUBS)
                )
                .build();

        dealWith80_club_contract_by_player2 = MockDealBuilder.builder()
                .withDealContractBid(
                        new ContreeBid(player2, ContreeBidValue.EIGHTY, CardSuit.CLUBS)
                )
                .build();

    }

    @DisplayName("When the contract bid is owned by a team 1 player, team 1 is the attack team and team 2 is the defense team")
    @Test
    public void testAttackTeamIsTeam1AndDefenseTeamIsTeam2() {

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player1);

        var attackTeam = dealPlayers.getCurrentDealAttackTeam().orElseThrow();
        var defenseTeam = dealPlayers.getCurrentDealDefenseTeam().orElseThrow();

        assertSame(ContreeTeam.TEAM1, attackTeam);
        assertSame(ContreeTeam.TEAM2, defenseTeam);

    }

    @DisplayName("When the contract bid is owned by a team 2 player, team 2 is the attack team")
    @Test
    public void testAttackTeamIsExpectedOne() {

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player2);

        var attackTeam = dealPlayers.getCurrentDealAttackTeam().orElseThrow();

        assertSame(ContreeTeam.TEAM2, attackTeam);

    }

    @DisplayName("When the contract bid is owned by a team 2 player, team 1 is the defense team")
    @Test
    public void testDefenseTeamIsExpectedOne() {

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player2);

        var defenseTeam = dealPlayers.getCurrentDealDefenseTeam().orElseThrow();

        assertSame(ContreeTeam.TEAM1, defenseTeam);

    }

    @DisplayName("On second deal, player1 becomes the dealer, player2 the first player")
    @Test
    public void testPlayerRotationWhenSecondDealStarts() {

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player1);

        assertEquals(players, dealPlayers.getCurrentDealPlayers());

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player2);

        assertSame(player2, dealPlayers.getCurrentDealPlayers().get(0));

    }

    @DisplayName("setCurrentDeal throws an exception if the same deal is set multiple times")
    @Test
    public void testSetSameCurrentDealTwiceThrowsException() {

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player2);

        assertThrows(
                RuntimeException.class,
                () -> dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player2)
        );

    }


    @DisplayName("Calculation a player list from an invalid index (>3) throw an exception")
    @Test
    public void testRollPlayerFromInvalidIndexThrowsException() {
        assertThrows(
            RuntimeException.class,
            () -> dealPlayers.rollPlayerFromIndex(4)
        );
    }

    @DisplayName("buildBidPlayers builds a valid object, with expected current player")
    @Test
    public void testBuildBidPlayers() {

        ContreeTrickPlayers trickPlayers = dealPlayers.buildTrickPlayers();

        assertNotNull(trickPlayers);
        assertNotNull(trickPlayers.getCurrentPlayer());
        assertSame(player1, trickPlayers.getCurrentPlayer());
    }

    @DisplayName("buildTrickPlayers builds a valid object, with expected current bidder")
    @Test
    public void testBuildTrickPlayers() {

        ContreeBidPlayers bidPlayers = dealPlayers.buildBidPlayers();

        assertNotNull(bidPlayers);
        assertNotNull(bidPlayers.getCurrentBidder());
        assertSame(player1, bidPlayers.getCurrentBidder());

    }

}