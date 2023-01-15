package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.game.GameStatus;
import sebastien.perpignane.cardgame.player.contree.ContreePlayerState;

import java.util.List;

public record ContreeGameState(
        String gameId,
        GameStatus status,
        List<ContreePlayerState> players,
        int team1Score,
        int team2Score,
        int maxScore) {



}
