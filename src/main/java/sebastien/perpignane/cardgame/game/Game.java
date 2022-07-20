package sebastien.perpignane.cardgame.game;

import org.apache.commons.collections4.iterators.LoopingListIterator;
import org.springframework.stereotype.Component;
import sebastien.perpignane.cardgame.card.Card;
import sebastien.perpignane.cardgame.player.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * TODO Refactoring needed, too much responsibilities in the same class.
 */
@Component
public class Game {

    private final String gameId;
    private GameState state;


    //We use CopyOnWriteArrayList to allow player registration while game is running,
    //for example to replace a bot player with a human one.
    private final List<Player> players = new CopyOnWriteArrayList<>();
    private Player currentPlayer = null;
    private final LoopingListIterator<Player> playerIterator = new LoopingListIterator<>(players);
    private Player winner = null;


    private Trick currentTrick = null;
    private final List<Trick> tricks = new ArrayList<>();

    private final GameEventSender gameEventSender;

    public Game(GameEventSender gameEventSender) {
        this.gameEventSender = gameEventSender;
        gameId = UUID.randomUUID().toString();
        updateState(GameState.NOT_INITIALIZED);
        updateState(GameState.INITIALIZED);
    }

    public void startGame(List<Card> shuffledCards) {

        if (players.size() != 2) {
            throw new IllegalStateException("2 players are required");
        }

        updateState(GameState.STARTING);

        distributeCardsToPlayers(shuffledCards);
        currentPlayer = playerIterator.next();

        currentTrick = new WarTrick(trickId(), players, gameEventSender);

        updateState(GameState.STARTED);
        letKnowPlayers();
    }

    private void distributeCardsToPlayers(List<Card> cards) {

        if (cards.size() % players.size() != 0) {
            throw new IllegalStateException("The cards cannot be equally distributed to all players");
        }

        var nbCards = cards.size() / players.size();
        int playerIdx = 0;
        for (Player player : players) {
            int offset = playerIdx * nbCards;
            player.receiveHand(cards.subList(offset, offset + nbCards));
            playerIdx++;
        }
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

        gameEventSender.sendPlayedCardEvent(pc);

        currentTrick.playerPlay(pc);
        if (currentTrick.isEndOfTrick()) {
            gameEventSender.sendWonTrickEvent(currentTrick);
            currentTrick.getWinner().receiveNewCards(currentTrick.getAllCards());

            tricks.add(currentTrick);
            if (endOfGameCondition()) {
                updateState(GameState.OVER);
                computeWinner();
                gameEventSender.sendEndOfGameEvent(this);
                return;
            }
            currentTrick = new WarTrick(trickId(), players, gameEventSender);
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

    private void computeWinner() {
        for (var p: players) {
            if (!p.hasNoMoreCard()) {
                winner = p;
                break;
            }
        }
    }

    private boolean endOfGameCondition() {
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
        gameEventSender.sendNextPlayerEvent(currentPlayer);
        letKnowPlayers();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
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
        gameEventSender.sendStateEvent(oldState, state);
        if (newState == GameState.STARTED) {
            gameEventSender.sendGameStartedEvent(players);
        }
    }

    public boolean isOver() {
        return getState() == GameState.OVER;
    }

    public void registerAsGameObserver(GameObserver observer) {
        gameEventSender.registerAsGameObserver(observer);
    }

    public void registerAsTrickObserver(WarTrickObserver observer) {
        gameEventSender.registerAsTrickObserver(observer);
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameId='" + gameId + '\'' +
                '}';
    }

    List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

}
