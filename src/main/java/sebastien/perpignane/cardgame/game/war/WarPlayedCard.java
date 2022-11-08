package sebastien.perpignane.cardgame.game.war;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.PlayedCard;
import sebastien.perpignane.cardgame.player.war.AbstractWarPlayer;

public final class WarPlayedCard extends PlayedCard<AbstractWarPlayer, ClassicalCard> {

    public WarPlayedCard(AbstractWarPlayer player, ClassicalCard card) {
        super(player, card);
    }

}
