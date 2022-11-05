package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;

import java.util.Collection;

public interface Trick {

    boolean isOver();

    Player getWinner();

    Collection<? extends GenericPlayedCard<?, ?>> getPlayedCards();

}
