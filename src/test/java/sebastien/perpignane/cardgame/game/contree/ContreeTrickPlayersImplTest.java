package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
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

        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer()).isPresent();
        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer().get()).isSameAs(player1);

    }

    @DisplayName("After call to goToNextPlayer, current player must be player2")
    @Test
    public void testCurrentPlayerAfterCallToNext() {
        trickPlayers.gotToNextPlayer();
        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer()).isPresent();
        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer().get()).isSameAs(player2);
    }

    @DisplayName("After four calls to next, current player is player1")
    @Test
    public void testCurrentPlayerAfterFourCallsToNext() {
        trickPlayers.gotToNextPlayer();
        trickPlayers.gotToNextPlayer();
        trickPlayers.gotToNextPlayer();
        trickPlayers.gotToNextPlayer();

        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer()).isPresent();
        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer().get()).isSameAs(player1);
    }

    @DisplayName("The expected current player is notified when notifyCurrentPlayerTurn is called")
    @Test
    public void testNotifyCurrentPlayer() {

        boolean[] calledPlayers = {false, false, false, false};

        boolean[] expectedCalledPlayers = {false, false, false, false};

        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer()).isPresent();
        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer().get()).isSameAs(player1);

        for (int testedPlayerIndex = 0 ; testedPlayerIndex < 4 ; testedPlayerIndex++) {
            expectedCalledPlayers[testedPlayerIndex] = true;

            mockOnPlayerTurnCall(testedPlayerIndex, calledPlayers);
            trickPlayers.notifyCurrentPlayerTurn(Set.of(ClassicalCard.ACE_SPADE));
            assertThat(calledPlayers).isEqualTo(expectedCalledPlayers);
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

        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer()).isPresent();
        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer().get()).isEqualTo(player1);
    }

    @DisplayName("When setting second current trick, currentPlayer is the winner of the first trick")
    @Test
    public void testGetCurrentPlayerWhenSecondCurrentTrickSet() {

        ContreeTrick firstTrick = mock(ContreeTrick.class);
        when(firstTrick.getWinner()).thenReturn(Optional.ofNullable(player4));

        ContreeTrick secondTrick = mock(ContreeTrick.class);

        trickPlayers.setCurrentTrick(firstTrick);


        trickPlayers.setCurrentTrick(secondTrick);

        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer().orElseThrow()).isSameAs(player4);

        trickPlayers.gotToNextPlayer();
        assertThat(trickPlayers.getCurrentPlayerSlot().getPlayer().orElseThrow()).isSameAs(player1);

    }

}
