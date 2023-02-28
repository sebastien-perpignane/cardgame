package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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

    @DisplayName("Join a specific team in a game that no player joined yet")
    @Test
    public void testJoinAnyTeamEmptyGame() {

        ContreePlayer newPlayer = humanPlayer();

        JoinGameResult joinGameResult = gamePlayers.joinGame(newPlayer);

        assertThat(joinGameResult.playerIndex()).isEqualTo(0);
        assertThat(joinGameResult.replacedPlayer()).isEmpty();
        assertThat(gamePlayers.isFull()).isFalse();
        assertThat(gamePlayers.isJoinableByHumanPlayers()).isTrue();
    }

    @DisplayName("Join team 1 in a game that no player joined yet")
    @Test
    public void testJoinTeam1EmptyGame() {

        ContreePlayer newPlayer = humanPlayer();

        JoinGameResult joinGameResult = gamePlayers.joinGame(newPlayer, ContreeTeam.TEAM1);

        assertThat(joinGameResult.playerIndex()).isEqualTo(0);
        assertThat(joinGameResult.replacedPlayer()).isEmpty();
        assertThat(gamePlayers.isFull()).isFalse();
        assertThat(gamePlayers.isJoinableByHumanPlayers()).isTrue();
    }

    @DisplayName("Join team 2 in a game that no player joined yet")
    @Test
    public void testJoinTeam2EmptyGame() {

        ContreePlayer newPlayer = humanPlayer();

        JoinGameResult joinGameResult = gamePlayers.joinGame(newPlayer, ContreeTeam.TEAM2);

        assertThat(joinGameResult.playerIndex()).isEqualTo(1);
        assertThat(joinGameResult.replacedPlayer()).isEmpty();
        assertThat(gamePlayers.isFull()).isFalse();
        assertThat(gamePlayers.isJoinableByHumanPlayers()).isTrue();
    }

    @DisplayName("A player cannot join the game twice")
    @Test
    public void testJoinTwiceMustFail() {

        ContreePlayer newPlayer = humanPlayer();

        gamePlayers.joinGame(newPlayer);

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> gamePlayers.joinGame(newPlayer));

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> gamePlayers.joinGame(newPlayer, ContreeTeam.TEAM2));
    }

    @DisplayName("Join a team already full of human players must fail")
    @Test
    public void testJoinTeamFullOfHumanPlayers() {

        ContreePlayer humanPlayer1 = humanPlayer();
        ContreePlayer humanPlayer2 = humanPlayer();

        gamePlayers.joinGame(humanPlayer1, ContreeTeam.TEAM2);
        gamePlayers.joinGame(humanPlayer2, ContreeTeam.TEAM2);

        ContreePlayer newPlayer = humanPlayer();

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> gamePlayers.joinGame(newPlayer, ContreeTeam.TEAM2));
    }

    @DisplayName("Joining a full game without bot players must fail")
    @Test
    public void testJoinGameNotJoinableEvenByHumanPlayersMustFail() {

        for (int i = 0 ; i < 4 ; i++ ) {
            ContreePlayer player = humanPlayer();
            gamePlayers.joinGame(player);
        }

        assertThat(gamePlayers.isFull()).isTrue();
        assertThat(gamePlayers.isJoinableByHumanPlayers()).isFalse();

        ContreePlayer newPlayer = humanPlayer();

        assertThat(gamePlayers.isFull()).isTrue();
        assertThat(gamePlayers.isJoinableByHumanPlayers()).isFalse();

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> gamePlayers.joinGame(newPlayer));

    }

    @DisplayName("Joining a full game with multiple bots must succeed")
    @Test
    public void testJoinFullGameWithMultipleBots() {

        ContreePlayer humanPlayer = humanPlayer();
        gamePlayers.joinGame(humanPlayer);
        for (int i = 0 ; i < 3 ; i++ ) {
            ContreePlayer player = botPlayer();
            gamePlayers.joinGame(player);
        }
        assertThat(gamePlayers.isFull()).isTrue();
        assertThat(gamePlayers.isJoinableByHumanPlayers()).isTrue();

        ContreePlayer newPlayer = humanPlayer();
        JoinGameResult joinGameResult = gamePlayers.joinGame(newPlayer);

        // player with index 0 is a human
        assertThat(joinGameResult.playerIndex()).isEqualTo(1);
        assertThat(joinGameResult.replacedPlayer()).isPresent();
        assertThat(joinGameResult.replacedPlayer().get()).isNotSameAs(newPlayer);
        assertThat(gamePlayers.isFull()).isTrue();
        assertThat(gamePlayers.isJoinableByHumanPlayers()).isTrue();
        assertThat(gamePlayers.getGamePlayers().get(1)).isSameAs(newPlayer);

    }

    @DisplayName("Joining an empty game in a specific team must succeed")
    @Test
    public void testJoinTeam2onEmptyPlayerList() {

        ContreePlayer player = humanPlayer();

        JoinGameResult joinGameResult = gamePlayers.joinGame(player, ContreeTeam.TEAM2);

        // first team 2 slot is index 1
        assertThat(joinGameResult.playerIndex()).isEqualTo(1);
        assertThat(joinGameResult.replacedPlayer()).isEmpty();
        assertThat(gamePlayers.getNbPlayers()).isEqualTo(1);
        assertThat(gamePlayers.getGamePlayers().get(1)).isSameAs(player);
        assertThat(gamePlayers.getGamePlayers().get(0)).isNull();

    }

    @DisplayName("When a human joins 3 bots waiting, the game is full")
    @Test
    public void testHumanJoinGameWithOnlyOneMissingPlayer_onlyBotAlreadyJoined() {

        for (int i = 0 ; i < 3 ; i++ ) {
            gamePlayers.joinGame(botPlayer());
        }

        gamePlayers.joinGame(humanPlayer());

        assertThat(gamePlayers.isFull()).isTrue();

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

        JoinGameResult joinGameResult = gamePlayers.joinGame(humanPlayer, ContreeTeam.TEAM1);

        assertThat(joinGameResult.playerIndex()).isEqualTo(0);
        assertThat(joinGameResult.replacedPlayer()).isPresent();
        assertThat(joinGameResult.replacedPlayer().get()).isSameAs(botPlayer1);
        assertThat(gamePlayers.getNbPlayers()).isEqualTo(2);
        assertThat(gamePlayers.getGamePlayers().get(0)).isSameAs(humanPlayer);
        assertThat(gamePlayers.getGamePlayers().get(1)).isNull();
        assertThat(gamePlayers.getGamePlayers().get(3)).isNull();

    }



    @Test
    @DisplayName("Building a ContreeDealPlayers on a valid ContreeGamePlayers must succeed and build a consistent object")
    public void testBuildDealPlayersOnFullGame() {

        players.forEach(gamePlayers::joinGame);

        var dealPlayers = gamePlayers.buildDealPlayers();
        assertThat(dealPlayers).isNotNull();
        assertThat(dealPlayers.getNumberOfPlayers()).isEqualTo(ContreePlayers.NB_PLAYERS);
        assertThat(dealPlayers.getCurrentDealPlayers()).isEqualTo(gamePlayers.getGamePlayers());
        assertThat(gamePlayers.getGamePlayers().get(0)).isSameAs(dealPlayers.getCurrentDealPlayers().get(0));

    }

    @DisplayName("Building a ContreeDealPlayers on an invalid ContreeGamePlayers must fail")
    @Test
    public void testBuildDealPlayersOnNonFullGameMustFail() {

        gamePlayers.joinGame(botPlayer());

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(gamePlayers::buildDealPlayers);

    }

    @DisplayName("A human player who previously joined the game leaves it")
    @Test
    public void testLeaveGame_playerExists() {

        var leaver = humanPlayer();

        gamePlayers.joinGame(leaver);
        gamePlayers.joinGame(botPlayer());
        gamePlayers.joinGame(botPlayer());
        gamePlayers.joinGame(botPlayer());

        var newPlayer = gamePlayers.leaveGameAndReplaceWithBotPlayer(leaver);

        assertThat(leaver).isNotSameAs(gamePlayers.getGamePlayers().get(0));
        assertThat(leaver).isNotSameAs(newPlayer);
        assertThat(leaver.isBot()).isFalse();
        assertThat(gamePlayers.getGamePlayers().get(0).isBot()).isTrue();

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

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> gamePlayers.leaveGameAndReplaceWithBotPlayer(leaver));

    }

    @DisplayName("Exception if a bot player who joined the game try to leave it")
    @Test
    public void testLeaveGame_leavingPlayerIsBot() {

        var leaver = botPlayer();

        gamePlayers.joinGame(leaver);
        gamePlayers.joinGame(botPlayer());
        gamePlayers.joinGame(botPlayer());
        gamePlayers.joinGame(botPlayer());

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> gamePlayers.leaveGameAndReplaceWithBotPlayer(leaver));

    }

    @Test
    public void testJoinGame_replacedPlayer_propagatedToBidPlayers() {

        var botPlayer1 = botPlayer();

        gamePlayers.joinGame(botPlayer1);
        gamePlayers.joinGame(botPlayer());
        gamePlayers.joinGame(botPlayer());
        gamePlayers.joinGame(botPlayer());

        var dealPlayers = gamePlayers.buildDealPlayers();

        var bidPlayers = dealPlayers.buildBidPlayers();

        assertThat(bidPlayers.getCurrentBidderSlot().getPlayer()).isPresent();
        assertThat(bidPlayers.getCurrentBidderSlot().getPlayer().get()).isSameAs(botPlayer1);

        var humanPlayer = humanPlayer();
        var joinResult = gamePlayers.joinGame(humanPlayer);
        var replacedPlayer = joinResult.replacedPlayer();

        assertThat(replacedPlayer).isNotNull();

        assertThat(bidPlayers.getCurrentBidderSlot().getPlayer()).isPresent();
        assertThat(bidPlayers.getCurrentBidderSlot().getPlayer().get()).isSameAs(humanPlayer);
    }

}
