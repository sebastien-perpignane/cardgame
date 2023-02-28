package sebastien.perpignane.cardgame.player.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PlayerMessageTest {
    @DisplayName("If message type requires not provided allowed cards, exception is thrown")
    @Test
    public void testWhenTypeRequiresNotProvidedAllowedCards() {

        var messageType = MessageType.PLAY;

        assertThat(messageType.isRequiresAllowedCards()).isTrue();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new PlayerMessage(messageType));

    }

    @DisplayName("If message type requires not provided allowed cards, exception is thrown")
    @Test
    public void testWhenTypeRequiresNotProvidedAllowedBids() {

        var messageType = MessageType.BID;

        assertThat(messageType.isRequiresAllowedBidValues()).isTrue();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new PlayerMessage(messageType));

    }

}