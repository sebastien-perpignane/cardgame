package sebastien.perpignane.cardgame.game;

import org.springframework.beans.factory.annotation.Autowired;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSetShuffler;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.WarBotPlayer;

public class GameLifecycleManager {

    @Autowired
    @SuppressWarnings("unused")
    private Game game;

    @Autowired
    @SuppressWarnings("unused")
    private CardSetShuffler cardSetShuffler;

    public void startGame() {
        Player p1 = new WarBotPlayer();
        Player p2 = new WarBotPlayer();
        game.joinGame(p1);
        game.joinGame(p2);
        game.startGame(cardSetShuffler.shuffle(CardSet.GAME_52));
    }

}
