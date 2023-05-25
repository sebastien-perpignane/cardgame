package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ContreeTrickLifeCycleTest extends TestCasesManagingPlayers {

    private ContreeTrick trickWithHeartTrump;

    @BeforeAll
    static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);
        when(trickPlayers.getCurrentPlayerSlot()).thenAnswer(AdditionalAnswers.returnsElementsOf(playerSlots));

        ContreeDeal deal = mock(ContreeDeal.class);
        when(deal.getTrumpSuit()).thenReturn(CardSuit.HEARTS);

        PlayableCardsFilter filter = mock(PlayableCardsFilter.class);
        when(filter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        trickWithHeartTrump = new ContreeTrick(deal, "TEST", trickPlayers, filter);
    }

    @DisplayName("After the first played card, game is not over and winner is not available")
    @Test
    void testTrickIsNotOverAfterOnePlayedCard() {

        trickWithHeartTrump.startTrick();

        trickWithHeartTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);

        assertThat(trickWithHeartTrump.isOver()).isFalse();
        assertThat(trickWithHeartTrump.getWinner()).isEmpty();

    }

    @DisplayName("After two played cards, game is not over and winner is not available")
    @Test
    void testTrickIsNotOverAfterTwoPlayedCards() {

        trickWithHeartTrump.startTrick();

        trickWithHeartTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trickWithHeartTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);

        assertThat(trickWithHeartTrump.isOver()).isFalse();
        assertThat(trickWithHeartTrump.getWinner()).isEmpty();

    }

    @DisplayName("After three played cards, game is not over and winner is not available")
    @Test
    void testTrickIsNotOverAfterThreePlayedCards() {
        trickWithHeartTrump.startTrick();

        trickWithHeartTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trickWithHeartTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trickWithHeartTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);

        assertThat(trickWithHeartTrump.isOver()).isFalse();
        assertThat(trickWithHeartTrump.getWinner()).isEmpty();

    }

    @DisplayName("After four played cards, game is  over and trick winner is available")
    @Test
    void testTrickIsOverAfterFourPlayedCards() {

        trickWithHeartTrump.startTrick();

        trickWithHeartTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trickWithHeartTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trickWithHeartTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        trickWithHeartTrump.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertThat(trickWithHeartTrump.isOver()).isTrue();
        assertThat(trickWithHeartTrump.getWinner()).isPresent();
        assertThat(trickWithHeartTrump.getWinner().orElseThrow()).isSameAs(player1);

    }

}
