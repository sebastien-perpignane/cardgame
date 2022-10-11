package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.Player;

import java.util.Collection;
import java.util.Optional;

public interface ContreePlayer extends Player {

    // TODO send allowed bids
    void onPlayerTurnToBid();

    void onPlayerTurn(Collection<ClassicalCard> allowedCards);

    Optional<ContreeTeam> getTeam();

    boolean sameTeam(Player otherPlayer);

}
