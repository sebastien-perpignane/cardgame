package sebastien.perpignane.cardgame.player.event.handler;

import sebastien.perpignane.cardgame.player.Player;

public interface PlayerEventHandler<P extends Player<?, ?>> {

    void onGameOver();

    void onGameStarted();

    void setPlayer(P player);

    boolean isBot();

}
