package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class TestCasesManagingPlayers {

    protected static List<ContreePlayer> players;

    protected static ContreePlayer player1;
    protected static ContreePlayer player2;
    protected static ContreePlayer player3;
    protected static ContreePlayer player4;

    protected static void initPlayers() {
        players = buildPlayers();

        int playerIndex = 0;
        player1 = players.get(playerIndex++);
        player2 = players.get(playerIndex++);
        player3 = players.get(playerIndex++);
        player4 = players.get(playerIndex);

        when(player1.sameTeam(player3)).thenReturn(true);
        when(player3.sameTeam(player1)).thenReturn(true);

        when(player2.sameTeam(player4)).thenReturn(true);
        when(player4.sameTeam(player2)).thenReturn(true);

    }

    protected static List<ContreePlayer> loopingPlayers(int nbLoops) {
        List<ContreePlayer> loopingPlayers = new ArrayList<>();

        for (int i = 0 ; i < nbLoops ; i++) {
            loopingPlayers.addAll(players);
        }

        return loopingPlayers;

    }

    private static List<ContreePlayer> buildPlayers() {

        ContreePlayer player1 = mock(ContreePlayer.class);
        when(player1.getTeam()).thenReturn(Optional.of(ContreeTeam.TEAM1));
        when(player1.toString()).thenReturn("player 1");

        ContreePlayer player2 = mock(ContreePlayer.class);
        when(player2.getTeam()).thenReturn(Optional.of(ContreeTeam.TEAM2));
        when(player2.toString()).thenReturn("player 2");

        ContreePlayer player3 = mock(ContreePlayer.class);
        when(player3.getTeam()).thenReturn(Optional.of(ContreeTeam.TEAM1));
        when(player3.toString()).thenReturn("player 3");

        ContreePlayer player4 = mock(ContreePlayer.class);
        when(player4.getTeam()).thenReturn(Optional.of(ContreeTeam.TEAM2));
        when(player4.toString()).thenReturn("player 4");

        return List.of(player1, player2, player3, player4);

    }


}
