package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.Map;
import java.util.Optional;

public class ContreeDealScore {

    private Map<ContreeTeam, Integer> rawScoreByTeam;

    private Map<ContreeTeam, Integer> notRoundedScoreByTeam;

    private Map<ContreeTeam, Integer> scoreByTeam;

    private boolean contractReached;

    private final DealScoreCalculator scoreCalculator;

    public ContreeDealScore(DealScoreCalculator scoreCalculator) {
        this.scoreCalculator = scoreCalculator;
    }

    public void computeScore(ContreeDeal deal) {

        var scoreCalculationResult = scoreCalculator.computeDealScores(deal);

        this.rawScoreByTeam = scoreCalculationResult.rawScoreByTeam();
        this.notRoundedScoreByTeam = scoreCalculationResult.finalNotRoundedScoreByTeam();
        this.scoreByTeam = scoreCalculationResult.finalRoundedScoreByTeam();
        this.contractReached = scoreCalculationResult.contractIsReached();
    }

    public Integer getRawTeamScore(ContreeTeam team) {
        return rawScoreByTeam.get(team);
    }

    public Integer getTeamNotRoundedScore(ContreeTeam team) {
        return notRoundedScoreByTeam.get(team);
    }

    public Integer getTeamScore(ContreeTeam team) {
        return scoreByTeam.get(team);
    }

    public Optional<Team> winnerTeam() {
        return scoreByTeam.entrySet().stream().filter(e -> e.getValue() > 0).max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }

    public boolean isContractReached() {
        return contractReached;
    }

}
