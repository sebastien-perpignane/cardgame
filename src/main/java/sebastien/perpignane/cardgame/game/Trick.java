package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.Team;

import java.util.Collection;
import java.util.Optional;

public interface Trick<P extends Player<?, T>, C extends PlayedCard<P, ?>, T extends Team> {

    boolean isOver();

    Optional<P> getWinner();

    Optional<T> getWinnerTeam();

    Collection<C> getPlayedCards();

}
