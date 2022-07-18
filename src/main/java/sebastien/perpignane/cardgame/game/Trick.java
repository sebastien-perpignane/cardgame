package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.card.Card;
import sebastien.perpignane.cardgame.player.Player;

import java.util.List;

public interface Trick {
    void playerPlay(PlayedCard pc);

    boolean isEndOfTrick();

    boolean isPrematureEndOfTrick();

    Player getWinner();

    List<Card> getAllCards();
}
