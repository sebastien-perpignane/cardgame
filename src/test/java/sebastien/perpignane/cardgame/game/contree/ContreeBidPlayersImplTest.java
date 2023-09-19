package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ContreeBidPlayersImplTest extends TestCasesManagingPlayers {

    private ContreeBidPlayers bidPlayers;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {

        ContreeDealPlayers dealPlayers = mock(ContreeDealPlayers.class);
        when(dealPlayers.getCurrentDealPlayers()).thenReturn(players);
        when(dealPlayers.getCurrentDealPlayerSlots()).thenReturn(playerSlots);

        bidPlayers = new ContreeBidPlayersImpl(dealPlayers);

    }

    @DisplayName("Current bidder is updated after each call to goToNextBidder")
    @Test
    void testGoToNextBidder() {

        bidPlayers.goToNextBidder();
        assertThat(bidPlayers.getCurrentBidderSlot().getPlayer())
                .isPresent()
                .containsSame(player2);

        bidPlayers.goToNextBidder();
        assertThat(bidPlayers.getCurrentBidderSlot().getPlayer())
                .isPresent()
                .contains(player3);

        bidPlayers.goToNextBidder();
        assertThat(bidPlayers.getCurrentBidderSlot().getPlayer())
                .isPresent()
                .containsSame(player4);

        bidPlayers.goToNextBidder();
        assertThat(bidPlayers.getCurrentBidderSlot().getPlayer())
                .isPresent()
                .containsSame(player1);

    }

    @DisplayName("After construction, current bidder is the player1 in the player list")
    @Test
    void testGetInitialCurrentBidder() {
        assertThat(bidPlayers.getCurrentBidderSlot().getPlayer())
                .isPresent()
                .containsSame(player1);
    }

    @DisplayName("When currentBidderTurnToBid is called and currentBidder is player1, onPlayerTurnToBid is called on player1")
    @Test
    void testOnCurrentBidderTurnToBid_player1() {

        bidPlayers.onCurrentBidderTurnToBid(Set.of(ContreeBidValue.PASS));

        verify(player1).onPlayerTurnToBid(anySet());

    }

    @DisplayName("When currentBidderTurnToBid is called and currentBidder is player2, onPlayerTurnToBid is called on player2")
    @Test
    void testOnCurrentBidderTurnToBid_player2() {

        bidPlayers.goToNextBidder();
        bidPlayers.onCurrentBidderTurnToBid(Set.of(ContreeBidValue.PASS));

        verify(player2).onPlayerTurnToBid(anySet());

    }

}