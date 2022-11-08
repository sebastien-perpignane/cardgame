package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.Map;
import java.util.Optional;

public class ContreeDealScore {

    private Map<ContreeTeam, Integer> scoreByTeam;

    private final DealScoreCalculator scoreCalculator;

    public ContreeDealScore(DealScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    public void computeScore(ContreeDeal deal) {
        this.scoreByTeam = scoreCalculator.computeDealScores(deal).finalRoundedScore();
    }

    public Integer getTeamScore(ContreeTeam team) {
        return scoreByTeam.get(team);
    }

    public Optional<Team> winnerTeam() {
        return scoreByTeam.entrySet().stream().filter(e -> e.getValue() > 0).max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }

}
