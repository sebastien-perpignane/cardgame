package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContreeTrickLifeCycleTest extends TestCasesManagingPlayers {

    private ContreeTrick trickWithHeartTrump;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);
        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        ContreeDeal deal = mock(ContreeDeal.class);
        when(deal.getTrumpSuit()).thenReturn(CardSuit.HEARTS);

        PlayableCardsFilter filter = mock(PlayableCardsFilter.class);
        when(filter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        trickWithHeartTrump = new ContreeTrick(deal, "TEST", trickPlayers, filter);
    }

    @DisplayName("After the first played card, game is not over and winner is not available")
    @Test
    public void testTrickIsNotOverAfterOnePlayedCard() {

        trickWithHeartTrump.startTrick();

        trickWithHeartTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);

        assertFalse(trickWithHeartTrump.isOver());
        assertTrue(trickWithHeartTrump.getWinner().isEmpty());

    }

    @DisplayName("After two played cards, game is not over and winner is not available")
    @Test
    public void testTrickIsNotOverAfterTwoPlayedCards() {

        trickWithHeartTrump.startTrick();

        trickWithHeartTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trickWithHeartTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);

        assertFalse(trickWithHeartTrump.isOver());
        assertTrue(trickWithHeartTrump.getWinner().isEmpty());

    }

    @DisplayName("After three played cards, game is not over and winner is not available")
    @Test
    public void testTrickIsNotOverAfterThreePlayedCards() {
        trickWithHeartTrump.startTrick();

        trickWithHeartTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trickWithHeartTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trickWithHeartTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);

        assertFalse(trickWithHeartTrump.isOver());
        assertTrue(trickWithHeartTrump.getWinner().isEmpty());

    }

    @DisplayName("After four played cards, game is  over and trick winner is available")
    @Test
    public void testTrickIsOverAfterFourPlayedCards() {

        trickWithHeartTrump.startTrick();

        trickWithHeartTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trickWithHeartTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trickWithHeartTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        trickWithHeartTrump.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertTrue(trickWithHeartTrump.isOver());
        assertTrue(trickWithHeartTrump.getWinner().isPresent());
        assertSame(player1, trickWithHeartTrump.getWinner().orElseThrow());

    }

}
