package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.CardGameObserver;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

public interface ContreeTrickObserver extends CardGameObserver {

    void onNewTrick(String trickId, CardSuit trumpSuit);

    void onTrumpedTrick(String trickId);

    void onEndOfTrick(String trickId, ContreePlayer winner);

}
