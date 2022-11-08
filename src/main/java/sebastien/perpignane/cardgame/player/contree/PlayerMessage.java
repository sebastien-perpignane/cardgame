package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;

import java.util.Collection;

public record PlayerMessage(
        AbstractLocalThreadContreePlayer.MessageType messageType,
        Collection<ClassicalCard> allowedCards,
        Collection<ContreeBidValue> allowedBidValues) {
}
