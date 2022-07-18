package sebastien.perpignane.cardgame.game;

import org.apache.commons.collections4.iterators.LoopingListIterator;
import org.springframework.stereotype.Component;
import sebastien.perpignane.cardgame.card.Card;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSetShuffler;
import sebastien.perpignane.cardgame.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * TODO Refactoring needed, too much responsabilities in the same class.
 */
@Component
public class Game {

    //We use CopyOnWriteArrayList to allow addition of observers while the game is running.
    private final List<GameObserver> gameObservers = new CopyOnWriteArrayList<>();
    private final List<WarTrickObserver> trickObservers = new CopyOnWriteArrayList<>();

    //We use CopyOnWriteArrayList to allow player registration while game is running,
    //for example to replace a bot player with a human one.
    private final List<Player> players = new CopyOnWriteArrayList<>();

    private Player currentPlayer = null;

    private final List<Card> cards;

    private final String gameId;

    private GameState state;

    private Player winner = null;

    private final List<Trick> tricks = new ArrayList<>();
    private Trick currentTrick = null;

    private final LoopingListIterator<Player> playerIterator = new LoopingListIterator<>(players);

    public Game(CardSet cardSet, CardSetShuffler shuffler, List<GameObserver> gameObservers, List<WarTrickObserver> trickObservers) {
        this.gameObservers.addAll(gameObservers);
        this.trickObservers.addAll(trickObservers);
        gameId = UUID.randomUUID().toString();
        updateState(GameState.NOT_INITIALIZED);
        cards = shuffler.shuffle(cardSet);
        updateState(GameState.INITIALIZED);
    }

    public void startGame() {

        if (players.size() != 2) {
            throw new IllegalStateException("2 players are required");
        }

        updateState(GameState.STARTING);

        if (cards.size() % players.size() != 0) {
            throw new IllegalStateException("The cards cannot be equally distributed to all players");
        }

        distributeCardsToPlayers(cards.size() / players.size());
        currentPlayer = playerIterator.next();
        currentTrick = new WarTrick(trickId(), players, trickObservers);
        updateState(GameState.STARTED);
        letKnowPlayers();
    }

    private String trickId() {
        return String.format("%s-%d", gameId, tricks.size());
    }

    public void joinGame(Player p) {
        p.setGame(this);
        players.add(p);
    }

    /*
    The method is synchronized because we want only one player to be able to play at the same time.
    -> it satisfies a "business constraint" and, by "luck", it also simplifies multithreading management.
    TODO Move trick management
    */
    public synchronized void play(PlayedCard pc) {

        if (isInvalidPlay(pc)) return;

        sendPlayedCardEvent(pc);

        currentTrick.playerPlay(pc);
        if (currentTrick.isEndOfTrick()) {
            sendWonTrickEvent(currentTrick);
            currentTrick.getWinner().receiveNewCards(currentTrick.getAllCards());

            tricks.add(currentTrick);
            if (endOfGameCondition()) {
                updateState(GameState.OVER);
                computeWinner();
                sendEndOfGameEvent();
                return;
            }
            currentTrick = new WarTrick(trickId(), players, trickObservers);
        }
        updateToNextPlayer();

    }

    private boolean isInvalidPlay(PlayedCard pc) {

        boolean invalidPlay = false;

        if ( !state.isPlayable()) {
            invalidPlay = true;
            System.err.printf("Cheater detected : %s tries to play on a game not in a playable state.", pc.player());
        }

        if (!players.contains(pc.player())) {
            invalidPlay = true;
            System.err.printf("Cheater detected : %s does not play in the game %s.", pc.player(), this);
        }

        if (pc.player() != getCurrentPlayer()) {
            invalidPlay = true;
            System.err.printf("Cheater detected : %s is not the current player.", pc.player());
        }
        return invalidPlay;
    }

    public boolean endOfGameCondition() {
        int nbPlayersWithCards = 0;
        for (var player: players) {
            if (!player.hasNoMoreCard()) {
                nbPlayersWithCards++;
            }
        }
        return nbPlayersWithCards == 1;
    }

    // TODO rename
    private void letKnowPlayers() {
        for (Player p : players) {
            p.onUpdatedGame();
        }
    }

    private void updateToNextPlayer() {
        currentPlayer = playerIterator.next();
        sendNextPlayerEvent();
        letKnowPlayers();

    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    private void distributeCardsToPlayers(int nbCards) {
        int playerIdx = 0;
        for (Player player : players) {
            int offset = playerIdx * nbCards;
            player.receiveHand(cards.subList(offset, offset + nbCards));
            playerIdx++;
        }
    }

    public GameState getState() {
        return state;
    }

    public Player getWinner() {
        return winner;
    }

    private void updateState(GameState newState) {
        var oldState = state;
        state = newState;
        sendStateEvent(oldState, state);
    }

    void computeWinner() {
        for (var p: players) {
            if (!p.hasNoMoreCard()) {
                winner = p;
                break;
            }
        }
    }

    public boolean isOver() {
        return getState() == GameState.OVER;
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
        if (newState == GameState.STARTED) {
            players.forEach(Player::onGameStarted);
        }
    }

    void sendPlayedCardEvent(PlayedCard pc) {
        for (GameObserver observer : gameObservers) {
            observer.onCardPlayed(pc);
        }
    }

    void sendNextPlayerEvent() {
        for (GameObserver observer : gameObservers) {
            observer.onNextPlayer(getCurrentPlayer());
        }
        //getCurrentPlayer().onPlayerTurn();
    }

    void sendEndOfGameEvent() {
        gameObservers.forEach(go -> go.onEndOfGame(this));
        players.forEach(Player::onGameOver);
    }

    void sendWonTrickEvent(Trick trick) {
        for (GameObserver observer : gameObservers) {
            observer.onWonTrick(trick);
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId='" + gameId + '\'' +
                '}';
    }

    List<Card> getCards() {
        return cards;
    }

}
