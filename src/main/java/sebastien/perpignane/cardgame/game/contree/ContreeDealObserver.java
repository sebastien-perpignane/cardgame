package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.CardGameObserver;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.Team;

public interface ContreeDealObserver extends CardGameObserver {

    void onDealStarted(int dealNumber, String dealId);

    void onEndOfDeal(String dealId, Team winnerTeam, ContreeDealScore dealScore, boolean capot);

    void onPlacedBid(String dealId, Player<?, ?> player, ContreeBidValue bidValue, CardSuit suit);

    void onBidStepStarted(String dealId);

    void onBidStepEnded(String dealId);

    void onPlayStepStarted(String dealId, CardSuit trumpSuit);

    void onPlayStepEnded(String dealId);

}
