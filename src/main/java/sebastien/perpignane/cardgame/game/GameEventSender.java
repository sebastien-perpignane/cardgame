package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameEventSender {

    //We use CopyOnWriteArrayList to allow addition of observers while the game is running.
    private final List<GameObserver> gameObservers = new CopyOnWriteArrayList<>();
    private final List<WarTrickObserver> trickObservers = new CopyOnWriteArrayList<>();

    public GameEventSender(Collection<CardGameObserver> observers) {

        observers.forEach(o -> {
            if (o instanceof WarTrickObserver warTrickObserver) {
                trickObservers.add(warTrickObserver);
            }
            if (o instanceof GameObserver gameObserver) {
                gameObservers.add(gameObserver);
            }
        });

    }

    public GameEventSender(CardGameObserver... observers) {
        this(Arrays.asList(observers));
    }

    public void registerAsGameObserver(GameObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("observer cannot be null");
        }
        gameObservers.add(observer);
    }

    public void registerAsTrickObserver(WarTrickObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("observer cannot be null");
        }
        trickObservers.add(observer);
    }

    void sendStateEvent(GameState oldState, GameState newState) {
        for (GameObserver observer : gameObservers) {
            observer.onStateUpdated(oldState, newState);
        }
    }

    void sendGameStartedEvent(List<Player> players) {
        players.forEach(Player::onGameStarted);
    }

    void sendPlayedCardEvent(PlayedCard pc) {
        for (GameObserver observer : gameObservers) {
            observer.onCardPlayed(pc);
        }
    }

    void sendNextPlayerEvent(Player currentPlayer) {
        for (GameObserver observer : gameObservers) {
            observer.onNextPlayer(currentPlayer);
        }
        //getCurrentPlayer().onPlayerTurn();
    }

    void sendEndOfGameEvent(Game game) {
        gameObservers.forEach(go -> go.onEndOfGame(game));
        game.getPlayers().forEach(Player::onGameOver);
    }

    void sendWonTrickEvent(Trick trick) {
        for (GameObserver observer : gameObservers) {
            observer.onWonTrick(trick);
        }
    }

    void sendWarEvent(List<PlayedCard> cardsTriggeringWar) {
        trickObservers.forEach(o -> o.onWar(cardsTriggeringWar));
    }
}