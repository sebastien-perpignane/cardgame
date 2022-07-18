package sebastien.perpignane.cardgame.player;

import sebastien.perpignane.cardgame.card.Card;
import sebastien.perpignane.cardgame.game.Game;

import java.util.Collection;

public interface Player {

    void receiveHand(Collection<Card> cards);

    Collection<Card> getHand();

    void onUpdatedGame();

    void setGame(Game game);

    boolean hasNoMoreCard();

    void receiveNewCards(Collection<Card> cards);

    void onGameStarted();

    void onGameOver();

    Card play();

    void onPlayerTurn();

}
