package sebastien.perpignane.cardgame.player.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerMessageTest {
    @DisplayName("If message type requires not provided allowed cards, exception is thrown")
    @Test
    public void testWhenTypeRequiresNotProvidedAllowedCards() {

        var messageType = MessageType.PLAY;

        assertTrue(messageType.isRequiresAllowedCards());
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerMessage(messageType)
        );

    }

    @DisplayName("If message type requires not provided allowed cards, exception is thrown")
    @Test
    public void testWhenTypeRequiresNotProvidedAllowedBids() {

        var messageType = MessageType.BID;

        assertTrue(messageType.isRequiresAllowedBidValues());
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerMessage(messageType)
        );

    }

}