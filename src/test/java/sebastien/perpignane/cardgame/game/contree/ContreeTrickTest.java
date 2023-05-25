package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

class ContreeTrickTest extends TestCasesManagingPlayers {

    private PlayableCardsFilter playableCardsFilter;

    private ContreeTrick trickWithHeartAsTrump;


    @BeforeAll
    static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {
        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);
        when(trickPlayers.getCurrentPlayerSlot()).thenAnswer(AdditionalAnswers.returnsElementsOf(playerSlots));

        var deal = MockDealBuilder.builder().withMockedGameEventSender().withTrumpSuit(CardSuit.HEARTS)
            .build();

        playableCardsFilter = mock(PlayableCardsFilter.class);

        when(playableCardsFilter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        trickWithHeartAsTrump = new ContreeTrick(deal, "TEST", trickPlayers, playableCardsFilter);
    }

    @DisplayName("When other player than current player plays, an exception is thrown")
    @Test
    void testThrowsExceptionWhenNotExpectedPlayerPlays() {
        trickWithHeartAsTrump.startTrick();

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> trickWithHeartAsTrump.playerPlays(player2, ClassicalCard.JACK_HEART));
    }

    @DisplayName("winning player on a trick without played card is null")
    @Test
    void testWinningPlayerWhenNoPlayedCardThrowsException() {
        assertThat(trickWithHeartAsTrump.winningPlayer()).isNull();
    }

    @DisplayName("winning player on a trick with played card is consistent")
    @Test
    void testWinningPlayerWhenCardsWerePlayed() {
        trickWithHeartAsTrump.startTrick();

        trickWithHeartAsTrump.playerPlays(player1, ClassicalCard.JACK_CLUB);
        assertThat(trickWithHeartAsTrump.winningPlayer()).isSameAs(player1);

        trickWithHeartAsTrump.playerPlays(player2, ClassicalCard.TEN_CLUB);
        assertThat(trickWithHeartAsTrump.winningPlayer()).isSameAs(player2);

        trickWithHeartAsTrump.playerPlays(player3, ClassicalCard.ACE_CLUB);
        assertThat(trickWithHeartAsTrump.winningPlayer()).isSameAs(player3);

        trickWithHeartAsTrump.playerPlays(player4, ClassicalCard.SEVEN_HEART);
        assertThat(trickWithHeartAsTrump.winningPlayer()).isSameAs(player4);

    }

    @DisplayName("winning player on a trick with played card is consistent")
    @Test
    void testPlayCardWhenTrickIsOver() {
        trickWithHeartAsTrump.startTrick();

        trickWithHeartAsTrump.playerPlays(player1, ClassicalCard.JACK_CLUB);
        trickWithHeartAsTrump.playerPlays(player2, ClassicalCard.TEN_CLUB);
        trickWithHeartAsTrump.playerPlays(player3, ClassicalCard.ACE_CLUB);
        trickWithHeartAsTrump.playerPlays(player4, ClassicalCard.SEVEN_HEART);

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> trickWithHeartAsTrump.playerPlays(player1, ClassicalCard.JACK_HEART));

    }

    @DisplayName("getAllCards reflects cards played during the trick")
    @Test
    void testGetPlayedCards() {

        trickWithHeartAsTrump.startTrick();

        trickWithHeartAsTrump.playerPlays(player1, ClassicalCard.JACK_CLUB);
        trickWithHeartAsTrump.playerPlays(player2, ClassicalCard.ACE_CLUB);
        trickWithHeartAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);

        assertThat(trickWithHeartAsTrump.getAllCards()).isEqualTo(Set.of(
                ClassicalCard.JACK_CLUB,
                ClassicalCard.ACE_CLUB,
                ClassicalCard.TEN_CLUB
        ));

    }

    @DisplayName("Exception when a player plays a not allowed card")
    @Test
    void testPlayNotAllowedCard() {

        trickWithHeartAsTrump.startTrick();

        when(playableCardsFilter.playableCards(any(), any())).thenReturn(Set.of(ClassicalCard.JACK_HEART));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> trickWithHeartAsTrump.playerPlays(player1, ClassicalCard.SEVEN_CLUB));

    }

    @DisplayName("A not started trick has no current player")
    @Test
    void testGetCurrentPlayer_notStartedTrick() {

        assertThat(trickWithHeartAsTrump.getCurrentPlayer()).isEmpty();

    }

}
