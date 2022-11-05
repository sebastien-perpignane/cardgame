package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

        bidPlayers = new ContreeBidPlayersImpl(dealPlayers);

    }

    @DisplayName("Current bidder is updated after each call to goToNextBidder")
    @Test
    void testGoToNextBidder() {

        bidPlayers.goToNextBidder();
        assertSame( player2, bidPlayers.getCurrentBidder() );

        bidPlayers.goToNextBidder();
        assertSame( player3, bidPlayers.getCurrentBidder() );

        bidPlayers.goToNextBidder();
        assertSame( player4, bidPlayers.getCurrentBidder() );

        bidPlayers.goToNextBidder();
        assertSame( player1, bidPlayers.getCurrentBidder() );

    }

    @DisplayName("After construction, current bidder is the player1 in the player list")
    @Test
    void testGetInitialCurrentBidder() {
        assertSame( player1, bidPlayers.getCurrentBidder() );
    }

    @Test
    void testOnCurrentBidderTurnToBid_player1() {

        boolean[] called = {false};

        doAnswer(invocationOnMock -> {
            called[0] = true;
            return null;
        }).when(player1).onPlayerTurnToBid(anySet());

        bidPlayers.onCurrentBidderTurnToBid(Set.of(ContreeBidValue.PASS));

        assertTrue(called[0]);

    }

    @Test
    void testOnCurrentBidderTurnToBid_player2() {

        boolean[] called = {false};

        doAnswer(invocationOnMock -> {
            called[0] = true;
            return null;
        }).when(player2).onPlayerTurnToBid(anySet());

        bidPlayers.goToNextBidder();
        bidPlayers.onCurrentBidderTurnToBid(Set.of(ContreeBidValue.PASS));

        assertTrue(called[0]);

    }

}