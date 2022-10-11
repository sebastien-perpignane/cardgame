package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.player.Team;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum ContreeTeam implements Team {
    TEAM1,
    TEAM2;


    public static Set<ContreeTeam> getTeams() {
        return Arrays.stream(ContreeTeam.values()).collect(Collectors.toSet());
    }

}
