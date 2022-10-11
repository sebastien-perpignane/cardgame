package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGame;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.game.GameState;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.*;

public class ContreeGame extends AbstractGame {

    final static int NB_PLAYERS = ContreeGamePlayers.NB_MAX_PLAYERS;

    private final ContreeGamePlayers gamePlayers;

    private ContreeGameScore gameScore;

    private final ContreeDeals gameDeals;

    private final ContreeGameEventSender gameEventSender;

    public ContreeGame(GameObserver... observers) {
        this.gameEventSender = new ContreeGameEventSender(observers);
        gamePlayers = new ContreeGamePlayers(this);
        gameDeals = new ContreeDeals(this, gamePlayers, gameEventSender);
    }

    @Override
    protected List<ContreePlayer> getPlayers() {
        return new ArrayList<>(gamePlayers.getPlayers());
    }

    public void startGame() {
        updateState(GameState.STARTED);
        gameScore = new ContreeGameScore();
        gameDeals.createAndStartNewDeal();
    }

    public synchronized void placeBid(ContreePlayer player, ContreeBidValue bidValue, CardSuit cardSuit) {
        gameDeals.placeBid(player, bidValue, cardSuit);
    }

    public void joinGame(ContreePlayer p) {
        gamePlayers.joinGame(p);
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
