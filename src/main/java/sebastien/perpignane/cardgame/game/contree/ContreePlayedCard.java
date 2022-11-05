package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.game.GenericPlayedCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Objects;

final class ContreePlayedCard extends GenericPlayedCard<ContreePlayer, ContreeCard> {
    private final ContreePlayer player;
    private final ContreeCard card;

    public ContreePlayedCard(ContreePlayer player, ContreeCard card) {
        super(player, card);
        if (player.getTeam().isEmpty()) {
            throw new IllegalArgumentException("player must be part of a team");
        }
        this.player = player;
        this.card = card;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ContreePlayedCard) obj;
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
