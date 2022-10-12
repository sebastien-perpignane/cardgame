package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;

public interface ContreeTrickObserver {

    void onNewTrick(String trickId, CardSuit trumpSuit);

    void onTrumpedTrick(String trickId);

}
