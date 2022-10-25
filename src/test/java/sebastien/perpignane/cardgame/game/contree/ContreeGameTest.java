package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.GameObserver;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// TODO [Unit test]

class ContreeGameTest extends TestCasesManagingPlayers {

    private ContreeGame game;

    private ContreeGame startedGame;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {
        game = new ContreeGame();
        startedGame = new ContreeGame();
        players.forEach(startedGame::joinGame);
        startedGame.startGame();
    }

    @DisplayName("A game can be started when 4 players joined the game")
    @Test
    void testStartGameWith4PlayersSucceeds() {
        game = new ContreeGame();
        players.forEach(game::joinGame);
        assertFalse(game.isWaitingForPlayers());
        game.startGame();
        assertTrue(game.isStarted());
    }

    @DisplayName("A game can be started when 4 players did not join the game")
    @Test
    void testStartGameWithMissingPlayersFails() {
        game = new ContreeGame();
        assertTrue(game.isWaitingForPlayers());
        assertThrows(
            RuntimeException.class,
            game::startGame
        );
    }

    @DisplayName("A registered game observer received game events")
    @Test
    void testEventsIsReceivedByRegisteredGameObserver() {
        GameObserver observer = mock(GameObserver.class);
        final boolean[] startedFlag = {false};
        doAnswer(invocationOnMock -> {
            startedFlag[0] = true;
            return null;
        }).when(observer).onStateUpdated(any(), any());

        game.registerAsGameObserver(observer);
        players.forEach(game::joinGame);
        game.startGame();

        assertTrue(startedFlag[0]);
    }

    @DisplayName("Winner is not available if a game is not over")
    @Test
    void testGetWinnerOnNotOverGame() {

        assertFalse(game.isOver());
        assertTrue(game.getWinner().isEmpty());

        assertFalse(startedGame.isOver());
        assertTrue(startedGame.getWinner().isEmpty());

    }

    @Test
    void testGetNbDealsOnStartedGame() {

        assertFalse(startedGame.isOver());
        assertEquals(1, startedGame.getNbDeals());

        startedGame.placeBid(player1, ContreeBidValue.EIGHTY, CardSuit.HEARTS);
        startedGame.placeBid(player2, ContreeBidValue.NONE, null);
        startedGame.placeBid(player3, ContreeBidValue.NONE, null);
        startedGame.placeBid(player4, ContreeBidValue.NONE, null);
        assertEquals(1, startedGame.getNbDeals());


    }

}
