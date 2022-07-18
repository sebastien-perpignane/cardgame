package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;

public interface GameObserver {

    void onStateUpdated(GameState oldState, GameState newState);

    void onCardPlayed(PlayedCard pc);

    void onNextPlayer(Player p);

    void onWonTrick(Trick trick);

    void onEndOfGame(Game game);

}
