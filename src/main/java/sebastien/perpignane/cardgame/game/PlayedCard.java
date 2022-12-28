package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.card.Card;
import sebastien.perpignane.cardgame.player.Player;

import java.util.Objects;

public class PlayedCard<P extends Player<?, ?>, C extends Card> {
    private final C card;
    private final P player;

    public PlayedCard(P player, C card) {

        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(card, "card cannot be null");

        this.card = card;
        this.player = player;
    }

    public C card() {
        return card;
    }

    public P player() {
        return player;
    }

    @Override
    public String toString() {
        return "PlayedCard[" +
                "player=" + player + ", " +
                "card=" + card + ']';
    }

}

