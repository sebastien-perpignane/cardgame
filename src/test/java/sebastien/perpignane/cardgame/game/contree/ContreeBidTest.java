package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;

public class ContreeBidTest {

    @DisplayName("Invalid bid if the bid is valued and no card suit is provided")
    @Test
    public void testInvalidBidWhenValuedBidWithNullCardSuit() {

        ContreePlayer biddingPlayer = mock(ContreePlayer.class);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
            () -> new ContreeBid(biddingPlayer, ContreeBidValue.EIGHTY, null)
        );

    }

    @DisplayName("Invalid bid if the bid is valued and NONE card suit is provided")
    @Test
    public void testInvalidBidWhenValuedBidWithNONECardSuit() {

        ContreePlayer biddingPlayer = mock(ContreePlayer.class);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
                () -> new ContreeBid(biddingPlayer, ContreeBidValue.EIGHTY, CardSuit.NONE)
        );

    }

    @Test
    void testIsDouble_true() {
        ContreePlayer player = mock(ContreePlayer.class);
        ContreeBid bid = new ContreeBid(player, ContreeBidValue.DOUBLE, null);

        assertThat(bid.isDouble()).isTrue();
        assertThat(bid.isRedouble()).isFalse();
    }

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
        assertThat(allowedSuits).doesNotContain(CardSuit.NONE);
    }

}
