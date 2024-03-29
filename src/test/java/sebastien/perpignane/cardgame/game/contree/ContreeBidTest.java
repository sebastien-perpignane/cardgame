package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

class ContreeBidTest {

    @DisplayName("Invalid bid if the bid is valued and no card suit is provided")
    @Test
    void testInvalidBidWhenValuedBidWithNullCardSuit() {

        ContreePlayer biddingPlayer = mock(ContreePlayer.class);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(
                () -> new ContreeBid(biddingPlayer, ContreeBidValue.EIGHTY, null)
            );

    }

    @DisplayName("Invalid bid if the bid is valued and NONE card suit is provided")
    @Test
    void testInvalidBidWhenValuedBidWithNONECardSuit() {

        ContreePlayer biddingPlayer = mock(ContreePlayer.class);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(
                () -> new ContreeBid(biddingPlayer, ContreeBidValue.EIGHTY, CardSuit.NONE)
            );

    }

    @DisplayName("ContreeBid#isDouble returns true when bid value is DOUBLE")
    @Test
    void testIsDouble_true() {
        ContreePlayer player = mock(ContreePlayer.class);
        ContreeBid bid = new ContreeBid(player, ContreeBidValue.DOUBLE, null);

        assertThat(bid.isDouble()).isTrue();
        assertThat(bid.isRedouble()).isFalse();
    }

    @DisplayName("ContreeBid#isRedouble returns true when bid value is not REDOUBLE")
    @Test
    void testIsDouble_false() {
        ContreePlayer player = mock(ContreePlayer.class);
        ContreeBid bid = new ContreeBid(player, ContreeBidValue.REDOUBLE, null);

        assertThat(bid.isDouble()).isFalse();
        assertThat(bid.isRedouble()).isTrue();
    }

    @Test
    void testAllowedSuitsForValuedBids() {
        var allowedSuits = ContreeBid.allowedCardSuitsForValuedBids();
        assertThat(allowedSuits)
                .isNotEmpty()
                .doesNotContain(CardSuit.NONE);
    }

}
