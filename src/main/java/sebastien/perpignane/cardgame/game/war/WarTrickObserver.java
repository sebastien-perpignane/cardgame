package sebastien.perpignane.cardgame.game.war;

import sebastien.perpignane.cardgame.game.CardGameObserver;

import java.util.List;

public interface WarTrickObserver extends CardGameObserver {

    void onWar(List<WarPlayedCard> cardsTriggeringWar);

}
