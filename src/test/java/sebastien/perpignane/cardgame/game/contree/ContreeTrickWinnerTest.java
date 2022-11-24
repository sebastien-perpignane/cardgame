package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ContreeTrickWinnerTest extends TestCasesManagingPlayers {

    private ContreeTrick trickWithClubAsTrump;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    public void setUp() {
        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);
        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        var deal = MockDealBuilder.builder().withMockedGameEventSender().withTrumpSuit(CardSuit.CLUBS)
                .build();

        PlayableCardsFilter playableCardsFilter = mock(PlayableCardsFilter.class);

        when(playableCardsFilter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        trickWithClubAsTrump = new ContreeTrick(deal, "TEST", trickPlayers, playableCardsFilter);
    }

    @DisplayName("non trump trick where player playing ACE must win")
    @Test
    public void testExpectedWinner_noTrump_ace() {
        trickWithClubAsTrump.startTrick();

        trickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_DIAMOND);
        trickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_DIAMOND);
        trickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_DIAMOND);
        trickWithClubAsTrump.playerPlays(player4, ClassicalCard.JACK_DIAMOND);

        assertTrue(trickWithClubAsTrump.isOver());
        assertTrue(trickWithClubAsTrump.getWinner().isPresent());
        assertSame(player1, trickWithClubAsTrump.getWinner().get());

    }


    @DisplayName("non trump trick where player playing TEN must win")
    @Test
    public void testExpectedWinner_noTrump_ten() {
        trickWithClubAsTrump.startTrick();

        trickWithClubAsTrump.playerPlays(player1, ClassicalCard.TEN_DIAMOND);
        trickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_DIAMOND);
        trickWithClubAsTrump.playerPlays(player3, ClassicalCard.EIGHT_DIAMOND);
        trickWithClubAsTrump.playerPlays(player4, ClassicalCard.JACK_DIAMOND);

        assertTrue(trickWithClubAsTrump.isOver());
        assertTrue(trickWithClubAsTrump.getWinner().isPresent());
        assertSame(player1, trickWithClubAsTrump.getWinner().get());

    }

    @DisplayName("Trump trick where player playing JACK must win")
    @Test
    public void testExpectedWinner_trump_jack() {

        trickWithClubAsTrump.startTrick();

        trickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        trickWithClubAsTrump.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertTrue(trickWithClubAsTrump.isOver());

        assertTrue(trickWithClubAsTrump.getWinner().isPresent());
        assertSame(player4, trickWithClubAsTrump.getWinner().get());

    }

    @DisplayName("Trump trick where player playing NINE must win")
    @Test
    public void testExpectedWinner_trump_nine() {

        trickWithClubAsTrump.startTrick();

        trickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        trickWithClubAsTrump.playerPlays(player4, ClassicalCard.NINE_CLUB);

        assertTrue(trickWithClubAsTrump.isOver());
        assertTrue(trickWithClubAsTrump.getWinner().isPresent());
        assertSame(player4, trickWithClubAsTrump.getWinner().get());

    }

    @DisplayName("Trump trick where player playing ACE must win")
    @Test
    public void testExpectedWinner_trump_ace() {

        trickWithClubAsTrump.startTrick();

        trickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);
        trickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        trickWithClubAsTrump.playerPlays(player4, ClassicalCard.EIGHT_CLUB);

        assertTrue(trickWithClubAsTrump.isOver());
        assertTrue(trickWithClubAsTrump.getWinner().isPresent());
        assertSame(player1, trickWithClubAsTrump.getWinner().get());

    }

    @DisplayName("Trump trick where player playing TEN must win")
    @Test
    public void testExpectedWinner_trump_ten() {

        trickWithClubAsTrump.startTrick();

        trickWithClubAsTrump.playerPlays(player1, ClassicalCard.QUEEN_CLUB);
        trickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        trickWithClubAsTrump.playerPlays(player4, ClassicalCard.EIGHT_CLUB);

        assertTrue(trickWithClubAsTrump.isOver());
        assertTrue(trickWithClubAsTrump.getWinner().isPresent());
        assertSame(player3, trickWithClubAsTrump.getWinner().get());

    }

    @DisplayName("Trump trick where player playing KING must win")
    @Test
    public void testExpectedWinner_trump_king() {

        trickWithClubAsTrump.startTrick();

        trickWithClubAsTrump.playerPlays(player1, ClassicalCard.QUEEN_CLUB);
        trickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trickWithClubAsTrump.playerPlays(player3, ClassicalCard.KING_CLUB);
        trickWithClubAsTrump.playerPlays(player4, ClassicalCard.EIGHT_CLUB);

        assertTrue(trickWithClubAsTrump.isOver());
        assertTrue(trickWithClubAsTrump.getWinner().isPresent());
        assertSame(player3, trickWithClubAsTrump.getWinner().get());

    }

    @DisplayName("Trumped trick where trumping player must win")
    @Test
    public void testExpectedWinner_trumpedTrick() {

        trickWithClubAsTrump.startTrick();

        trickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_DIAMOND);
        trickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_DIAMOND);
        trickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        trickWithClubAsTrump.playerPlays(player4, ClassicalCard.JACK_DIAMOND);

        assertTrue(trickWithClubAsTrump.isOver());
        assertTrue(trickWithClubAsTrump.getWinner().isPresent());
        assertSame(player3, trickWithClubAsTrump.getWinner().get());

    }

    @DisplayName("Trump trick where player playing highest trump must win")
    @Test
    public void testExpectedWinner_multiTrumpedTrick() {

        trickWithClubAsTrump.startTrick();

        trickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_DIAMOND);
        trickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        trickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        trickWithClubAsTrump.playerPlays(player4, ClassicalCard.ACE_CLUB);

        assertTrue(trickWithClubAsTrump.isOver());
        assertTrue(trickWithClubAsTrump.getWinner().isPresent());
        assertSame(player4, trickWithClubAsTrump.getWinner().get());

    }

    @DisplayName("""
If a player plays a card with the highest rank of the trick
but the suit of his card is not the wanted suit of the trick, he does not win
    """)
    @Test
    public void testExpectedWinner_highestCardOnNotWantedSuit() {
        trickWithClubAsTrump.startTrick();

        trickWithClubAsTrump.playerPlays(player1, ClassicalCard.JACK_DIAMOND);
        trickWithClubAsTrump.playerPlays(player2, ClassicalCard.TEN_HEART);
        trickWithClubAsTrump.playerPlays(player3, ClassicalCard.ACE_HEART);
        trickWithClubAsTrump.playerPlays(player4, ClassicalCard.SEVEN_SPADE);

        assertTrue(trickWithClubAsTrump.isOver());
        assertTrue(trickWithClubAsTrump.getWinner().isPresent());
        assertSame(player1, trickWithClubAsTrump.getWinner().get());
    }

    @DisplayName("A low rank trump card will win a trick, even if higher wanted suit card has a higher rank than the trump card")
    @Test
    public void testExpectedWinner_trumpedTrick_trumpIsLowerRankThanHighestWantedSuitCard() {

        trickWithClubAsTrump.startTrick();

        trickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_DIAMOND);
        trickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_DIAMOND);
        trickWithClubAsTrump.playerPlays(player3, ClassicalCard.SEVEN_CLUB);
        trickWithClubAsTrump.playerPlays(player4, ClassicalCard.TEN_DIAMOND);

        assertTrue(trickWithClubAsTrump.isOver());
        assertTrue(trickWithClubAsTrump.getWinner().isPresent());
        assertSame(player3, trickWithClubAsTrump.getWinner().get());
    }

}
