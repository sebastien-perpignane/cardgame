package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class ContreeBidTest {

    @DisplayName("Invalid bid if the bid is valued and no card suit is provided")
    @Test
    public void testFirstBid_invalid_EIGHTY() {

        ContreePlayer biddingPlayer = mock(ContreePlayer.class);

        var e = assertThrows(IllegalArgumentException.class, () -> new ContreeBid(biddingPlayer, ContreeBidValue.EIGHTY, null));
        System.err.println(e.getMessage());

    }

}
