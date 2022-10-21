package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContreeTrickLifeCycleTest extends TestCasesManagingPlayers {

    private ContreeTrickPlayers trickPlayers;

    private ContreeTrick trick;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {

        trickPlayers = mock(ContreeTrickPlayers.class);
        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        ContreeDeal deal = mock(ContreeDeal.class);
        when(deal.getTrumpSuit()).thenReturn(CardSuit.HEARTS);

        PlayableCardsFilter filter = mock(PlayableCardsFilter.class);
        when(filter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        trick = new ContreeTrick(deal, "TEST", trickPlayers, filter);
    }

    @Test
    public void testTrickIsNotOverAfterOnePlayedCard() {

        trick.startTrick();

        trick.playerPlays(player1, ClassicalCard.ACE_CLUB);

        assertFalse(trick.isOver());
        assertNull(trick.getWinner());

    }

    @Test
    public void testTrickIsNotOverAfterTwoPlayedCards() {

        trick.startTrick();

        trick.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trick.playerPlays(player2, ClassicalCard.SEVEN_CLUB);

        assertFalse(trick.isOver());
        assertNull(trick.getWinner());

    }

    @Test
    public void testTrickIsNotOverAfterThreePlayedCards() {
        trick.startTrick();

        trick.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trick.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(player3, ClassicalCard.TEN_CLUB);

        assertFalse(trick.isOver());
        assertNull(trick.getWinner());

    }

    @Test
    public void testTrickIsOverAfterFourPlayedCards() {

        trick.startTrick();

        trick.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trick.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trick.playerPlays(player3, ClassicalCard.TEN_CLUB);
        trick.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertTrue(trick.isOver());
        assertNotNull(trick.getWinner());

    }



}
