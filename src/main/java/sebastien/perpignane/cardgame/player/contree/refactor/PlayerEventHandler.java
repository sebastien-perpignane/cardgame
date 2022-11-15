package sebastien.perpignane.cardgame.player.contree.refactor;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGame;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.player.Player;

import java.util.Set;

public interface PlayerEventHandler<P extends Player<G, ?>, G extends AbstractGame<P>> {

    void onGameOver();

    void onGameStarted();

    // FIXME must move to a contree specific player event handler
    void onPlayerTurnToBid(Set<ContreeBidValue> allowedBidValues);

    void onPlayerTurn(Set<ClassicalCard> allowedCards);

    void setGame(G game);

    void setPlayer(P player);

    boolean isBot();

}
