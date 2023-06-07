package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeGame;
import sebastien.perpignane.cardgame.game.war.WarGame;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

public interface GameObserver extends CardGameObserver {

    void onStateUpdated(GameStatus oldState, GameStatus newState);

    void onCardPlayed(Player<?, ?> player, ClassicalCard card);

    void onNextPlayer(Player<?, ?> p);

    void onWonTrick(Trick<?, ?, ?> trick);

    // FIXME must work with abstract classes

    void onEndOfGame(WarGame warGame);

    void onEndOfGame(ContreeGame contreeGame);

    void onJoinedGame(ContreeGame contreeGame, int playerIndex, ContreePlayer player);

}
