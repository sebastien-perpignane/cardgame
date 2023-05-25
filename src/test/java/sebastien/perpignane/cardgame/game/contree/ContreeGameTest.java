package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.*;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.game.GameStatus;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

class ContreeGameTest extends TestCasesManagingPlayers {

    private ContreeGamePlayers gamePlayers;

    private ContreeDeals deals;

    private ContreeGame game;

    @BeforeAll
    static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {

        ContreeGameScore score = mock(ContreeGameScore.class);
        gamePlayers = mock(ContreeGamePlayers.class);
        deals = mock(ContreeDeals.class);

        when(deals.getGameScore()).thenReturn(score);

        ContreeGameEventSender eventSender = mock(ContreeGameEventSender.class);

        when(gamePlayers.joinGame(any())).thenReturn(new JoinGameResult(0, Optional.empty()));

        when(gamePlayers.isFull()).thenAnswer(
            AdditionalAnswers.returnsElementsOf(
                List.of(
                        false,
                        false,
                        false,
                        true
                )
            )
        );




        game = new ContreeGame(gamePlayers, deals, eventSender);
    }

    private void makeTheGameStart() {
        players.forEach(game::joinGame);
    }

    @DisplayName("A game can be started when 4 players joined the game")
    @Test
    void testStartGameWith4PlayersSucceeds() {
        makeTheGameStart();
        assertThat(game.isWaitingForPlayers()).isFalse();
        assertThat(game.isStarted()).isTrue();
    }

    @DisplayName("A game is waiting for players until gamePlayers is full")
    @Test
    void testGameWithoutPlayersHasExpectedState() {

        game.joinGame(player1);
        assertThat(game.isWaitingForPlayers()).isTrue();

        game.joinGame(player2);
        assertThat(game.isWaitingForPlayers()).isTrue();

        game.joinGame(player3);
        assertThat(game.isWaitingForPlayers()).isTrue();

        game.joinGame(player4);

        assertThat(game.isWaitingForPlayers()).isFalse();
        assertThat(game.isStarted()).isTrue();
    }

    @DisplayName("Winner is not available if a game is not over")
    @Test
    void testGetWinnerOnNotOverGame() {

        assertThat(game.isOver()).isFalse();
        assertThat(game.getWinner()).isEmpty();

        makeTheGameStart();

        assertThat(game.isOver()).isFalse();
        assertThat(game.getWinner()).isEmpty();

    }

