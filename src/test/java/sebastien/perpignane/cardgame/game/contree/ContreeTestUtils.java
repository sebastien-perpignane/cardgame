package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class ContreeTestUtils {

    public static List<ContreePlayer> buildPlayers() {

        ContreePlayer player1 = mock(ContreePlayer.class);
        when(player1.getTeam()).thenReturn(Optional.of(ContreeTeam.TEAM1));

        ContreePlayer player2 = mock(ContreePlayer.class);
        when(player2.getTeam()).thenReturn(Optional.of(ContreeTeam.TEAM2));

        ContreePlayer player3 = mock(ContreePlayer.class);
        when(player3.getTeam()).thenReturn(Optional.of(ContreeTeam.TEAM1));

        ContreePlayer player4 = mock(ContreePlayer.class);
        when(player4.getTeam()).thenReturn(Optional.of(ContreeTeam.TEAM2));

        return List.of(player1, player2, player3, player4);

    }

}
