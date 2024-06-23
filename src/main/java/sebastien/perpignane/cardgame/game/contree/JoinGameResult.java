package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Optional;

public record JoinGameResult(int playerIndex, Optional<ContreePlayer> replacedPlayer) {
}