    @DisplayName("Exception if a player tries to play a card on an over game")
    @Test
    void testCannotPlayCardIfGameIsOver() {
        makeTheGameOver();
        assertThat(game.isOver()).isTrue();

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> game.playCard(player2, ClassicalCard.ACE_SPADE));
    }

    @DisplayName("Exception if a player tries to bid on an over game")
    @Test
    void testCannotBidIfGameIsOver() {
        makeTheGameOver();
        assertThat(game.isOver()).isTrue();

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> game.placeBid(player2, ContreeBidValue.PASS, null));
    }

    @DisplayName("No exception if a player tries to bid on ongoing game")
    @Test
    void testCanBidIfGameIsOver() {
        assertThat(game.isOver()).isFalse();
        game.placeBid(player2, ContreeBidValue.PASS, null);
    }

    @DisplayName("Exception if a player tries to join an over game")
    @Test
    void testCannotJoinIfGameIsOver() {
        makeTheGameOver();
        assertThat(game.isOver()).isTrue();

        ContreePlayer newPlayer = mock(ContreePlayer.class);
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> game.joinGame(newPlayer));
    }

    @DisplayName("Exception if a player tries to join an over game")
    @Test
    void testCannotRegisterObserverIfGameIsOver() {
        makeTheGameOver();
        assertThat(game.isOver()).isTrue();

        GameObserver observer = mock(GameObserver.class);

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> game.registerAsGameObserver(observer));
    }

    private void makeTheGameOver() {
        makeTheGameStart();
        when(deals.isMaximumScoreReached()).thenReturn(true);
        game.playCard(player1, ClassicalCard.JACK_HEART);
    }

    @DisplayName("Leaving a not started game does not trigger currentBidder and currentPlayer replacement but newPlayer get the leaver hand")
    @Test
    void testLeaveNotStartedGame() {

        LeaveGameFlags flags = new LeaveGameFlags();

        var leaver = player1;
        ContreePlayer newPlayer = mock(ContreePlayer.class);
        when(leaver.isBot()).thenReturn(false);
        when(leaver.getHand()).thenReturn(Set.of(ClassicalCard.ACE_SPADE, ClassicalCard.TEN_SPADE));
        when(gamePlayers.leaveGameAndReplaceWithBotPlayer(leaver)).thenReturn(newPlayer);

        doAnswer(invocationOnMock -> {
            flags.hand = invocationOnMock.getArgument(0);
            return null;
        }).when(newPlayer).receiveHand(anySet());

        doAnswer((invocationOnMock -> {
            flags.updatedGame = invocationOnMock.getArgument(0);
            return null;
        })).when(newPlayer).setGame(any());

        doAnswer((invocationOnMock -> {
            flags.playerGameStartedEvent = true;
            return null;
        })).when(newPlayer).onGameStarted();

        game.joinGame(leaver);

        game.leaveGame(leaver);

        assertThat(flags.hand).isEqualTo(leaver.getHand());
        assertThat(flags.updatedGame).isSameAs(game);
        assertThat(flags.playerGameStartedEvent).isFalse();

    }

    @DisplayName("Leaving a started game does trigger currentBidder and currentPlayer replacement, newPlayer get the leaver hand")
    @Test
    void testLeaveStartedGame() {

        LeaveGameFlags flags = new LeaveGameFlags();

        makeTheGameStart();

        var leaver = player1;
        ContreePlayer newPlayer = mock(ContreePlayer.class);
        when(leaver.isBot()).thenReturn(false);
        when(leaver.getHand()).thenReturn(Set.of(ClassicalCard.ACE_SPADE, ClassicalCard.TEN_SPADE));
        when(gamePlayers.leaveGameAndReplaceWithBotPlayer(leaver)).thenReturn(newPlayer);

        doAnswer(invocationOnMock -> {
            flags.hand = invocationOnMock.getArgument(0);
            return null;
        }).when(newPlayer).receiveHand(anySet());

        doAnswer((invocationOnMock -> {
            flags.updatedGame = invocationOnMock.getArgument(0);
            return null;
        })).when(newPlayer).setGame(any());

        doAnswer((invocationOnMock -> {
            flags.playerGameStartedEvent = true;
            return null;
        })).when(newPlayer).onGameStarted();

        game.leaveGame(leaver);

        assertThat(flags.hand).isEqualTo(leaver.getHand());
        assertThat(flags.updatedGame).isSameAs(game);
        assertThat(flags.playerGameStartedEvent).isTrue();

    }

    @DisplayName("Leaving an over game does not trigger anything")
    @Test
    void testLeaveOverGame() {

        LeaveGameFlags flags = new LeaveGameFlags();

        makeTheGameOver();

        var leaver = player1;
        ContreePlayer newPlayer = mock(ContreePlayer.class);
        when(leaver.isBot()).thenReturn(false);
        when(leaver.getHand()).thenReturn(Set.of(ClassicalCard.ACE_SPADE, ClassicalCard.TEN_SPADE));
        when(gamePlayers.leaveGameAndReplaceWithBotPlayer(leaver)).thenReturn(newPlayer);

        doAnswer(invocationOnMock -> {
            flags.hand = invocationOnMock.getArgument(0);
            return null;
        }).when(newPlayer).receiveHand(anySet());

        doAnswer((invocationOnMock -> {
            flags.updatedGame = invocationOnMock.getArgument(0);
            return null;
        })).when(newPlayer).setGame(any());

        doAnswer((invocationOnMock -> {
            flags.playerGameStartedEvent = true;
            return null;
        })).when(newPlayer).onGameStarted();

        game.leaveGame(leaver);

        assertThat(flags.hand).isNotEqualTo(leaver.getHand());
        assertThat(flags.updatedGame).isNull();
        assertThat(flags.playerGameStartedEvent).isFalse();

    }

    @Test
    @DisplayName("on a just initialized game, toState() returns a ContreeGameState object with expected data")
    void testGameToState_game_initialized() {
        ContreeGameState gameState = game.toState();
        assertThat(gameState).isNotNull();
        assertThat(gameState.gameId()).isNotNull();
        assertThat(gameState.status()).isSameAs(GameStatus.WAITING_FOR_PLAYERS);
        gameState.players().forEach(Assertions::assertNull);
    }

    @Test
    @DisplayName("on a started game, toState() returns a ContreeGameState object with expected data")
    void testGameToState_game_started() {
        makeTheGameStart();

        ContreeGameState gameState = game.toState();
        assertThat(gameState).isNotNull();
        assertThat(gameState.gameId()).isNotNull();
        assertThat(gameState.status()).isSameAs(GameStatus.STARTED);
        gameState.players().forEach(Assertions::assertNotNull);
    }

    @Test
    @DisplayName("on a over game, toState() returns a ContreeGameState object with expected data")
    void testGameToState_game_over() {
        makeTheGameOver();

        ContreeGameState gameState = game.toState();
        assertThat(gameState).isNotNull();
        assertThat(gameState.gameId()).isNotNull();
        assertThat(gameState.status()).isSameAs(GameStatus.OVER);
        gameState.players().forEach(Assertions::assertNotNull);
    }

}

class LeaveGameFlags {

    boolean playerGameStartedEvent;

    Set<ClassicalCard> hand;

    ContreeGame updatedGame;

}
