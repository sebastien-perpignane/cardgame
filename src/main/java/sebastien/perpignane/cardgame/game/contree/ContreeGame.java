package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGame;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.game.GameState;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.ArrayList;
import java.util.List;

public class ContreeGame extends AbstractGame {

    private final ContreeGamePlayers gamePlayers;

    private ContreeGameScore gameScore;

    private ContreeDeals gameDeals;

    private final ContreeGameEventSender gameEventSender;

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
        if (!gamePlayers.isFull()) {
            throw new IllegalArgumentException("Game cannot be started until four players joined the game");
        }
        gameDeals = new ContreeDeals(this, gamePlayers.buildDealPlayers(), gameEventSender);
        gameScore = new ContreeGameScore();
        gameDeals.createAndStartNewDeal();
    }

    public synchronized void placeBid(ContreePlayer player, ContreeBidValue bidValue, CardSuit cardSuit) {
        gameDeals.placeBid(player, bidValue, cardSuit);
    }

    public void joinGame(ContreePlayer p) {
        gamePlayers.joinGame(p);
        if (gamePlayers.isFull()) {
            updateState(GameState.STARTED);
        }
    }

    public synchronized void playCard(ContreePlayer player, ClassicalCard card) {
        gameEventSender.sendPlayedCardEvent(player, card);
        gameDeals.playCard(player, card);
        if (gameDeals.isCurrentDealOver()) {

            gameScore.addDealScore(gameDeals.getCurrentDeal());

            // FIXME Send updated score event
            ContreeTeam.getTeams().forEach(t -> System.out.printf("Score of Team %s : %d%n", t, gameScore.getTeamScore(t)));

            if (gameScore.isMaximumScoreReached()) {
                updateState(GameState.OVER);
                gameEventSender.sendEndOfGameEvent(this);
                return;
            }
            gameDeals.createAndStartNewDeal();
        }

    }

    public void registerAsGameObserver(GameObserver observer) {
        gameEventSender.registerAsGameObserver(observer);
    }

    public Team getWinner() {
        return gameScore.getWinner();
    }

    @Override
    public ContreeGameEventSender getGameEventSender() {
        return gameEventSender;
    }

    public boolean isOver() {
        return getState() == GameState.OVER;
    }

    @Override
    public String toString() {
        return "ContreeGame{" +
                "id=" + getGameId() +
                ",winner=" + gameScore.getWinner() +
                '}';
    }

}
