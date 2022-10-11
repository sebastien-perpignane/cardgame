package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.Player;

import java.util.Set;

public interface Trick {

    boolean isEndOfTrick();

    Player getWinner();

    // FIXME is this method required in generic interface ?
    Set<ClassicalCard> getAllCards();
}
