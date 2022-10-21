package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.buildPlayers;

public class ContreeDealPlayersImplTest {

    @DisplayName("When the contract bid is owned by a team 1 player, team 1 is the attack team and team 2 is the defense team")
    @Test
    public void testAttackTeamIsTeam1AndDefenseTeamIsTeam2() {

        var players = buildPlayers();

        ContreeGamePlayers gamePlayers = mock(ContreeGamePlayers.class);

        when(gamePlayers.getGamePlayers()).thenReturn(players);

        ContreeDealPlayers dealPlayers =  new ContreeDealPlayersImpl(gamePlayers);

        var deal = MockDealBuilder.builder()
                .withDealContractBid(
                    new ContreeBid(players.get(0), ContreeBidValue.EIGHTY, CardSuit.CLUBS)
                )
                .build();

        dealPlayers.setCurrentDeal(deal);

        var attackTeam = dealPlayers.getCurrentDealAttackTeam().orElseThrow();
        var defenseTeam = dealPlayers.getCurrentDealDefenseTeam().orElseThrow();

        assertSame(ContreeTeam.TEAM1, attackTeam);
        assertSame(ContreeTeam.TEAM2, defenseTeam);

    }

    @DisplayName("When the contract bid is owned by a team 2 player, team 2 is the attack team and team 1 is the defense team")
    @Test
    public void testPlayerListAfterNewDeal() {

        var players = buildPlayers();

        ContreeGamePlayers gamePlayers = mock(ContreeGamePlayers.class);

        when(gamePlayers.getGamePlayers()).thenReturn(players);

        ContreeDealPlayers dealPlayers =  new ContreeDealPlayersImpl(gamePlayers);

        var deal = MockDealBuilder.builder()
                .withDealContractBid(
                        new ContreeBid(players.get(1), ContreeBidValue.EIGHTY, CardSuit.CLUBS)
                )
                .build();

        dealPlayers.setCurrentDeal(deal);

        var attackTeam = dealPlayers.getCurrentDealAttackTeam().orElseThrow();
        var defenseTeam = dealPlayers.getCurrentDealDefenseTeam().orElseThrow();

        assertSame(ContreeTeam.TEAM2, attackTeam);
        assertSame(ContreeTeam.TEAM1, defenseTeam);

    }

    @DisplayName("When the contract bid is owned by a team 2 player, team 2 is the attack team and team 1 is the defense team")
    @Test
    public void testAttackTeamIsTeam2AndDefenseTeamIsTeam1() {

        var players = buildPlayers();

        ContreeGamePlayers gamePlayers = mock(ContreeGamePlayers.class);

        when(gamePlayers.getGamePlayers()).thenReturn(players);

        ContreeDealPlayers dealPlayers =  new ContreeDealPlayersImpl(gamePlayers);

        var deal = MockDealBuilder.builder()
                .withDealContractBid(
                        new ContreeBid(players.get(1), ContreeBidValue.EIGHTY, CardSuit.CLUBS)
                )
                .build();

        dealPlayers.setCurrentDeal(deal);

        var attackTeam = dealPlayers.getCurrentDealAttackTeam().orElseThrow();
        var defenseTeam = dealPlayers.getCurrentDealDefenseTeam().orElseThrow();

        assertSame(ContreeTeam.TEAM2, attackTeam);
        assertSame(ContreeTeam.TEAM1, defenseTeam);

    }

    @DisplayName("On second deal, player1 becomes the dealer, player2 the first player")
    @Test
    public void testPlayerRotationWhenSecondDealStarts() {

        var players = buildPlayers();

        ContreeGamePlayers gamePlayers = mock(ContreeGamePlayers.class);

        when(gamePlayers.getGamePlayers()).thenReturn(players);

        ContreeDealPlayers dealPlayers =  new ContreeDealPlayersImpl(gamePlayers);

        var deal1 = MockDealBuilder.builder()
                .withDealContractBid(
                        new ContreeBid(players.get(1), ContreeBidValue.EIGHTY, CardSuit.CLUBS)
                )
                .build();

        var deal2 = MockDealBuilder.builder()
                .withDealContractBid(
                        new ContreeBid(players.get(1), ContreeBidValue.EIGHTY, CardSuit.CLUBS)
                )
                .build();

        dealPlayers.setCurrentDeal(deal1);

        assertEquals(players, dealPlayers.getCurrentDealPlayers());

        dealPlayers.setCurrentDeal(deal2);

        assertSame(players.get(1), dealPlayers.getCurrentDealPlayers().get(0));

    }

}