package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;

import java.util.List;
import java.util.UUID;

public abstract class AbstractGame {

    private final String gameId;
    private GameState state;


    public AbstractGame() {
        // FIXME this lifecycle is shit
        updateState(GameState.NOT_INITIALIZED);
        gameId = UUID.randomUUID().toString();
        updateState(GameState.INITIALIZED);
        updateState(GameState.WAITING_FOR_PLAYERS);

    }

    protected void updateState(GameState newState) {

        // FIXME gameEventSender is null on the INIT state
        if (getEventSender() == null) {
            return;
        }

        GameState oldState = state;
        state = newState;
        getEventSender().sendStateEvent(oldState, state);
        if (newState == GameState.STARTED) {
            getEventSender().sendGameStartedEvent(getPlayers());
        }
    }

    protected abstract List<? extends Player> getPlayers();

    public GameState getState() {
        return state;
    }

    public String getGameId() {
        return gameId;
    }

    protected abstract AbstractGameEventSender getEventSender();

}
