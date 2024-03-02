package sebastien.perpignane.cardgame.player.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PlayerMessageTest {
    @DisplayName("If message type requires not provided allowed cards, exception is thrown")
    @Test
    void testWhenTypeRequiresNotProvidedAllowedCards() {

        var messageType = MessageType.PLAY;

        assertThat(messageType.isRequiresAllowedCards()).isTrue();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new PlayerMessage(messageType));

    }

    @DisplayName("If message type requires allowed bids but they're not provided, exception is thrown")
    @Test
    void testWhenTypeRequiresNotProvidedAllowedBids() {

        var messageType = MessageType.BID;

        assertThat(messageType.isRequiresAllowedBidValues()).isTrue();
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> new PlayerMessage(messageType));

    }

    @DisplayName("onlyOneAllowedCard returns true when only one card is allowed")
    @Test
    void testOnlyOneAllowedCard_oneCard() {

        var playerMessage = new PlayerMessage(MessageType.PLAY, List.of(ClassicalCard.ACE_SPADE), null, null);

        assertThat(playerMessage.onlyOneAllowedCard()).isTrue();
    }

    @DisplayName("onlyOneAllowedCard returns false when multiple cards allowed")
    @Test
    void testOnlyOneAllowedCard_multipleCards() {

        var playerMessage = new PlayerMessage(MessageType.PLAY, List.of(ClassicalCard.ACE_SPADE, ClassicalCard.JACK_SPADE), null, null);

        assertThat(playerMessage.onlyOneAllowedCard()).isFalse();
    }

}