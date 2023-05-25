package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.Team;

import java.util.Collection;
import java.util.Optional;

public interface Trick<P extends Player<G, T>, G extends AbstractGame<P>, T extends Team> {

    boolean isOver();

    Optional<P> getWinner();

    Collection<P> getPlayedCards();

}
