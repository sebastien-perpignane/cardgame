package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractGameEventSender {

    //We use CopyOnWriteArrayList to allow addition of observers while the game is running.
    protected final List<GameObserver> gameObservers = new CopyOnWriteArrayList<>();

    public AbstractGameEventSender(GameObserver... observers) {
        gameObservers.addAll(Arrays.asList(observers));
    }

    public void registerAsGameObserver(GameObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("observer cannot be null");
        }
        gameObservers.add(observer);
    }

    public void sendStateEvent(GameState oldState, GameState newState) {
        gameObservers.forEach(go -> go.onStateUpdated(oldState, newState));
    }

    public void sendGameStartedEvent(List<? extends Player> players) {
        players.forEach(Player::onGameStarted);
    }

    public void sendNextPlayerEvent(Player currentPlayer) {
        gameObservers.forEach(go -> go.onNextPlayer(currentPlayer));
    }
}
