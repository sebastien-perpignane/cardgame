package sebastien.perpignane.cardgame.game;

import java.util.List;

public interface WarTrickObserver {

    void onWar(List<PlayedCard> cards);

}
