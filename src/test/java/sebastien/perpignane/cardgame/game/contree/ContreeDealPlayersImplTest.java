package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContreeDealPlayersImplTest extends TestCasesManagingPlayers {

    private ContreeDealPlayersImpl dealPlayers;

    private ContreeDeal dealWith80_club_contract_by_player1;

    private ContreeDeal dealWith80_club_contract_by_player2;

    @BeforeAll
    static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {
        ContreeGamePlayers gamePlayers = mock(ContreeGamePlayers.class);
        when(gamePlayers.getGamePlayers()).thenReturn(players);

        ContreeGamePlayerSlots playerSlots = new ContreeGamePlayerSlots();
        for (int i = 0; i < players.size(); i++) {
            playerSlots.addPlayerToSlotIndex(i, players.get(i));
        }
        when(gamePlayers.getPlayerSlots()).thenReturn(playerSlots);

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
    void testAttackTeamIsTeam1AndDefenseTeamIsTeam2() {

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player1);

        var attackTeam = dealPlayers.getCurrentDealAttackTeam().orElseThrow();
        var defenseTeam = dealPlayers.getCurrentDealDefenseTeam().orElseThrow();

        assertThat(attackTeam).isSameAs(ContreeTeam.TEAM1);
        assertThat(defenseTeam).isSameAs(ContreeTeam.TEAM2);

    }

    @DisplayName("When the contract bid is owned by a team 2 player, team 2 is the attack team")
    @Test
    void testAttackTeamIsExpectedOne() {

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player2);

        var attackTeam = dealPlayers.getCurrentDealAttackTeam().orElseThrow();

        assertThat(attackTeam).isSameAs(ContreeTeam.TEAM2);

    }

    @DisplayName("When the contract bid is owned by a team 2 player, team 1 is the defense team")
    @Test
    void testDefenseTeamIsExpectedOne() {

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player2);

        var defenseTeam = dealPlayers.getCurrentDealDefenseTeam().orElseThrow();

        assertThat(defenseTeam).isSameAs(ContreeTeam.TEAM1);

    }

    @DisplayName("On second deal, player1 becomes the dealer, player2 the first player")
    @Test
    void testPlayerRotationWhenSecondDealStarts() {

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player1);

        assertThat(dealPlayers.getCurrentDealPlayers()).isEqualTo(players);

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player2);

        assertThat(dealPlayers.getCurrentDealPlayers().get(0)).isSameAs(player2);

    }

    @DisplayName("On first deal, deal players are in their initial order")
    @Test
    void testGetPlayerSlots() {

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player1);

        assertThat(dealPlayers.getCurrentDealPlayerSlots().stream().map(ps -> ps.getPlayer().orElseThrow()).toList()).isEqualTo(players);

    }

    @DisplayName("setCurrentDeal throws an exception if the same deal is set multiple times")
    @Test
    void testSetSameCurrentDealTwiceThrowsException() {

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player2);

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player2));

    }


    @DisplayName("Calculation a player list from an invalid index (>3) throw an exception")
    @Test
    void testRollPlayerFromInvalidIndexThrowsException() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> dealPlayers.rollPlayerFromIndex(4));
    }


    @DisplayName("buildTrickPlayers builds a valid object, with expected current bidder")
    @Test
    void testBuildTrickPlayers() {

        ContreeTrickPlayers trickPlayers = dealPlayers.buildTrickPlayers();

        assertThat(trickPlayers).isNotNull();
        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer()).isPresent();
        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer()).containsSame(player1);
    }

    @DisplayName("buildBidPlayers builds a valid object, with expected current player")
    @Test
    void testBuildBidPlayers() {

        ContreeBidPlayers bidPlayers = dealPlayers.buildBidPlayers();

        assertThat(bidPlayers).isNotNull();
        assertThat(bidPlayers.getCurrentBidderSlot().getPlayer()).isPresent();
        assertThat(bidPlayers.getCurrentBidderSlot().getPlayer()).containsSame(player1);

    }

    @DisplayName("indexOf returns the expected index of the player")
    @Test
    void testIndexOf() {
        assertThat(dealPlayers.indexOf(player1)).isZero();
        assertThat(dealPlayers.indexOf(player2)).isEqualTo(1);
        assertThat(dealPlayers.indexOf(player3)).isEqualTo(2);
        assertThat(dealPlayers.indexOf(player4)).isEqualTo(3);
    }

    @DisplayName("indexOf returns the expected index of the player after current dealer is updated")
    @Test
    void testIndexOfAfterSecondDealRotation() {

        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player1);
        dealPlayers.setCurrentDeal(dealWith80_club_contract_by_player2);

        assertThat(dealPlayers.indexOf(player2)).isZero();
        assertThat(dealPlayers.indexOf(player3)).isEqualTo(1);
        assertThat(dealPlayers.indexOf(player4)).isEqualTo(2);
        assertThat(dealPlayers.indexOf(player1)).isEqualTo(3);

    }

}