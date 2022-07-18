package sebastien.perpignane.cardgame.game;

import org.springframework.beans.factory.annotation.Autowired;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.WarBotPlayer;

public class GameLifecycleManager {

    @Autowired
    private Game game;

    public void startGame() {
        Player p1 = new WarBotPlayer();
        Player p2 = new WarBotPlayer();
        game.joinGame(p1);
        game.joinGame(p2);
        game.startGame();
    }

}
