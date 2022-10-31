package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGameEventSender;
import sebastien.perpignane.cardgame.game.CardGameObserver;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.player.Player;

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

        // FIXME a player should be an observer but all events cannot be sent to all players. Example : onPlayerTurn with the allowed cards
        contreeGame.getPlayers().forEach(Player::onGameOver);

    }

    void sendPlayedCardEvent(Player player, ClassicalCard card) {
        gameObservers.forEach(observer -> observer.onCardPlayed(player, card));
    }

    void sendStartOfDealEvent(String dealId) {
        dealObservers.forEach(cdo -> cdo.onDealStarted(dealId));
    }

    void sendEndOfDealEvent(String dealId) {
        dealObservers.forEach(cdo -> cdo.onDealOver(dealId));
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

}
