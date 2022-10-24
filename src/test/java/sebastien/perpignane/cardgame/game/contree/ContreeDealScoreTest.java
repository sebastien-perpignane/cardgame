package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContreeDealScoreTest extends TestCasesManagingPlayers {

    @DisplayName("Get the score computed for each team")
    @Test
    void testGetTeamScore() {

        ContreeDeal deal = mock(ContreeDeal.class);
        DealScoreCalculator calculator = mock(DealScoreCalculator.class);
        when(calculator.computeDealScores()).thenReturn(
            Map.of(ContreeTeam.TEAM1, 130, ContreeTeam.TEAM2, 30)
        );

        ContreeDealScore dealScore = new ContreeDealScore(calculator, deal);

        assertEquals(130, dealScore.getTeamScore(ContreeTeam.TEAM1));
        assertEquals( 30, dealScore.getTeamScore(ContreeTeam.TEAM2));

    }
}