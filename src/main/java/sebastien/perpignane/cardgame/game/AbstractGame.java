package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;

import java.util.List;
import java.util.UUID;

public abstract class AbstractGame {

    private final String gameId;
    private GameState state;


    public AbstractGame() {
        gameId = UUID.randomUUID().toString();
    }

    protected void updateState(GameState newState) {

        GameState oldState = state;
        state = newState;
        getEventSender().sendStateEvent(oldState, state);
        if (newState == GameState.STARTED) {
            getEventSender().sendGameStartedEvent(getPlayers());
        }
    }

    protected abstract List<? extends Player> getPlayers();

    public boolean isInPlayableState() {
        return state.isPlayable();
    }

    public boolean isInitialized() {
        return state == GameState.INITIALIZED;
    }

    public boolean isWaitingForPlayers() {
        return state == GameState.WAITING_FOR_PLAYERS;
    }

    public boolean isStarted() {
        return state == GameState.STARTED;
    }

    public boolean isOver() {
        return state == GameState.OVER;
    }

    public String getGameId() {
        return gameId;
    }

    protected abstract AbstractGameEventSender getEventSender();

}
