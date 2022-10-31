package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.Team;

import java.util.Map;

public class ContreeDealScore {

    private Map<Team, Integer> scoreByTeam;

    private final DealScoreCalculator scoreCalculator;

    public ContreeDealScore(DealScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    public void computeScore(ContreeDeal deal) {
        this.scoreByTeam = scoreCalculator.computeDealScores(deal);
    }

    public Integer getTeamScore(Team team) {
        return scoreByTeam.get(team);
    }

}
