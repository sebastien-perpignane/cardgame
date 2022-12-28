package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGameEventSender;
import sebastien.perpignane.cardgame.game.CardGameObserver;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ContreeGameEventSender extends AbstractGameEventSender {

    private final Set<ContreeDealObserver> dealObservers = ConcurrentHashMap.newKeySet();
    private final Set<ContreeTrickObserver> trickObservers = ConcurrentHashMap.newKeySet();

    public ContreeGameEventSender() {
        this(Collections.emptySet());
    }

    public ContreeGameEventSender(Collection<CardGameObserver> observers) {
        observers.forEach(this::registerAsObserver);
    }

    public ContreeGameEventSender(CardGameObserver... observers) {
        this(Arrays.asList(observers));
    }

    public void registerAsObserver(CardGameObserver observer) {
        if (observer instanceof ContreeTrickObserver cto) {
            trickObservers.add(cto);
        }
        if (observer instanceof GameObserver go) {
            gameObservers.add(go);
        }
        if (observer instanceof ContreeDealObserver cdo) {
            dealObservers.add(cdo);
        }
    }

    void sendEndOfGameEvent(ContreeGame contreeGame) {
        gameObservers.forEach(go -> go.onEndOfGame(contreeGame));
        contreeGame.getPlayers().forEach(Player::onGameOver);
    }

    void sendPlayedCardEvent(Player<?, ?> player, ClassicalCard card) {
        gameObservers.forEach(observer -> observer.onCardPlayed(player, card));
    }

    void sendStartOfDealEvent(int dealNumber, String dealId) {
        dealObservers.forEach(cdo -> cdo.onDealStarted(dealNumber, dealId));
    }

    void sendEndOfDealEvent(String dealId, Team winnerTeam, ContreeDealScore dealScore, boolean capot) {
        dealObservers.forEach(cdo -> cdo.onEndOfDeal(dealId, winnerTeam, dealScore, capot));
    }

    void sendPlacedBidEvent(String dealId, ContreeBid bid) {
        dealObservers.forEach(cdo -> cdo.onPlacedBid(dealId, bid.player(), bid.bidValue(), bid.cardSuit()));
    }

    void sendBidStepStartedEvent(String dealId) {
        dealObservers.forEach(cdo -> cdo.onBidStepStarted(dealId));
    }

    void sendBidStepEndedEvent(String dealId) {
        dealObservers.forEach(cdo -> cdo.onBidStepEnded(dealId));
    }

    void sendPlayStepStartedEvent(String dealId, CardSuit trumpSuit) {
        dealObservers.forEach(cdo -> cdo.onPlayStepStarted(dealId, trumpSuit));
    }

    void sendPlayStepEndedEvent(String dealId) {
        dealObservers.forEach(cdo -> cdo.onPlayStepEnded(dealId));
    }

    void sendTrumpedTrickEvent(String trickId) {
        trickObservers.forEach(to -> to.onTrumpedTrick(trickId));
    }

    void sendNewTrickEvent(String trickId, CardSuit trumpSuit) {
        trickObservers.forEach(to -> to.onNewTrick(trickId, trumpSuit));
    }

    void sendEndOfTrickEvent(String trickId, ContreeTeam winner) {
        trickObservers.forEach(to -> to.onEndOfTrick(trickId, winner));
    }

    void sendJoinedGameEvent(ContreeGame game, int playerIndex, ContreePlayer player) {
        gameObservers.forEach(go -> go.onJoinedGame(game, playerIndex, player));
    }

}
