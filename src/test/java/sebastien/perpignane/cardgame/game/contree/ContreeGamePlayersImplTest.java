package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.buildPlayers;

public class ContreeGamePlayersImplTest {

    @DisplayName("Join a specific team in a game that no player joined yet")
    @Test
    public void testJoinAnyTeamEmptyGame() {

        ContreeGame game = mock(ContreeGame.class);
        ContreeGamePlayers gamePlayers = new ContreeGamePlayersImpl(game);

        ContreePlayer newPlayer = humanPlayer();

        gamePlayers.joinGame(newPlayer);

        assertFalse(gamePlayers.isFull());
        assertTrue(gamePlayers.isJoinableByHumanPlayers());
    }

    @DisplayName("Join team 1 in a game that no player joined yet")
    @Test
    public void testJoinTeam1EmptyGame() {

        ContreeGame game = mock(ContreeGame.class);
        ContreeGamePlayers gamePlayers = new ContreeGamePlayersImpl(game);

        ContreePlayer newPlayer = humanPlayer();

        gamePlayers.joinGame(newPlayer, ContreeTeam.TEAM1);

        assertFalse(gamePlayers.isFull());
        assertTrue(gamePlayers.isJoinableByHumanPlayers());
    }

    @DisplayName("Join team 2 in a game that no player joined yet")
    @Test
    public void testJoinTeam2EmptyGame() {

        ContreeGame game = mock(ContreeGame.class);
        ContreeGamePlayers gamePlayers = new ContreeGamePlayersImpl(game);

        ContreePlayer newPlayer = humanPlayer();

        gamePlayers.joinGame(newPlayer, ContreeTeam.TEAM2);

        assertFalse(gamePlayers.isFull());
        assertTrue(gamePlayers.isJoinableByHumanPlayers());
    }

    @DisplayName("A player cannot join the game twice")
    @Test
    public void testJoinTwiceMustFail() {

        ContreeGame game = mock(ContreeGame.class);
        ContreeGamePlayers gamePlayers = new ContreeGamePlayersImpl(game);

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

        ContreeGame game = mock(ContreeGame.class);
        ContreeGamePlayers gamePlayers = new ContreeGamePlayersImpl(game);

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

        ContreeGame game = mock(ContreeGame.class);
        ContreeGamePlayers gamePlayers = new ContreeGamePlayersImpl(game);

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

    @DisplayName("Joining a full game with multiple bots")
    @Test
    public void testJoinFullGameWithMultipleBots() {

        ContreeGame game = mock(ContreeGame.class);
        ContreeGamePlayers gamePlayers = new ContreeGamePlayersImpl(game);

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

        ContreeGame game = mock(ContreeGame.class);
        ContreeGamePlayers gamePlayers = new ContreeGamePlayersImpl(game);

        ContreePlayer player = humanPlayer();

        gamePlayers.joinGame(player, ContreeTeam.TEAM2);

        assertEquals(1, gamePlayers.getNbPlayers());
        assertSame(player, gamePlayers.getGamePlayers().get(1));
        assertNull(gamePlayers.getGamePlayers().get(0));

    }

    @DisplayName("Joining a team with only bots must succeed")
    @Test
    public void testJoinTeamWhenTeamFullOfBotPlayers() {

        ContreeGame game = mock(ContreeGame.class);
        ContreeGamePlayers gamePlayers = new ContreeGamePlayersImpl(game);

        ContreePlayer botPlayer1 = botPlayer();
        ContreePlayer botPlayer2 = botPlayer();

        ContreePlayer humanPlayer = humanPlayer();

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

        var players = buildPlayers();

        ContreeGame contreeGame = mock(ContreeGame.class);

        ContreeGamePlayers gamePlayers = new ContreeGamePlayersImpl(contreeGame);

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

        ContreeGame contreeGame = mock(ContreeGame.class);

        final ContreeGamePlayers gamePlayers = new ContreeGamePlayersImpl(contreeGame);

        gamePlayers.joinGame(botPlayer());

        assertThrows(
            RuntimeException.class,
                gamePlayers::buildDealPlayers
        );

    }

}
