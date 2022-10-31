package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;

public interface Trick {

    boolean isOver();

    Player getWinner();

}
