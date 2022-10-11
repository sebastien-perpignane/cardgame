package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGameEventSender;
import sebastien.perpignane.cardgame.game.CardGameObserver;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.player.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ContreeGameEventSender extends AbstractGameEventSender {

    private final List<ContreeDealObserver> dealObservers = new CopyOnWriteArrayList<>();
    private final List<ContreeTrickObserver> trickObservers = new CopyOnWriteArrayList<>();

    public ContreeGameEventSender(Collection<CardGameObserver> observers) {
        observers.forEach(o -> {
            if (o instanceof ContreeTrickObserver cto) {
                trickObservers.add(cto);
            }
            if (o instanceof GameObserver go) {
                gameObservers.add(go);
            }
            if (o instanceof ContreeDealObserver cdo) {
                dealObservers.add(cdo);
            }
        });
    }

    public ContreeGameEventSender(CardGameObserver... observers) {
        this(Arrays.asList(observers));
    }

    void sendEndOfGameEvent(ContreeGame contreeGame) {

        gameObservers.forEach(go -> go.onEndOfGame(contreeGame));

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

    // FIXME Manage this event in trick management code
    void sendTrumpedTrickEvent(String trickId) {
        trickObservers.forEach(to -> to.onTrumpedTrick(trickId));
    }

}
