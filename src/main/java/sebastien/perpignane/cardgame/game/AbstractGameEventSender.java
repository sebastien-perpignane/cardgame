package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractGameEventSender {

    //We use a concurrent set (provided by concurrent hash map) to allow safe addition of observers while the game is running.
    protected final Set<GameObserver> gameObservers = ConcurrentHashMap.newKeySet();

    protected AbstractGameEventSender(GameObserver... observers) {
        gameObservers.addAll(Arrays.asList(observers));
    }

    public void registerAsGameObserver(GameObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("observer cannot be null");
        }
        gameObservers.add(observer);
    }

    public void sendStateEvent(GameStatus oldState, GameStatus newState) {
        gameObservers.forEach(go -> go.onStateUpdated(oldState, newState));
    }

    public <P extends Player<?, ?>> void sendGameStartedEvent(List<P> players) {
        players.forEach(Player::onGameStarted);
    }

    public void sendNextPlayerEvent(Player<?, ?> currentPlayer) {
        gameObservers.forEach(go -> go.onNextPlayer(currentPlayer));
    }

}
