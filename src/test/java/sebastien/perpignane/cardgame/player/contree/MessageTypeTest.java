package sebastien.perpignane.cardgame.player.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageTypeTest {

    @DisplayName("Enum member with default constructor does not require allowed values nor allowed cards")
    @Test
    void testDefaultConstructor() {
        assertThat(MessageType.GAME_OVER.isRequiresAllowedBidValues()).isFalse();
        assertThat(MessageType.GAME_OVER.isRequiresAllowedCards()).isFalse();
    }

}