package sebastien.perpignane.cardgame.player.contree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.game.contree.ContreeGame;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;
import static sebastien.perpignane.cardgame.card.ClassicalCard.*;

class ContreePlayerImplTest {

    private final static String TEST_PLAYER_NAME = "test player";

    private ContreePlayerEventHandler eventHandler;

    private ContreeGame game;

    private ContreePlayerImpl contreePlayer;

    @BeforeEach
    public void setUp() {
        eventHandler = mock(ContreePlayerEventHandler.class);
        game = mock(ContreeGame.class);
        contreePlayer = new ContreePlayerImpl(TEST_PLAYER_NAME, eventHandler);
    }

    @DisplayName("When player receives hand, eventHandler is called, player has cards")
    @Test
    public void testReceiveHand() {

        boolean[] flag = new boolean[1];

        var hand = List.of(ACE_SPADE, JACK_HEART);

        doAnswer((invocationOnMock -> {
            flag[0] = true;
            return null;
        })).when(eventHandler).onReceivedHand(hand);


        contreePlayer.receiveHand(hand);

        assertThat(flag[0]).isTrue();
        assertThat(contreePlayer.hasNoMoreCard()).isFalse();
        assertThat(contreePlayer.getHand().stream().toList()).isEqualTo(hand);

    }

    @DisplayName("After instantiation, a player has no cards")
    @Test
    public void testHasNoMoreCard_initialState() {
        assertThat(contreePlayer.hasNoMoreCard()).isTrue();
    }

    @DisplayName("After hand is received, player has cards")
    @Test
    public void testHasNoMoreCard_afterHandReceived() {
        contreePlayer.receiveHand(List.of(JACK_SPADE, ACE_HEART));

        assertThat(contreePlayer.hasNoMoreCard()).isFalse();
    }

