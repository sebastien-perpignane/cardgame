package sebastien.perpignane.cardgame.game.war;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.PlayedCard;
import sebastien.perpignane.cardgame.player.war.WarPlayer;

public final class WarPlayedCard extends PlayedCard<WarPlayer, ClassicalCard> {

    public WarPlayedCard(WarPlayer player, ClassicalCard card) {
        super(player, card);
    }

}
