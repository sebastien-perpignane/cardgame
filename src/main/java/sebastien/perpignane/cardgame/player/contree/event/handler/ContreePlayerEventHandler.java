package sebastien.perpignane.cardgame.player.contree.event.handler;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.event.handler.PlayerEventHandler;

import java.util.Set;

public interface ContreePlayerEventHandler extends PlayerEventHandler<ContreePlayer> {

    void onPlayerTurnToBid(Set<ContreeBidValue> allowedBidValues);

    void onPlayerTurn(Set<ClassicalCard> allowedCards);

}
