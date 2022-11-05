package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContreeGameTest extends TestCasesManagingPlayers {

    private ContreeDeals deals;

    private ContreeGame game;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    public void setUp() {

        ContreeGamePlayers gamePlayers = mock(ContreeGamePlayers.class);
        deals = mock(ContreeDeals.class);
        ContreeGameEventSender eventSender = mock(ContreeGameEventSender.class);

        when(gamePlayers.isFull()).thenAnswer(AdditionalAnswers.returnsElementsOf(List.of(false, false, false, true)));

        game = new ContreeGame(gamePlayers, deals, eventSender);
    }

    private void makeTheGameStart() {
        players.forEach(game::joinGame);
    }

    @DisplayName("A game can be started when 4 players joined the game")
    @Test
    public void testStartGameWith4PlayersSucceeds() {
        makeTheGameStart();
        assertFalse(game.isWaitingForPlayers());
        assertTrue(game.isStarted());
    }

    @DisplayName("A game is waiting for players until 4 players joined it")
    @Test
    public void testGameWithoutPlayersHasExpectedState() {

        game.joinGame(player1);
        assertTrue(game.isWaitingForPlayers());

        game.joinGame(player2);
        assertTrue(game.isWaitingForPlayers());

        game.joinGame(player3);
        assertTrue(game.isWaitingForPlayers());


        game.joinGame(player4);

        assertFalse(game.isWaitingForPlayers());
        assertTrue(game.isStarted());
    }

    @DisplayName("Winner is not available if a game is not over")
    @Test
    public void testGetWinnerOnNotOverGame() {

        assertFalse(game.isOver());
        assertTrue(game.getWinner().isEmpty());

        makeTheGameStart();

        assertFalse(game.isOver());
        assertTrue(game.getWinner().isEmpty());

    }

    @DisplayName("Exception if a player tries to play a card on an over game")
    @Test
    public void testCannotPlayCardIfGameIsOver() {
        makeTheGameOver();
        assertTrue(game.isOver());

        assertThrows(
                RuntimeException.class,
                () -> game.playCard(player2, ClassicalCard.ACE_SPADE)
        );
    }

    @DisplayName("Exception if a player tries to bid on an over game")
    @Test
    public void testCannotBidIfGameIsOver() {
        makeTheGameOver();
        assertTrue(game.isOver());

        assertThrows(
                RuntimeException.class,
                () -> game.placeBid(player2, ContreeBidValue.PASS, null)
        );
    }

    @DisplayName("No exception if a player tries to bid on ongoing game")
    @Test
    public void testCanBidIfGameIsOver() {
        assertFalse(game.isOver());
        game.placeBid(player2, ContreeBidValue.PASS, null);
    }

    @DisplayName("Exception if a player tries to join an over game")
    @Test
    public void testCannotJoinIfGameIsOver() {
        makeTheGameOver();
        assertTrue(game.isOver());

        ContreePlayer newPlayer = mock(ContreePlayer.class);
        assertThrows(
                RuntimeException.class,
                () -> game.joinGame(newPlayer)
        );
    }

    @DisplayName("Exception if a player tries to join an over game")
    @Test
    public void testCannotRegisterObserverIfGameIsOver() {
        makeTheGameOver();
        assertTrue(game.isOver());

        GameObserver observer = mock(GameObserver.class);

        assertThrows(
                RuntimeException.class,
                () -> game.registerAsGameObserver(observer)
        );
    }

    private void makeTheGameOver() {
        makeTheGameStart();
        when(deals.isMaximumScoreReached()).thenReturn(true);
        game.playCard(player1, ClassicalCard.JACK_HEART);
    }



}
