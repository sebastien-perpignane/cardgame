package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;

import java.util.Collection;
import java.util.Optional;

public interface Trick {

    boolean isOver();

    Optional<? extends Player<?, ?>> getWinner();

    Collection<? extends PlayedCard<?, ?>> getPlayedCards();

}
