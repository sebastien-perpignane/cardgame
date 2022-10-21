package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.ArrayList;
import java.util.List;

import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.buildPlayers;

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

    }

    protected static List<ContreePlayer> loopingPlayers(int nbLoops) {
        List<ContreePlayer> loopingPlayers = new ArrayList<>();

        for (int i = 0 ; i < nbLoops ; i++) {
            loopingPlayers.addAll(players);
        }

        return loopingPlayers;

    }


}
