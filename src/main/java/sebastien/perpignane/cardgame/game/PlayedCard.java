package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.Player;

public record PlayedCard(Player player, ClassicalCard card) {
    public PlayedCard {
        if (player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }
        if (card == null) {
            throw new IllegalArgumentException("card cannot be null");
        }
    }
}
