package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContreeTrickPlayersImplTest extends TestCasesManagingPlayers {

    private ContreeTrickPlayers trickPlayers;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    public void setUp() {

        ContreeDealPlayers dealPlayers = mock(ContreeDealPlayers.class);
        when(dealPlayers.getCurrentDealPlayers()).thenReturn(players);
        when(dealPlayers.getCurrentDealPlayerSlots()).thenReturn(playerSlots);
        when(dealPlayers.indexOf(player1)).thenReturn(0);
        when(dealPlayers.indexOf(player2)).thenReturn(1);
        when(dealPlayers.indexOf(player3)).thenReturn(2);
        when(dealPlayers.indexOf(player4)).thenReturn(3);


        trickPlayers = new ContreeTrickPlayersImpl(dealPlayers);

    }

    @DisplayName("After construction, current player must be player1")
    @Test
    public void testCurrentPlayerAfterConstruction() {

        assertTrue(trickPlayers.getCurrentPlayerSlot().getPlayer().isPresent());
        assertSame(player1, trickPlayers.getCurrentPlayerSlot().getPlayer().get());

    }

    @DisplayName("After call to goToNextPlayer, current player must be player2")
    @Test
    public void testCurrentPlayerAfterCallToNext() {
        trickPlayers.gotToNextPlayer();
        assertTrue(trickPlayers.getCurrentPlayerSlot().getPlayer().isPresent());
        assertSame(player2, trickPlayers.getCurrentPlayerSlot().getPlayer().get());
    }

    @DisplayName("After four calls to next, current player is player1")
    @Test
    public void testCurrentPlayerAfterFourCallsToNext() {
        trickPlayers.gotToNextPlayer();
        trickPlayers.gotToNextPlayer();
        trickPlayers.gotToNextPlayer();
        trickPlayers.gotToNextPlayer();

        assertTrue(trickPlayers.getCurrentPlayerSlot().getPlayer().isPresent());
        assertSame(player1, trickPlayers.getCurrentPlayerSlot().getPlayer().get());
    }

    @DisplayName("The expected current player is notified when notifyCurrentPlayerTurn is called")
    @Test
    public void testNotifyCurrentPlayer() {

        boolean[] calledPlayers = {false, false, false, false};

        boolean[] expectedCalledPlayers = {false, false, false, false};

        assertTrue(trickPlayers.getCurrentPlayerSlot().getPlayer().isPresent());
        assertSame(player1, trickPlayers.getCurrentPlayerSlot().getPlayer().get());

        for (int testedPlayerIndex = 0 ; testedPlayerIndex < 4 ; testedPlayerIndex++) {
            expectedCalledPlayers[testedPlayerIndex] = true;

            mockOnPlayerTurnCall(testedPlayerIndex, calledPlayers);
            trickPlayers.notifyCurrentPlayerTurn(Set.of(ClassicalCard.ACE_SPADE));
            assertArrayEquals(expectedCalledPlayers, calledPlayers);
            trickPlayers.gotToNextPlayer();
        }

    }

    private void mockOnPlayerTurnCall(int playerIndex, boolean[] calledPlayerFlags) {
        doAnswer((invocationOnMock) -> {
            calledPlayerFlags[playerIndex] = true;
            return null;
        }).when(players.get(playerIndex)).onPlayerTurn(any());
    }

    @DisplayName("When setting first current trick, currentPlayer is player 1")
    @Test
    public void testGetCurrentPlayerWhenFirstCurrentTrickSet() {
        ContreeTrick firstTrick = mock(ContreeTrick.class);

        trickPlayers.setCurrentTrick(firstTrick);

        assertTrue(trickPlayers.getCurrentPlayerSlot().getPlayer().isPresent());
        assertEquals(player1, trickPlayers.getCurrentPlayerSlot().getPlayer().get());
    }

    @DisplayName("When setting second current trick, currentPlayer is the winner of the first trick")
    @Test
    public void testGetCurrentPlayerWhenSecondCurrentTrickSet() {

        ContreeTrick firstTrick = mock(ContreeTrick.class);
        when(firstTrick.getWinner()).thenReturn(Optional.ofNullable(player4));

        ContreeTrick secondTrick = mock(ContreeTrick.class);

        trickPlayers.setCurrentTrick(firstTrick);


        trickPlayers.setCurrentTrick(secondTrick);

        assertSame(player4, trickPlayers.getCurrentPlayerSlot().getPlayer().orElseThrow());

        trickPlayers.gotToNextPlayer();
        assertSame(player1, trickPlayers.getCurrentPlayerSlot().getPlayer().orElseThrow());

    }

}
