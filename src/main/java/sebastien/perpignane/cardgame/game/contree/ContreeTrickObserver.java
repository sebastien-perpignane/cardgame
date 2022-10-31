package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.CardGameObserver;

public interface ContreeTrickObserver extends CardGameObserver {

    void onNewTrick(String trickId, CardSuit trumpSuit);

    void onTrumpedTrick(String trickId);

}
