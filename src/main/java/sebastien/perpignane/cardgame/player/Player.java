package sebastien.perpignane.cardgame.player;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGame;

import java.util.Collection;
import java.util.Optional;

public interface Player {

    void receiveHand(Collection<ClassicalCard> cards);

    // FIXME Replace with finer grained event
    void onUpdatedGame();

    void setGame(AbstractGame game);

    boolean hasNoMoreCard();

    void receiveNewCards(Collection<ClassicalCard> cards);

    void onGameStarted();

    void onGameOver();

    // FIXME should not be in the public interface
    ClassicalCard play();

    // FIXME to be replaced with the "allowed cards" version
    void onPlayerTurn();

    int nbAvailableCards();

    Collection<ClassicalCard> getHand();

    Optional<? extends Team> getTeam();

    void setTeam(Team team);

    boolean isBot();

}
