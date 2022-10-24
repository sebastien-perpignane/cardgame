package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// TODO [Unit test]
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

        assertEquals(team1Score, gameScoreWith1000AsMax.getTeamScore(ContreeTeam.TEAM1));
        assertEquals(team2Score, gameScoreWith1000AsMax.getTeamScore(ContreeTeam.TEAM2));

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

        assertEquals(120 + 40, gameScoreWith1000AsMax.getTeamScore(ContreeTeam.TEAM1));
        assertEquals(40 + 120, gameScoreWith1000AsMax.getTeamScore(ContreeTeam.TEAM2));

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

        assertTrue(gameScoreWith1000AsMax.isMaximumScoreReached());
        assertSame(ContreeTeam.TEAM1, gameScoreWith1000AsMax.getWinner());

    }

    @DisplayName("Adding the score of a not over deal throws exception")
    @Test
    public void testExceptionIfAddingScoreOfNotOverDeal() {

        ContreeDeal deal = MockDealBuilder.builder().withIsOver(false).build();

        assertThrows(
            RuntimeException.class,
            () -> gameScoreWith1000AsMax.addDealScore(deal)
        );

    }

}