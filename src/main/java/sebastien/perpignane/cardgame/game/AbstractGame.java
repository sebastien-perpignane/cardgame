package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;

import java.util.List;
import java.util.UUID;

public abstract class AbstractGame<P extends Player<?, ?>> {

    private final String gameId;
    private GameStatus status;

    protected AbstractGame() {
        gameId = UUID.randomUUID().toString();
    }

    protected void updateState(GameStatus newState) {

        GameStatus oldState = status;
        status = newState;
        getEventSender().sendStateEvent(oldState, status);
        if (newState == GameStatus.STARTED) {
            getEventSender().sendGameStartedEvent(getPlayers());
        }
    }

    protected abstract List<P> getPlayers();

    public boolean isInPlayableState() {
        return status.isPlayable();
    }

    public boolean isInitialized() {
        return status == GameStatus.INITIALIZED;
    }

    public boolean isWaitingForPlayers() {
        return status == GameStatus.WAITING_FOR_PLAYERS;
    }

    public boolean isStarted() {
        return status == GameStatus.STARTED;
    }

    public boolean isOver() {
        return status == GameStatus.OVER;
    }

    public String getGameId() {
        return gameId;
    }

    public GameStatus getStatus() {
        return status;
    }

    protected abstract AbstractGameEventSender getEventSender();

}
