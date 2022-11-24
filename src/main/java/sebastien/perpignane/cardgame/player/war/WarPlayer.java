package sebastien.perpignane.cardgame.player.war;

import sebastien.perpignane.cardgame.game.war.WarGame;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.Team;

public interface WarPlayer extends Player<WarGame, Team> {

    void playCard();

}
