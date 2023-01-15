package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;

import java.util.Collection;
import java.util.Set;

public interface ContreePlayerEventHandler extends PlayerEventHandler<ContreePlayer> {

    void onReceivedHand(Collection<ClassicalCard> hand);

    void onPlayerTurnToBid(Set<ContreeBidValue> allowedBidValues);

    void onPlayerTurn(Set<ClassicalCard> allowedCards);

    void onEjection();

}
