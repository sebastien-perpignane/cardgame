package sebastien.perpignane.cardgame.game.war;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.*;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.war.WarPlayer;
import sebastien.perpignane.cardgame.player.war.local.thread.AbstracLocalThreadWarPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * TODO Refactoring needed, too much responsibilities in the same class.
 */
//@Component
public class WarGame extends AbstractGame<WarPlayer> {

    private final List<WarPlayer> players = new CopyOnWriteArrayList<>();

    private WarPlayer currentPlayer = null;

    private WarPlayer winner = null;

    private final WarGameEventSender warGameEventSender;

    private WarTrick currentTrick = null;
    private final List<WarTrick> tricks = new ArrayList<>();

    private int currentPlayerIndex = 0;

    public WarGame(CardGameObserver... observers) {
        super();
        warGameEventSender = new WarGameEventSender(observers);
        updateState(GameStatus.NOT_INITIALIZED);
        updateState(GameStatus.INITIALIZED);
    }

    public void startGame(List<ClassicalCard> shuffledCards) {

        if (players.size() != 2) {
            throw new IllegalStateException("2 players are required");
        }

        updateState(GameStatus.STARTING);

        distributeCardsToPlayers(shuffledCards);
        currentPlayer = players.get(currentPlayerIndex);

        currentTrick = new WarTrick(trickId(), players, warGameEventSender);

        updateState(GameStatus.STARTED);
        warGameEventSender.sendNextPlayerEvent(currentPlayer);
        letKnowPlayers();
    }

    private void distributeCardsToPlayers(List<ClassicalCard> cards) {

        if (cards.size() % players.size() != 0) {
            throw new IllegalStateException("The cards cannot be equally distributed to all players");
        }

        int nbCards = cards.size() / players.size();
        int playerIdx = 0;
        for (WarPlayer player : players) {
            int offset = playerIdx * nbCards;
            player.receiveHand(cards.subList(offset, offset + nbCards));
            playerIdx++;
        }
    }

    private String trickId() {
        return getGameId() + "-" + tricks.size();
    }

    public synchronized void joinGame(WarPlayer p) {
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

    private void addPlayer(WarPlayer p) {
        players.add(p);
    }

    boolean hasBotPlayer() {
        return players.stream().anyMatch(Player::isBot);
    }

    private void replaceAnyBotPlayer(WarPlayer player) {
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

    public synchronized void play(AbstracLocalThreadWarPlayer player, ClassicalCard card) {

        if (isInvalidPlay(player)) return;

        warGameEventSender.sendPlayedCardEvent(player, card);

        currentTrick.playerPlay(player, card);
        player.removeCardFromHand(card);
        if (currentTrick.isOver()) {
            warGameEventSender.sendWonTrickEvent(currentTrick);
            currentTrick.getWinner().orElseThrow().receiveNewCards(currentTrick.getAllCards());

            tricks.add(currentTrick);
            if (endOfGameCondition()) {
                updateState(GameStatus.OVER);
                computeWinner();
                warGameEventSender.sendEndOfGameEvent(this);
                getPlayers().forEach(WarPlayer::onGameOver);
                return;
            }
            currentTrick = new WarTrick(trickId(), players, warGameEventSender);
        }
        updateToNextPlayer();
    }

    private boolean isInvalidPlay(AbstracLocalThreadWarPlayer player) {

        boolean invalidPlay = !isInPlayableState();

        if (!players.contains(player)) {
            invalidPlay = true;
        }

        if (player != getCurrentPlayer()) {
            invalidPlay = true;
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
        currentPlayer = getNextPlayer();
        warGameEventSender.sendNextPlayerEvent(currentPlayer);
        letKnowPlayers();
    }

    private WarPlayer getNextPlayer() {
        if (currentPlayerIndex == 1) {
            currentPlayerIndex = 0;
        }
        else {
            currentPlayerIndex = 1;
        }

        return players.get(currentPlayerIndex);

    }

    public WarPlayer getCurrentPlayer() {
        return currentPlayer;
    }

    public WarPlayer getWinner() {
        return winner;
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

    protected List<WarPlayer> getPlayers() {
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
