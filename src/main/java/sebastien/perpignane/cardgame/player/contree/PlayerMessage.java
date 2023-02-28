package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;

import java.util.Collection;

public record PlayerMessage(
        MessageType messageType,
        Collection<ClassicalCard> allowedCards,
        Collection<ContreeBidValue> allowedBidValues,
        ContreePlayerStatus newStatus) {

    public PlayerMessage {

        if (messageType.isRequiresAllowedCards() && allowedCards == null) {
            throw new IllegalArgumentException(String.format("For the message type %s, allowedCards parameter is required", messageType));
        }

        if (messageType.isRequiresAllowedBidValues() && allowedBidValues == null) {
            throw new IllegalArgumentException(String.format("For the message type %s, allowedBidValues parameter is required", messageType));
        }

    }

    public PlayerMessage(MessageType messageType) {
        this(messageType, null, null, null);
    }
}
