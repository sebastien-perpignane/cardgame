package sebastien.perpignane.cardgame.game.war;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGameEventSender;
import sebastien.perpignane.cardgame.game.CardGameObserver;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.game.Trick;
import sebastien.perpignane.cardgame.player.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class WarGameEventSender extends AbstractGameEventSender {

    private final List<WarTrickObserver> trickObservers = new CopyOnWriteArrayList<>();

    WarGameEventSender(Collection<CardGameObserver> observers) {

        observers.forEach(o -> {
            if (o instanceof WarTrickObserver) {

                trickObservers.add((WarTrickObserver) o);
            }
            if (o instanceof GameObserver) {
                gameObservers.add((GameObserver) o);
            }
        });

    }

    WarGameEventSender(CardGameObserver... observers) {
        this(Arrays.asList(observers));
    }

    void registerAsTrickObserver(WarTrickObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("observer cannot be null");
        }
        trickObservers.add(observer);
    }

    void sendPlayedCardEvent(Player<?, ?> player, ClassicalCard card) {
        gameObservers.forEach(observer -> observer.onCardPlayed(player, card));
    }

    void sendEndOfGameEvent(WarGame warGame) {
        gameObservers.forEach(go -> go.onEndOfGame(warGame));
    }

    void sendWonTrickEvent(Trick trick) {
        for (GameObserver observer : gameObservers) {
            observer.onWonTrick(trick);
        }
    }

    void sendWarEvent(List<WarPlayedCard> cardsTriggeringWar) {
        trickObservers.forEach(o -> o.onWar(cardsTriggeringWar));
    }
}