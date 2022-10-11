package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeGame;
import sebastien.perpignane.cardgame.game.war.WarGame;
import sebastien.perpignane.cardgame.player.Player;

public interface GameObserver extends CardGameObserver {

    void onStateUpdated(GameState oldState, GameState newState);

    void onCardPlayed(Player player, ClassicalCard card);

    void onNextPlayer(Player p);

    void onWonTrick(Trick trick);

    // FIXME must work with abstract classes

    void onEndOfGame(WarGame warGame);

    void onEndOfGame(ContreeGame contreeGame);

}
