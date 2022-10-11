package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.Player;

public interface ContreeDealObserver {

    void onDealStarted(String dealId);

    void onDealOver(String dealId);

    void onPlacedBid(String dealId, Player player, ContreeBidValue bidValue, CardSuit suit);

    void onBidStepStarted(String dealId);

    void onBidStepEnded(String dealId);

    void onPlayStepStarted(String dealId, CardSuit trumpSuit);

    void onPlayStepEnded(String dealId);

}
