package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.player.contree.ContreeBotPlayer;

import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContreeGamePlayersTest {

    @DisplayName("Join a game that no player joined yet")
    @Test
    public void testJoinEmptyGame() {

        ContreeGame game = new ContreeGame(GameTextDisplayer.getInstance());
        ContreeGamePlayers contreeGamePlayers = new ContreeGamePlayers(game);

        ContreeBotPlayer newPlayer = new TestBiddingContreePlayer(ContreeBidValue.EIGHTY, CardSuit.HEARTS);

        contreeGamePlayers.joinGame(newPlayer);

        assertFalse(contreeGamePlayers.isFull());
    }

    @DisplayName("Joining a full game without bot players must fail")
    @Test
    public void testJoinFullGame() {

        ContreeGame game = new ContreeGame(GameTextDisplayer.getInstance());
        ContreeGamePlayers contreeGamePlayers = new ContreeGamePlayers(game);

        for (int i = 0 ; i < 4 ; i++ ) {
            ContreeBotPlayer player = new TestBiddingContreePlayer(ContreeBidValue.NONE, null);
            contreeGamePlayers.joinGame(player);
        }

        ContreeBotPlayer newPlayer = new TestBiddingContreePlayer(ContreeBidValue.EIGHTY, CardSuit.HEARTS);

        assertThrows(
                IllegalStateException.class,
                () -> contreeGamePlayers.joinGame(newPlayer)
        );

    }

    @DisplayName("Joining a full game with bots must succeed")
    @Test
    public void testJoinNotFullGame() {

        ContreeGame game = new ContreeGame(GameTextDisplayer.getInstance());
        ContreeGamePlayers contreeGamePlayers = new ContreeGamePlayers(game);

        TestBiddingContreePlayer notBotPlayer = new TestBiddingContreePlayer(ContreeBidValue.EIGHTY, CardSuit.HEARTS);
        contreeGamePlayers.joinGame(notBotPlayer);
        for (int i = 0 ; i < 3 ; i++ ) {
            ContreeBotPlayer player = new ContreeBotPlayer();
            contreeGamePlayers.joinGame(player);
        }

        TestBiddingContreePlayer newPlayer = new TestBiddingContreePlayer(ContreeBidValue.EIGHTY, CardSuit.HEARTS);
        contreeGamePlayers.joinGame(newPlayer);

        assertSame(newPlayer, contreeGamePlayers.getPlayers().get(1));

    }

    @DisplayName("Rolling player list when a new deal starts")
    @Test
    public void testNextPlayerList() {

        ContreeGame game = mock(ContreeGame.class);

        var players = buildPlayers();

        ContreeGamePlayers contreeGamePlayers = new ContreeGamePlayers(game);

        players.forEach(contreeGamePlayers::joinGame);

        assertSame(contreeGamePlayers.getCurrentDealer(), players.get(3));

        var nextPlayerList = contreeGamePlayers.nextPlayerList();

        // 1st player is now the dealer (i.e. the last player in the deal player list)
        assertEquals(ContreeGame.NB_PLAYERS, nextPlayerList.size());
        assertSame(
            contreeGamePlayers.getCurrentDealer(),
            contreeGamePlayers.getPlayers().get(0),
            "Current dealer is supposed to be player with index 0 mais is actually player with index: " + players.indexOf(contreeGamePlayers.getCurrentDealer())
        );

        nextPlayerList = contreeGamePlayers.nextPlayerList();
        assertSame(contreeGamePlayers.getCurrentDealer(), players.get(1));
        assertEquals(ContreeGame.NB_PLAYERS, nextPlayerList.size());

        nextPlayerList = contreeGamePlayers.nextPlayerList();
        assertSame(contreeGamePlayers.getCurrentDealer(), players.get(2));
        assertEquals(ContreeGame.NB_PLAYERS, nextPlayerList.size());

        nextPlayerList = contreeGamePlayers.nextPlayerList();
        assertSame(contreeGamePlayers.getCurrentDealer(), players.get(3));
        assertEquals(ContreeGame.NB_PLAYERS, nextPlayerList.size());

        nextPlayerList = contreeGamePlayers.nextPlayerList();
        assertSame(contreeGamePlayers.getCurrentDealer(), players.get(0));
        assertEquals(ContreeGame.NB_PLAYERS, nextPlayerList.size());

    }

}
