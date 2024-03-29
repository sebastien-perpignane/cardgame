package sebastien.perpignane.cardgame.game.war;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.war.WarPlayer;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WarTrickTest {

    private static WarPlayer player1;
    private static WarPlayer player2;

    private static List<WarPlayer> players;

    private static WarGameEventSender eventSender;

    private WarTrick trick;

    @BeforeAll
    static void globalSetUp() {
        player1 = mock(WarPlayer.class);
        player2 = mock(WarPlayer.class);
        players = List.of(player1, player2);
        eventSender = mock(WarGameEventSender.class);
    }

    @BeforeEach
    void setUp() {
        trick = new WarTrick("TEST", players, eventSender);
    }

    @Test
    @DisplayName("Trick is not over when one player plays one card at the beginning of a trick")
    void testPlayCardOnEmptyTrick() {
        trick.playerPlay(player1, ClassicalCard.SIX_CLUB);

        assertThat(trick.isOver()).isFalse();
        assertThat(trick.getWinner()).isEmpty();

    }

    @Test
    @DisplayName("End of trick when two players play 1 card, if no war situation")
    void testPlayEndOfTrick_no_war_condition_mock() {
        trick.playerPlay(player1, ClassicalCard.JACK_CLUB);
        trick.playerPlay(player2, ClassicalCard.ACE_CLUB);

        assertThat(trick.isOver()).isTrue();
    }

    @Test
    @DisplayName("End of trick when two players play 1 card, if no war situation")
    void testPlayEndOfTrick_no_war_condition() {

        trick.playerPlay(player1, ClassicalCard.SIX_CLUB);
        trick.playerPlay(player2, ClassicalCard.TEN_DIAMOND);

        assertThat(trick.isOver()).isTrue();

        assertThat(trick.getWinner()).isPresent();
        assertThat(trick.getWinner()).containsSame(player2);

    }

    @Test
    @DisplayName("End of trick when 2 players play 3 cards in simple war condition")
    void testPlayEndOfTrick_war_condition() {

        trick.playerPlay(player1, ClassicalCard.SIX_CLUB);
        trick.playerPlay(player2, ClassicalCard.SIX_DIAMOND);

        trick.playerPlay(player1, ClassicalCard.ACE_CLUB);
        trick.playerPlay(player2, ClassicalCard.TEN_DIAMOND);

        trick.playerPlay(player1, ClassicalCard.ACE_SPADE);
        trick.playerPlay(player2, ClassicalCard.TEN_SPADE);

        assertThat(trick.isOver()).isTrue();
        assertThat(trick.getWinner()).isPresent();
        assertThat(trick.getWinner()).containsSame(player1);

    }

    @Test
    @DisplayName("End of trick when 2 players play 5 cards in 2 successive war conditions")
    void testPlayEndOfTrick_successive_war_conditions() {

        trick.playerPlay(player1, ClassicalCard.SIX_CLUB);
        trick.playerPlay(player2, ClassicalCard.SIX_DIAMOND);

        trick.playerPlay(player1, ClassicalCard.ACE_CLUB);
        trick.playerPlay(player2, ClassicalCard.TEN_DIAMOND);

        trick.playerPlay(player1, ClassicalCard.ACE_SPADE);
        trick.playerPlay(player2, ClassicalCard.ACE_DIAMOND);

        trick.playerPlay(player1, ClassicalCard.EIGHT_CLUB);
        trick.playerPlay(player2, ClassicalCard.EIGHT_HEART);

        trick.playerPlay(player1, ClassicalCard.NINE_CLUB);
        trick.playerPlay(player2, ClassicalCard.TEN_HEART);

        assertThat(trick.isOver()).isTrue();
        assertThat(trick.getWinner()).isPresent();
        assertThat(trick.getWinner()).containsSame(player2);

    }

    @Test
    @DisplayName("Error if a player plays a card on a ended trick")
    void testPlayAfterEndOfTrick() {

        trick.playerPlay(player1, ClassicalCard.SIX_CLUB);
        trick.playerPlay(player2, ClassicalCard.TEN_DIAMOND);

        assertThat(trick.isOver()).isTrue();

        IllegalStateException ise =
                assertThrows(
                        IllegalStateException.class,
                        () -> trick.playerPlay(player1, ClassicalCard.TEN_CLUB)
                );

        assertThat(ise).isNotNull();
        assertThat(ise.getMessage()).isEqualTo("This trick is over");

    }

    @Test
    @DisplayName("Trick stops if one player is out of cards during war")
    void testPrematureEndOfTrickDuringWar() {
        trick.playerPlay(player1, ClassicalCard.SIX_CLUB);
        trick.playerPlay(player2, ClassicalCard.SIX_DIAMOND);
        // WAR

        trick.playerPlay(player1, ClassicalCard.ACE_SPADE);
        when(player2.hasNoMoreCard()).thenReturn(true);
        trick.playerPlay(player2, ClassicalCard.FIVE_SPADE);
        // Player 2 has no more card to play

        assertThat(player2.hasNoMoreCard()).isTrue();
        assertThat(trick.isOver()).isTrue();
        assertThat(trick.getWinner()).isPresent();
        assertThat(trick.getWinner()).containsSame(player1);

    }

}
