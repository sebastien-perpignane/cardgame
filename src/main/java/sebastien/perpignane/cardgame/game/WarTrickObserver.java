package sebastien.perpignane.cardgame.game;

import java.util.List;

public interface WarTrickObserver extends CardGameObserver {

    void onWar(List<PlayedCard> cardsTriggeringWar);

}
