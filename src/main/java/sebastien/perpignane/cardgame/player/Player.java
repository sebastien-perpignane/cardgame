package sebastien.perpignane.cardgame.player;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGame;

import java.util.Collection;
import java.util.Optional;

public interface Player<G extends AbstractGame<?>> {

    void receiveHand(Collection<ClassicalCard> cards);

    // FIXME Replace with finer grained event
    void onUpdatedGame();

    void setGame(G game);

    boolean hasNoMoreCard();

    void receiveNewCards(Collection<ClassicalCard> cards);

    void onGameStarted();

    void onGameOver();

    // FIXME to be replaced with the "allowed cards" version
    void onPlayerTurn();

    int nbAvailableCards();

    Collection<ClassicalCard> getHand();

    Optional<? extends Team> getTeam();

    void setTeam(Team team);

    boolean isBot();

    void playCard(ClassicalCard card);

}
