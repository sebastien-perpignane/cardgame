package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ContreeGameScoreTest extends TestCasesManagingPlayers {

    private ContreeGameScore gameScoreWith1000AsMax;

    private final List<ContreeDeal> deals = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        gameScoreWith1000AsMax = new ContreeGameScore(1000);
    }

    @DisplayName("Team score is consistent with the score of the only played deal")
    @Test
    void testGetTeamScoreWhenOneDealPlayed() {

        final int team1Score = 150;
        final int team2Score = 10;

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsOver(true)
                .withScore(
                    Map.of(
                        ContreeTeam.TEAM1, team1Score,
                        ContreeTeam.TEAM2, team2Score
                    )
                )
                .build();

        deals.add(deal);

        deals.forEach(
                gameScoreWith1000AsMax::addDealScore
        );

        assertThat(gameScoreWith1000AsMax.getTeamScore(ContreeTeam.TEAM1)).isEqualTo(team1Score);
        assertThat(gameScoreWith1000AsMax.getTeamScore(ContreeTeam.TEAM2)).isEqualTo(team2Score);

    }

    @DisplayName("Team score is consistent with the scores of 2 played deals")
    @Test
    void testGetTeamScoreWhenTwoDealsPlayed() {

        deals.add(
            MockDealBuilder.builder()
                    .withIsOver(true)
                    .withScore(
                            Map.of(
                                    ContreeTeam.TEAM1, 120,
                                    ContreeTeam.TEAM2, 40
                            )
                    )
                    .build()
        );

        deals.add(
                MockDealBuilder.builder()
                        .withIsOver(true)
                        .withScore(
                                Map.of(
                                        ContreeTeam.TEAM1, 40,
                                        ContreeTeam.TEAM2, 120
                                )
                        )
                        .build()
        );

        deals.forEach(
                gameScoreWith1000AsMax::addDealScore
        );

        assertThat(gameScoreWith1000AsMax.getTeamScore(ContreeTeam.TEAM1)).isEqualTo(120 + 40);
        assertThat(gameScoreWith1000AsMax.getTeamScore(ContreeTeam.TEAM2)).isEqualTo(40 + 120);

    }

    @DisplayName("The max score is considered as reached after enough played deals and the winner is consistent with the score")
    @Test
    void testIsMaximumScoreReachedWhenItIsAndTheWinnerIsConsistent() {

        int i = 0;
        while (i < 10) {

            deals.add(
                    MockDealBuilder.builder()
                            .withIsOver(true)
                            .withScore(
                                    Map.of(
                                            ContreeTeam.TEAM1, 100,
                                            ContreeTeam.TEAM2, 60
                                    )
                            )
                            .build()
            );

            i++;
        }

        deals.forEach(
                gameScoreWith1000AsMax::addDealScore
        );

        assertThat(gameScoreWith1000AsMax.isMaximumScoreReached()).isTrue();
        assertThat(gameScoreWith1000AsMax.getWinner().orElseThrow()).isSameAs(ContreeTeam.TEAM1);

    }

    @DisplayName("Adding the score of a not over deal throws exception")
    @Test
    public void testExceptionIfAddingScoreOfNotOverDeal() {

        ContreeDeal deal = MockDealBuilder.builder().withIsOver(false).build();

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> gameScoreWith1000AsMax.addDealScore(deal));

    }

}