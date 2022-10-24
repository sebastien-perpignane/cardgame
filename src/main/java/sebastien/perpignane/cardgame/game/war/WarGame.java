package sebastien.perpignane.cardgame.game.war;

import org.apache.commons.collections4.iterators.LoopingListIterator;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.*;
import sebastien.perpignane.cardgame.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * TODO Refactoring needed, too much responsibilities in the same class.
 */
//@Component
public class WarGame extends AbstractGame {

    private final List<Player> players = new CopyOnWriteArrayList<>();
    private Player currentPlayer = null;
    private LoopingListIterator<Player> playerIterator = new LoopingListIterator<>(players);
    private Player winner = null;

    private final WarGameEventSender warGameEventSender;

    private WarTrick currentTrick = null;
    private final List<WarTrick> tricks = new ArrayList<>();

    public WarGame(CardGameObserver... observers) {
        warGameEventSender = new WarGameEventSender(observers);
        updateState(GameState.NOT_INITIALIZED);
        updateState(GameState.INITIALIZED);
    }

    public void startGame(List<ClassicalCard> shuffledCards) {

        if (players.size() != 2) {
            throw new IllegalStateException("2 players are required");
        }

        updateState(GameState.STARTING);

        distributeCardsToPlayers(shuffledCards);
        currentPlayer = playerIterator.next();

        currentTrick = new WarTrick(trickId(), players, warGameEventSender);

        updateState(GameState.STARTED);
        warGameEventSender.sendNextPlayerEvent(currentPlayer);
        letKnowPlayers();
    }

    private void distributeCardsToPlayers(List<ClassicalCard> cards) {

        if (cards.size() % players.size() != 0) {
            throw new IllegalStateException("The cards cannot be equally distributed to all players");
        }

        int nbCards = cards.size() / players.size();
        int playerIdx = 0;
        for (Player player : players) {
            int offset = playerIdx * nbCards;
            player.receiveHand(cards.subList(offset, offset + nbCards));
            playerIdx++;
        }
    }

    private String trickId() {
        return getGameId() + "-" + tricks.size();
    }

    public synchronized void joinGame(Player p) {
        if (players.size() == 2) {
            if (hasBotPlayer()) {
                replaceAnyBotPlayer(p);
                return;
            }
            else {
                throw new IllegalStateException("Game is full");
            }

        }
        p.setGame(this);
        addPlayer(p);
    }

    private void addPlayer(Player p) {
        players.add(p);
        this.playerIterator = new LoopingListIterator<>(players);
    }

    boolean hasBotPlayer() {
        return players.stream().anyMatch(Player::isBot);
    }

    private void replaceAnyBotPlayer(Player player) {
        players.stream().filter(Player::isBot).findFirst().ifPresent(p -> {
            int idx = players.indexOf(p);
            players.set(idx, player);
        });
    }

    // TODO to be tested
    public boolean isWarInProgress() {
        return currentTrick.isWarInProgress();
    }

    // TODO to be tested
    public int currentTrickTurn() {
        return currentTrick.getTrickTurn();
    }

    public synchronized void play(Player player, ClassicalCard card) {

        if (isInvalidPlay(player, card)) return;

        warGameEventSender.sendPlayedCardEvent(player, card);

        currentTrick.playerPlay(player, card);
        if (currentTrick.isOver()) {
            warGameEventSender.sendWonTrickEvent(currentTrick);
            currentTrick.getWinner().receiveNewCards(currentTrick.getAllCards());

            tricks.add(currentTrick);
            if (endOfGameCondition()) {
                updateState(GameState.OVER);
                computeWinner();
                warGameEventSender.sendEndOfGameEvent(this);
                return;
            }
            currentTrick = new WarTrick(trickId(), players, warGameEventSender);
        }
        updateToNextPlayer();
    }

    private boolean isInvalidPlay(Player player, ClassicalCard card) {

        boolean invalidPlay = !getState().isPlayable();

        //System.err.printf("Cheater detected : %s tries to play on a game not in a playable state.", pc.player());

        if (!players.contains(player)) {
            invalidPlay = true;
            //System.err.printf("Cheater detected : %s does not play in the game %s.", pc.player(), this);
        }

        if (player != getCurrentPlayer()) {
            invalidPlay = true;
            //System.err.printf("Cheater detected : %s is not the current player.", pc.player());
        }
        return invalidPlay;
    }

    private void computeWinner() {
        winner = players.stream().filter(Predicate.not(Player::hasNoMoreCard)).findFirst().orElseThrow(() -> {throw new IllegalStateException("No player with remaining cards");});
    }

    private boolean endOfGameCondition() {
        return players.stream().filter(p -> !p.hasNoMoreCard()).count() == 1;
    }

    // TODO rename
    private void letKnowPlayers() {
        players.forEach(Player::onUpdatedGame);
    }

    private void updateToNextPlayer() {
        currentPlayer = playerIterator.next();
        warGameEventSender.sendNextPlayerEvent(currentPlayer);
        letKnowPlayers();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getWinner() {
        return winner;
    }

    public boolean isOver() {
        return getState() == GameState.OVER;
    }

    public void registerAsGameObserver(GameObserver observer) {
        warGameEventSender.registerAsGameObserver(observer);
    }

    public void registerAsTrickObserver(WarTrickObserver observer) {
        warGameEventSender.registerAsTrickObserver(observer);
    }

    // TODO to be tested
    public int nbTricks() {
        return tricks.size();
    }

    @Override
    public String toString() {
        return "WarGame{" +
                "gameId='" + getGameId() + '\'' +
                '}';
    }

    protected List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    @Override
    protected AbstractGameEventSender getEventSender() {
        return warGameEventSender;
    }

    public void forceEndOfGame() {
        warGameEventSender.sendEndOfGameEvent(this);
    }

}
