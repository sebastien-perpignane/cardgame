package sebastien.perpignane.cardgame.player.contree.handlers;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.player.contree.PlayerMessage;

public class BiddingBotEventHandler extends ContreeBotPlayerEventHandler {

    @Override
    void manageBidMessage(PlayerMessage bidMessage) {
        getPlayer().placeBid(ContreeBidValue.EIGHTY, CardSuit.HEARTS);
    }
}
