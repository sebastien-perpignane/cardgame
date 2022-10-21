package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContreeTestUtils {

    public static List<ContreePlayer> buildPlayers() {

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
