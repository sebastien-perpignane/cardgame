package sebastien.perpignane.cardgame.game.contree;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Dependent
public class ContreeGameScore {

    private final int maxScore;

    private final Map<ContreeTeam, Integer> scoreByTeam = new HashMap<>();

    @Inject
    public ContreeGameScore(int maxScore) {
        this.maxScore = maxScore;
        ContreeTeam.getTeams().forEach(t -> scoreByTeam.put(t, 0));
    }

    public Integer getTeamScore(ContreeTeam team) {
        return scoreByTeam.get(team);
    }

    public void addDealScore(ContreeDeal deal) {
        if (!deal.isOver()) {
            throw new IllegalStateException("Deal is not over, score cannot be added");
        }
        if (deal.hasOnlyNoneBids()) {
            return;
        }
        ContreeTeam.getTeams().forEach(t -> incrementTeamScore(t, deal.getTeamScore(t)) );
    }

    private void incrementTeamScore(ContreeTeam team, int newScore) {
        scoreByTeam.put(team, scoreByTeam.get(team) + newScore);
    }

    public boolean isMaximumScoreReached() {
        return scoreByTeam.entrySet().stream().anyMatch(e -> e.getValue() >= maxScore);
    }

    public Optional<ContreeTeam> getWinner() {
        return scoreByTeam.entrySet().stream().filter(e -> e.getValue() >= maxScore).map(Map.Entry::getKey).findFirst();
    }

}
