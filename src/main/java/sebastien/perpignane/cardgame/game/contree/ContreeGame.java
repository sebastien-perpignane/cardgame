package sebastien.perpignane.cardgame.game.contree;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGame;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.game.GameState;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dependent
public class ContreeGame extends AbstractGame {

    private final ContreeGamePlayers gamePlayers;

    private final ContreeDeals gameDeals;

    private final ContreeGameEventSender gameEventSender;

    @Inject
    ContreeGame(
        ContreeGamePlayers gamePlayers,
        ContreeDeals gameDeals,
        ContreeGameEventSender eventSender
    ) {
        super();
        this.gamePlayers = gamePlayers;
        this.gameDeals = gameDeals;
        this.gameEventSender = eventSender;
        updateState(GameState.WAITING_FOR_PLAYERS);
    }

    @Override
    protected List<ContreePlayer> getPlayers() {
        return new ArrayList<>(gamePlayers.getGamePlayers());
    }

    private void startGame() {
        gameDeals.startDeals(getGameId(), gamePlayers.buildDealPlayers());
    }

    public synchronized void placeBid(ContreePlayer player, ContreeBidValue bidValue, CardSuit cardSuit) {
        if (isOver()) {
            throw new IllegalStateException("This game is over, you cannot place a bid on it");
        }
        gameDeals.placeBid(player, bidValue, cardSuit);
    }

    public synchronized void joinGame(ContreePlayer p) {
        if (isOver()) {
            throw new IllegalStateException("This game is over, you cannot join it");
        }
        gamePlayers.joinGame(p);
        p.setGame(this);
        if (gamePlayers.isFull()) {
            updateState(GameState.STARTED);
            startGame();
        }
    }

    public synchronized void playCard(ContreePlayer player, ClassicalCard card) {
        if (isOver()) {
            throw new IllegalStateException("This game is over, you cannot play a card on it");
        }
        gameEventSender.sendPlayedCardEvent(player, card);
        gameDeals.playCard(player, card);

        if (gameDeals.isMaximumScoreReached()) {
            updateState(GameState.OVER);
            gameEventSender.sendEndOfGameEvent(this);
        }

    }

    public void registerAsGameObserver(GameObserver observer) {
        if (isOver()) {
            throw new IllegalStateException("This game is over, you cannot register an observer on it");
        }
        gameEventSender.registerAsGameObserver(observer);
    }

    public Optional<ContreeTeam> getWinner() {
        if (gameDeals == null) {
            return Optional.empty();
        }
        return gameDeals.getWinner();
    }

    @Override
    protected ContreeGameEventSender getEventSender() {
        return gameEventSender;
    }

    @Override
    public String toString() {
        return "ContreeGame{" +
                "id=" + getGameId() +
                ",winner=" + (gameDeals.getWinner().isEmpty() ? "(game not over)" : gameDeals.getWinner().get()) +
                '}';
    }

    public int getNbDeals() {
        return gameDeals.getNbDeals();
    }

}
