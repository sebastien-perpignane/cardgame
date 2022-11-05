package sebastien.perpignane.cardgame.game.war;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.GenericPlayedCard;
import sebastien.perpignane.cardgame.player.Player;

import java.util.Objects;

public final class WarPlayedCard extends GenericPlayedCard<Player, ClassicalCard> {
    private final Player player;
    private final ClassicalCard card;

    public WarPlayedCard(Player player, ClassicalCard card) {
        super(player, card);
        this.player = player;
        this.card = card;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (WarPlayedCard) obj;
        return Objects.equals(this.player, that.player) &&
                Objects.equals(this.card, that.card);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, card);
    }

    @Override
    public String toString() {
        return "PlayedCard[" +
                "player=" + player + ", " +
                "card=" + card + ']';
    }

}
