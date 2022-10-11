package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.HashMap;
import java.util.Map;

public class ContreeGameScore {

    private final Map<ContreeTeam, Integer> scoreByTeam = new HashMap<>();

    public ContreeGameScore() {

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
        ContreeTeam.getTeams().forEach(t -> incrementTeamScore(t, deal.getScore().getTeamScore(t)) );

    }

    private void incrementTeamScore(ContreeTeam team, int newScore) {
        scoreByTeam.put(team, scoreByTeam.get(team) + newScore);
    }

    public boolean isMaximumScoreReached() {
        return scoreByTeam.entrySet().stream().anyMatch(e -> e.getValue() > 1000);
    }

    public ContreeTeam getWinner() {
        return scoreByTeam.entrySet().stream().filter(e -> e.getValue() > 1000).map(Map.Entry::getKey).findFirst().orElseThrow();
    }

}
