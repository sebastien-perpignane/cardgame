package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContreeGamePlayersImplTest extends TestCasesManagingPlayers {

    private ContreeGamePlayers gamePlayers;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    public void setUp() {
        gamePlayers = new ContreeGamePlayersImpl();
    }

    @DisplayName("Join a specific team in a game that no player joined yet")
    @Test
    public void testJoinAnyTeamEmptyGame() {

        ContreePlayer newPlayer = humanPlayer();

        gamePlayers.joinGame(newPlayer);

        assertFalse(gamePlayers.isFull());
        assertTrue(gamePlayers.isJoinableByHumanPlayers());
    }

    @DisplayName("Join team 1 in a game that no player joined yet")
    @Test
    public void testJoinTeam1EmptyGame() {

        ContreePlayer newPlayer = humanPlayer();

        gamePlayers.joinGame(newPlayer, ContreeTeam.TEAM1);

        assertFalse(gamePlayers.isFull());
        assertTrue(gamePlayers.isJoinableByHumanPlayers());
    }

    @DisplayName("Join team 2 in a game that no player joined yet")
    @Test
    public void testJoinTeam2EmptyGame() {

        ContreePlayer newPlayer = humanPlayer();

        gamePlayers.joinGame(newPlayer, ContreeTeam.TEAM2);

        assertFalse(gamePlayers.isFull());
        assertTrue(gamePlayers.isJoinableByHumanPlayers());
    }

    @DisplayName("A player cannot join the game twice")
    @Test
    public void testJoinTwiceMustFail() {

        ContreePlayer newPlayer = humanPlayer();

        gamePlayers.joinGame(newPlayer);

        assertThrows(
                RuntimeException.class,
                () -> gamePlayers.joinGame(newPlayer)
        );

        assertThrows(
                RuntimeException.class,
                () -> gamePlayers.joinGame(newPlayer, ContreeTeam.TEAM2)
        );
    }

    @DisplayName("Join a team already full of human players must fail")
    @Test
    public void testJoinTeamFullOfHumanPlayers() {

        ContreePlayer humanPlayer1 = humanPlayer();
        ContreePlayer humanPlayer2 = humanPlayer();

        gamePlayers.joinGame(humanPlayer1, ContreeTeam.TEAM2);
        gamePlayers.joinGame(humanPlayer2, ContreeTeam.TEAM2);

        ContreePlayer newPlayer = humanPlayer();

        assertThrows(
                RuntimeException.class,
                () -> gamePlayers.joinGame(newPlayer, ContreeTeam.TEAM2)
        );
    }

    @DisplayName("Joining a full game without bot players must fail")
    @Test
    public void testJoinGameNotJoinableEvenByHumanPlayersMustFail() {

        for (int i = 0 ; i < 4 ; i++ ) {
            ContreePlayer player = humanPlayer();
            gamePlayers.joinGame(player);
        }

        assertTrue(gamePlayers.isFull());
        assertFalse(gamePlayers.isJoinableByHumanPlayers());

        ContreePlayer newPlayer = humanPlayer();

        assertTrue(gamePlayers.isFull());
        assertFalse(gamePlayers.isJoinableByHumanPlayers());

        assertThrows(
                RuntimeException.class,
                () -> gamePlayers.joinGame(newPlayer)
        );

    }

    @DisplayName("Joining a full game with multiple bots must succeed")
    @Test
    public void testJoinFullGameWithMultipleBots() {

        ContreePlayer notBotPlayer = humanPlayer();
        gamePlayers.joinGame(notBotPlayer);
        for (int i = 0 ; i < 3 ; i++ ) {
            ContreePlayer player = botPlayer();
            gamePlayers.joinGame(player);
        }
        assertTrue(gamePlayers.isFull());
        assertTrue(gamePlayers.isJoinableByHumanPlayers());

        ContreePlayer newPlayer = humanPlayer();
        gamePlayers.joinGame(newPlayer);

        assertTrue(gamePlayers.isFull());
        assertTrue(gamePlayers.isJoinableByHumanPlayers());
        assertSame(newPlayer, gamePlayers.getGamePlayers().get(1));

    }

    @DisplayName("Joining a full game with bots must succeed")
    @Test
    public void testJoinTeam2onEmptyPlayerList() {

        ContreePlayer player = humanPlayer();

        gamePlayers.joinGame(player, ContreeTeam.TEAM2);

        assertEquals(1, gamePlayers.getNbPlayers());
        assertSame(player, gamePlayers.getGamePlayers().get(1));
        assertNull(gamePlayers.getGamePlayers().get(0));

    }

    @DisplayName("When a human joins 3 bots waiting, the game is full")
    @Test
    public void testHumanJoinGameWithOnlyOneMissingPlayer_onlyBotAlreadyJoined() {

        for (int i = 0 ; i < 3 ; i++ ) {
            ContreePlayer player = botPlayer();
            gamePlayers.joinGame(player);
        }

        gamePlayers.joinGame(humanPlayer());

        assertTrue(gamePlayers.isFull());

    }

    @DisplayName("Joining a team with only bots must succeed")
    @Test
    public void testJoinTeamWhenTeamFullOfBotPlayers() {

        ContreePlayer botPlayer1 = botPlayer();
        ContreePlayer botPlayer2 = botPlayer();

        ContreePlayer humanPlayer = humanPlayer();

        when(botPlayer1.getTeam()).thenReturn(Optional.of(ContreeTeam.TEAM1));
        when(botPlayer2.getTeam()).thenReturn(Optional.of(ContreeTeam.TEAM1));

        gamePlayers.joinGame(botPlayer1, ContreeTeam.TEAM1);
        gamePlayers.joinGame(botPlayer2, ContreeTeam.TEAM1);

        gamePlayers.joinGame(humanPlayer, ContreeTeam.TEAM1);

        assertEquals(2, gamePlayers.getNbPlayers());
        assertSame(humanPlayer, gamePlayers.getGamePlayers().get(0));
        assertNull(gamePlayers.getGamePlayers().get(1));
        assertNull(gamePlayers.getGamePlayers().get(3));

    }

    private ContreePlayer botPlayer() {
        ContreePlayer botPlayer = mock(ContreePlayer.class);
        when(botPlayer.isBot()).thenReturn(true);
        return botPlayer;
    }

    private ContreePlayer humanPlayer() {
        ContreePlayer botPlayer = mock(ContreePlayer.class);
        when(botPlayer.isBot()).thenReturn(false);
        return botPlayer;
    }

    @Test
    @DisplayName("Building a ContreeDealPlayers on a valid ContreeGamePlayers must succeed and build a consistent object")
    public void testBuildDealPlayersOnFullGame() {

        players.forEach(gamePlayers::joinGame);

        var dealPlayers = gamePlayers.buildDealPlayers();
        assertNotNull(dealPlayers);
        assertEquals(ContreePlayers.NB_PLAYERS, dealPlayers.getNumberOfPlayers());
        assertEquals(gamePlayers.getGamePlayers(), dealPlayers.getCurrentDealPlayers());
        assertSame(dealPlayers.getCurrentDealPlayers().get(0), gamePlayers.getGamePlayers().get(0));

    }

    @DisplayName("Building a ContreeDealPlayers on an invalid ContreeGamePlayers must fail")
    @Test
    public void testBuildDealPlayersOnNonFullGame() {

        gamePlayers.joinGame(botPlayer());

        assertThrows(
            RuntimeException.class,
                gamePlayers::buildDealPlayers
        );

    }

    @DisplayName("A human player who previously joined the game leaves it")
    @Test
    public void testLeaveGame_playerExists() {

        var leaver = humanPlayer();

        gamePlayers.joinGame(leaver);
        gamePlayers.joinGame(botPlayer());
        gamePlayers.joinGame(botPlayer());
        gamePlayers.joinGame(botPlayer());

        gamePlayers.leaveGameAndReplaceWithBotPlayer(leaver);

        assertNotSame(gamePlayers.getGamePlayers().get(0), leaver);
        assertFalse(leaver.isBot());
        assertTrue(gamePlayers.getGamePlayers().get(0).isBot());

    }

    @DisplayName("Exception if a human player who didn't joined the game try to leave it")
    @Test
    public void testLeaveGame_playerDoesNotExist() {

        var leaver = humanPlayer();

        var joiner = humanPlayer();

        gamePlayers.joinGame(joiner);
        gamePlayers.joinGame(botPlayer());
        gamePlayers.joinGame(botPlayer());
        gamePlayers.joinGame(botPlayer());

        assertThrows(
            RuntimeException.class,
            () -> gamePlayers.leaveGameAndReplaceWithBotPlayer(leaver)
        );

    }

    @DisplayName("Exception if a bot player who joined the game try to leave it")
    @Test
    public void testLeaveGame_leavingPlayerIsBot() {

        var leaver = botPlayer();

        gamePlayers.joinGame(leaver);
        gamePlayers.joinGame(botPlayer());
        gamePlayers.joinGame(botPlayer());
        gamePlayers.joinGame(botPlayer());

        assertThrows(
            RuntimeException.class,
            () -> gamePlayers.leaveGameAndReplaceWithBotPlayer(leaver)
        );

    }

}
