package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.buildPlayers;

class ContreeTrickPlayersImplTest {

    private List<ContreePlayer> players;

    private ContreePlayer player1;
    private ContreePlayer player2;
    private ContreePlayer player3;
    private ContreePlayer player4;

    private ContreeTrickPlayers trickPlayers;

    @BeforeEach
    public void setUpTests() {

        players = buildPlayers();

        int playerIndex = 0;
        player1 = players.get(playerIndex++);
        player2 = players.get(playerIndex++);
        player3 = players.get(playerIndex++);
        player4 = players.get(playerIndex);

        ContreeDealPlayers dealPlayers = mock(ContreeDealPlayers.class);
        when(dealPlayers.getCurrentDealPlayers()).thenReturn(players);

        trickPlayers = new ContreeTrickPlayersImpl(dealPlayers);

    }

    @DisplayName("After construction, current player must be player1")
    @Test
    public void testCurrentPlayerAfterConstruction() {

        assertSame(player1, trickPlayers.getCurrentPlayer());

    }

    @DisplayName("After call to goToNextPlayer, current player must be player2")
    @Test
    public void testCurrentPlayerAfterCallToNext() {
        trickPlayers.gotToNextPlayer();
        assertSame(player2, trickPlayers.getCurrentPlayer());
    }

    @DisplayName("After four calls to next, current player is player1")
    @Test
    public void testCurrentPlayerAfterFourCallsToNext() {
        trickPlayers.gotToNextPlayer();
        trickPlayers.gotToNextPlayer();
        trickPlayers.gotToNextPlayer();
        trickPlayers.gotToNextPlayer();

        assertSame(player1, trickPlayers.getCurrentPlayer());
    }

    @DisplayName("The expected current player is notified when notifyCurrentPlayerTurn is called")
    @Test
    public void testNotifyCurrentPlayer() {

        boolean[] calledPlayers = {false, false, false, false};

        boolean[] expectedCalledPlayers = {false, false, false, false};

        assertSame(player1, trickPlayers.getCurrentPlayer());

        for (int testedPlayerIndex = 0 ; testedPlayerIndex < 4 ; testedPlayerIndex++) {
            expectedCalledPlayers[testedPlayerIndex] = true;

            mockOnPlayerTurnCall(testedPlayerIndex, calledPlayers);
            trickPlayers.notifyCurrentPlayerTurn(List.of(ClassicalCard.ACE_SPADE));
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

        assertEquals(player1, trickPlayers.getCurrentPlayer());
    }

    @DisplayName("When setting second current trick, currentPlayer is the winner of the first trick")
    @Test
    public void testGetCurrentPlayerWhenSecondCurrentTrickSet() {

        ContreeTrick firstTrick = mock(ContreeTrick.class);
        when(firstTrick.getWinner()).thenReturn(player4);

        ContreeTrick secondTrick = mock(ContreeTrick.class);


        trickPlayers.setCurrentTrick(firstTrick);
        trickPlayers.setCurrentTrick(secondTrick);

        assertSame(player4, trickPlayers.getCurrentPlayer());

        trickPlayers.gotToNextPlayer();
        assertSame(player1, trickPlayers.getCurrentPlayer());

    }

}
