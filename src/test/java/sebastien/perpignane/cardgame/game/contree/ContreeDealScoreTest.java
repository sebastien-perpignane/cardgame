package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContreeDealScoreTest extends TestCasesManagingPlayers {

    @DisplayName("Get the deal score computed for each team")
    @Test
    void testGetTeamScore() {

        ContreeDeal deal = mock(ContreeDeal.class);
        DealScoreCalculator calculator = mock(DealScoreCalculator.class);
        var fakeScore = Map.of(ContreeTeam.TEAM1, 130, ContreeTeam.TEAM2, 30);
        when(calculator.computeDealScores(deal)).thenReturn(
            new DealScoreResult(
                    fakeScore, fakeScore, fakeScore, true
            )
        );

        ContreeDealScore dealScore = new ContreeDealScore(calculator);

        dealScore.computeScore(deal);

        assertThat(dealScore.getTeamScore(ContreeTeam.TEAM1)).isEqualTo(130);
        assertThat(dealScore.getTeamScore(ContreeTeam.TEAM2)).isEqualTo(30);
        assertThat(dealScore.isContractReached()).isTrue();

    }
}