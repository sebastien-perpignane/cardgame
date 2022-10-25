package sebastien.perpignane.cardgame.game.contree;

import jakarta.enterprise.context.Dependent;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGame;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.game.GameState;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.ArrayList;
import java.util.List;

@Dependent
public class ContreeGame extends AbstractGame {

    private final ContreeGamePlayers gamePlayers;

    private ContreeDeals gameDeals;

    private final ContreeGameEventSender gameEventSender;

    public ContreeGame() {
        super();
        this.gameEventSender = new ContreeGameEventSender();
        gamePlayers = new ContreeGamePlayersImpl(this);
        updateState(GameState.WAITING_FOR_PLAYERS);
    }

    public ContreeGame(GameObserver... observers) {
        super();
        this.gameEventSender = new ContreeGameEventSender(observers);
        gamePlayers = new ContreeGamePlayersImpl(this);
        updateState(GameState.WAITING_FOR_PLAYERS);
    }

    @Override
    protected List<ContreePlayer> getPlayers() {
        return new ArrayList<>(gamePlayers.getGamePlayers());
    }

    public void startGame() {

        if (isOver()) {
            throw new IllegalStateException("This game is over, you cannot start it");
        }

        if (!gamePlayers.isFull()) {
            throw new IllegalArgumentException("Game cannot be started until four players joined the game");
        }
        gameDeals = new ContreeDeals(this, gamePlayers.buildDealPlayers());
        gameDeals.startDeals();
    }

    public synchronized void placeBid(ContreePlayer player, ContreeBidValue bidValue, CardSuit cardSuit) {
        if (isOver()) {
            throw new IllegalStateException("This game is over, you cannot place a bid on it");
        }
        gameDeals.placeBid(player, bidValue, cardSuit);
    }

    public void joinGame(ContreePlayer p) {
        if (isOver()) {
            throw new IllegalStateException("This game is over, you cannot join it");
        }
        gamePlayers.joinGame(p);
        if (gamePlayers.isFull()) {
            updateState(GameState.STARTED);
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

    public Team getWinner() {
        return gameDeals.getWinner();
    }

    @Override
    protected ContreeGameEventSender getEventSender() {
        return gameEventSender;
    }

    public boolean isOver() {
        return getState() == GameState.OVER;
    }

    @Override
    public String toString() {
        return "ContreeGame{" +
                "id=" + getGameId() +
                ",winner=" + gameDeals.getWinner() +
                '}';
    }

    public int getNbDeals() {
        return gameDeals.getNbDeals();
    }

}
