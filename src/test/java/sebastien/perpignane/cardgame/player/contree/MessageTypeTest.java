package sebastien.perpignane.cardgame.player.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class MessageTypeTest {

    @DisplayName("Enum member with default constructor does not required allowed values nor allowed cards")
    @Test
    public void testDefaultConstructor() {
        assertFalse(MessageType.GAME_OVER.isRequiresAllowedBidValues());
        assertFalse(MessageType.GAME_OVER.isRequiresAllowedCards());
    }

}