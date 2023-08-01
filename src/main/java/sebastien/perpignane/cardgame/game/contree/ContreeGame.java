package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGame;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.game.GameStatus;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContreeGame extends AbstractGame<ContreePlayer> {

    private final ContreeGamePlayers gamePlayers;

    private final ContreeDeals gameDeals;

    private final ContreeGameEventSender gameEventSender;

    ContreeGame(
        ContreeGamePlayers gamePlayers,
        ContreeDeals gameDeals,
        ContreeGameEventSender eventSender
    ) {
        super();
        this.gamePlayers = gamePlayers;
        this.gameDeals = gameDeals;
        this.gameEventSender = eventSender;
        updateState(GameStatus.WAITING_FOR_PLAYERS);
    }

    @Override
    public List<ContreePlayer> getPlayers() {
        return new ArrayList<>(gamePlayers.getGamePlayers());
    }

    public synchronized void joinGame(ContreePlayer p) {
        if (isOver()) {
            throw new IllegalStateException("This game is over, you cannot join it");
        }
        JoinGameResult joinGameResult = gamePlayers.joinGame(p);
        p.setGame(this);
        joinGameResult.replacedPlayer().ifPresent(contreePlayer -> p.receiveHand(contreePlayer.getHand()));
        gameEventSender.sendJoinedGameEvent(this, joinGameResult.playerIndex(), p);
        if (isStarted()) {

            joinGameResult.replacedPlayer().ifPresentOrElse(
                    rp -> {
                        joinGameResult.replacedPlayer().ifPresent(Player::onGameEjection);
                        p.onGameStarted();
                    },
                    () -> {throw new IllegalStateException("When game is already started, if a new player joins the game, joinGameResult.replacedPlayer must not be null");}
            );
        }
        if (gamePlayers.isFull() && !isStarted()) {
            updateState(GameStatus.STARTED);
            startGame();
        }
    }

    public synchronized void leaveGame(ContreePlayer leavingPlayer) {

        if (isOver()) {
            return;
        }
        var newPlayer = gamePlayers.leaveGameAndReplaceWithBotPlayer(leavingPlayer);
        newPlayer.setGame(this);
        newPlayer.receiveHand(leavingPlayer.getHand());
        if (isStarted()) {
            newPlayer.onGameStarted();
        }

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

    public synchronized void playCard(ContreePlayer player, ClassicalCard card) {
        if (isOver()) {
            throw new IllegalStateException("This game is over, you cannot play a card on it");
        }
        gameEventSender.sendPlayedCardEvent(player, card);
        gameDeals.playCard(player, card);
        if (gameDeals.isMaximumScoreReached()) {
            updateState(GameStatus.OVER);
            getPlayers().forEach(ContreePlayer::onGameOver);
            gameEventSender.sendEndOfGameEvent(this);
        }
    }

    public void registerAsGameObserver(GameObserver observer) {
        if (isOver()) {
            throw new IllegalStateException("This game is over, you cannot register an observer on it");
        }
        gameEventSender.registerAsObserver(observer);
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
                ",winner=" + (gameDeals.getWinner().map(Enum::toString).orElse("(game not over)")) +
                '}';
    }

    public int getNbDeals() {
        return gameDeals.getNbDeals();
    }

    public ContreeGameState toState() {
        return new ContreeGameState(
                getGameId(),
                getStatus(),
                gamePlayers.getGamePlayers().stream().map(p -> p == null ? null : p.toState()).toList(),
                gameDeals.getGameScore().getTeamScore(ContreeTeam.TEAM1),
                gameDeals.getGameScore().getTeamScore(ContreeTeam.TEAM2),
                gameDeals.getGameScore().getMaxScore()
        );
    }

    public void forceEndOfGame() {
        getEventSender().sendEndOfGameEvent(this);
    }

}