    @DisplayName("receiveNewCards is not supported")
    @Test
    public void testReceiveNewCardsFails() {
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> contreePlayer.receiveNewCards(Collections.emptyList()));
    }

    @DisplayName("Event handler is called when game started event is triggered")
    @Test
    public void testOnGameStarted() {

        boolean[] flag = new boolean[1];

        doAnswer((invocationOnMock -> {
            flag[0] = true;
            return null;
        })).when(eventHandler).onGameStarted();

        contreePlayer.onGameStarted();

        assertThat(flag[0]).isTrue();

    }

    @DisplayName("event handler called when game over event is triggered")
    @Test
    void testOnGameOver() {

        boolean[] flag = new boolean[1];

        doAnswer((invocationOnMock -> {
            flag[0] = true;
            return null;
        })).when(eventHandler).onGameOver();

        contreePlayer.onGameOver();

        assertThat(flag[0]).isTrue();

    }

    @DisplayName("on player turn to play, eventHandler is called")
    @Test
    void testOnPlayerTurn() {

        var allowedCards = Set.of(ACE_SPADE, NINE_DIAMOND);

        boolean[] flag = new boolean[1];

        doAnswer((invocationOnMock -> {
            flag[0] = true;
            return null;
        })).when(eventHandler).onPlayerTurn(allowedCards);

        contreePlayer.onPlayerTurn(allowedCards);

        assertThat(flag[0]).isTrue();

    }

    @DisplayName("When a player is ejected from a game, eventHandler is called")
    @Test
    void testOnGameEjection() {

        boolean[] flag = new boolean[1];

        doAnswer((invocationOnMock -> {
            flag[0] = true;
            return null;
        })).when(eventHandler).onEjection();

        contreePlayer.onGameEjection();

        assertThat(flag[0]).isTrue();

    }

    @DisplayName("nbAvailableCards is consistent with the hand of the player")
    @Test
    public void testNbAvailableCards() {

        assertThat(contreePlayer.nbAvailableCards()).isEqualTo(0);

        contreePlayer.receiveHand(List.of(TEN_SPADE, NINE_DIAMOND));
        assertThat(contreePlayer.nbAvailableCards()).isEqualTo(2);

    }

    @DisplayName("removeCardFromHand works as expected")
    @Test
    public void testRemoveCardFromHand() {

        contreePlayer.receiveHand(new ArrayList<>(List.of(JACK_SPADE, ACE_HEART, NINE_HEART)));
        assertThat(contreePlayer.getHand().contains(ACE_HEART)).isTrue();

        contreePlayer.removeCardFromHand(ACE_HEART);

        assertThat(contreePlayer.getHand().contains(ACE_HEART)).isFalse();

    }

    @DisplayName("event handler is called when player turn to bid event is triggered")
    @Test
    public void testOnPlayerTurnToBid() {

        Set<ContreeBidValue> allowedBids = Set.of(ContreeBidValue.HUNDRED, ContreeBidValue.CAPOT);

        boolean[] flag = new boolean[1];

        doAnswer((invocationOnMock -> {
            flag[0] = true;
            return null;
        })).when(eventHandler).onPlayerTurnToBid(allowedBids);

        contreePlayer.onPlayerTurnToBid(allowedBids);

        assertThat(flag[0]).isTrue();

    }

    @DisplayName("When both players have same team, sameTeam() returns true")
    @Test
    void testSameTeam_sameTeam() {

        contreePlayer.setTeam(ContreeTeam.TEAM1);

        var otherPlayer = new ContreePlayerImpl(eventHandler);
        otherPlayer.setTeam(ContreeTeam.TEAM1);

        assertThat(contreePlayer.sameTeam(otherPlayer)).isTrue();

    }

    @DisplayName("When both players have different team, sameTeam() returns false")
    @Test
    void testSameTeam_notSameTeam() {

        contreePlayer.setTeam(ContreeTeam.TEAM1);

        var otherPlayer = new ContreePlayerImpl(eventHandler);
        otherPlayer.setTeam(ContreeTeam.TEAM2);

        assertThat(contreePlayer.sameTeam(otherPlayer)).isFalse();

    }

    @DisplayName("When team is not defined on other player, sameTeam() returns false")
    @Test
    public void testSameTeam_noTeamDefinedOnOther() {

        contreePlayer.setTeam(ContreeTeam.TEAM1);
        var otherPlayer = new ContreePlayerImpl(eventHandler);

        assertThat(contreePlayer.sameTeam(otherPlayer)).isFalse();

    }

    @DisplayName("When team is not defined on current player but defined on other player , sameTeam() returns false")
    @Test
    public void testSameTeam_noTeamDefinedOnCurrentPlayer() {

        var otherPlayer = new ContreePlayerImpl(eventHandler);
        otherPlayer.setTeam(ContreeTeam.TEAM1);

        assertThat(contreePlayer.sameTeam(otherPlayer)).isFalse();

    }

    @DisplayName("When both players have no team, sameTeam() returns false")
    @Test
    public void testSameTeam_noTeamOnBothPlayers_false() {
        var otherPlayer = new ContreePlayerImpl(eventHandler);

        assertThat(contreePlayer.sameTeam(otherPlayer)).isFalse();
    }

    @DisplayName("When a player plays a card, game#playCard method is called")
    @Test
    void testPlayCard() {

        contreePlayer.setGame(game);

        boolean[] flag = new boolean[1];

        doAnswer((invocationOnMock -> {
            flag[0] = true;
            return null;
        })).when(game).playCard(contreePlayer, JACK_CLUB);

        contreePlayer.playCard(JACK_CLUB);

        assertThat(flag[0]).isTrue();

    }

    @DisplayName("When a player places a bid, game#placeBid method is called")
    @Test
    void testPlaceBid() {

        contreePlayer.setGame(game);

        boolean[] flag = new boolean[1];

        doAnswer((invocationOnMock -> {
            flag[0] = true;
            return null;
        })).when(game).placeBid(contreePlayer, ContreeBidValue.EIGHTY, CardSuit.HEARTS);

        contreePlayer.placeBid(ContreeBidValue.EIGHTY, CardSuit.HEARTS);

        assertThat(flag[0]).isTrue();

    }

    @DisplayName("When leaveGame event is triggered, game#leaveGame is called")
    @Test
    void testLeaveGame() {
        contreePlayer.setGame(game);

        boolean[] flag = new boolean[1];

        doAnswer((invocationOnMock -> {
            flag[0] = true;
            return null;
        })).when(game).leaveGame(contreePlayer);

        contreePlayer.leaveGame();

        assertThat(flag[0]).isTrue();
    }

    @DisplayName("Initial status of a player is 'waiting'")
    @Test
    void testInitialPlayerStatus() {
        assertThat(contreePlayer.isWaiting()).isTrue();
    }

    @DisplayName("setPlayingPlayer method changes the player status as expected")
    @Test
    void testSetPlaying() {
        contreePlayer.setPlaying();

        assertThat(contreePlayer.isPlaying()).isTrue();
    }

    @DisplayName("setBiddingPlayer method changes the player status as expected")
    @Test
    void testSetBidding() {
        contreePlayer.setBidding();
        assertThat(contreePlayer.isBidding()).isTrue();
    }

    @DisplayName("setWaiting method changes the player status as expected")
    @Test
    void testSetWaiting() {
        // As waiting is initial state, we change the state before testing setWaiting
        contreePlayer.setBidding();
        assertThat(contreePlayer.isPlaying()).isFalse();

        contreePlayer.setWaiting();
        assertThat(contreePlayer.isWaiting()).isTrue();
    }

    @DisplayName("toState method returns the expected data")
    @Test
    public void testToState() {

        var state = contreePlayer.toState();

        assertThat(state).isNotNull();
        assertThat(state.getName()).isNotNull();
        assertThat(state.getName()).isEqualTo(TEST_PLAYER_NAME);
        assertThat(state.getStatus()).isNotNull();

    }

    @DisplayName("getId returns a unique ID, on 10000 instantiated players")
    @Test
    public void testGetId() {

        Set<String> playerIds = new HashSet<>();

        ContreePlayer player;
        for (int i = 0; i < 10000; i++) {
            player = new ContreePlayerImpl(mock(ContreePlayerEventHandler.class));
            playerIds.add(player.getId());
        }

        assertThat(playerIds.size()).isEqualTo(10000);

    }

    @DisplayName("toFullState method returns the expected data")
    @Test
    public void testToFullState() {

        var fullState = contreePlayer.toFullState();

        assertThat(fullState).isNotNull();
        assertThat(fullState.getName()).isNotNull();
        assertThat(fullState.getStatus()).isNotNull();
        assertThat(fullState.getId()).isNotNull();
        assertThat(fullState.getHand()).isNotNull();

    }

    @DisplayName("toString formatting on bot player")
    @Test
    public void testToString_botPlayer() {

        var eventHandler = mock(ContreePlayerEventHandler.class);
        when(eventHandler.isBot()).thenReturn(true);

        var botPlayer = new ContreePlayerImpl(eventHandler);

        assertThat(botPlayer.toString().contains("Bot")).isTrue();

    }

    @DisplayName("toString formatting on bot player")
    @Test
    public void testToString_humanPlayer() {

        var eventHandler = mock(ContreePlayerEventHandler.class);
        when(eventHandler.isBot()).thenReturn(false);

        var botPlayer = new ContreePlayerImpl(eventHandler);

        assertThat(botPlayer.toString().startsWith("*")).isTrue();

    }

}