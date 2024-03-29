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

class ContreeTrickWinnerTest extends TestCasesManagingPlayers {

    private ContreeTrick startedTrickWithClubAsTrump;

    @BeforeAll
    static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {
        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);
        when(trickPlayers.getCurrentPlayerSlot()).thenAnswer(AdditionalAnswers.returnsElementsOf(playerSlots));

        var deal = MockDealBuilder.builder().withMockedGameEventSender().withTrumpSuit(CardSuit.CLUBS)
                .build();

        PlayableCardsFilter playableCardsFilter = mock(PlayableCardsFilter.class);

        when(playableCardsFilter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        startedTrickWithClubAsTrump = new ContreeTrick(deal, "TEST", trickPlayers, playableCardsFilter);
        startedTrickWithClubAsTrump.startTrick();
    }

    @DisplayName("non trump trick where player playing ACE must win")
    @Test
    void testExpectedWinner_noTrump_ace() {

        startedTrickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_DIAMOND);
        startedTrickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_DIAMOND);
        startedTrickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_DIAMOND);
        startedTrickWithClubAsTrump.playerPlays(player4, ClassicalCard.JACK_DIAMOND);

        assertThat(startedTrickWithClubAsTrump.isOver()).isTrue();
        assertThat(startedTrickWithClubAsTrump.getWinner()).isPresent();
        assertThat(startedTrickWithClubAsTrump.getWinner()).containsSame(player1);

    }

    @DisplayName("non trump trick where player playing TEN must win")
    @Test
    void testExpectedWinner_noTrump_ten() {

        startedTrickWithClubAsTrump.playerPlays(player1, ClassicalCard.TEN_DIAMOND);
        startedTrickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_DIAMOND);
        startedTrickWithClubAsTrump.playerPlays(player3, ClassicalCard.EIGHT_DIAMOND);
        startedTrickWithClubAsTrump.playerPlays(player4, ClassicalCard.JACK_DIAMOND);

        assertThat(startedTrickWithClubAsTrump.isOver()).isTrue();
        assertThat(startedTrickWithClubAsTrump.getWinner()).isPresent();
        assertThat(startedTrickWithClubAsTrump.getWinner()).containsSame(player1);

    }

    @DisplayName("Trump trick where player playing JACK must win")
    @Test
    void testExpectedWinner_trump_jack() {

        startedTrickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertThat(startedTrickWithClubAsTrump.isOver()).isTrue();

        assertThat(startedTrickWithClubAsTrump.getWinner()).isPresent();
        assertThat(startedTrickWithClubAsTrump.getWinner()).containsSame(player4);

    }

    @DisplayName("Trump trick where player playing NINE must win")
    @Test
    void testExpectedWinner_trump_nine() {

        startedTrickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player4, ClassicalCard.NINE_CLUB);

        assertThat(startedTrickWithClubAsTrump.isOver()).isTrue();
        assertThat(startedTrickWithClubAsTrump.getWinner()).isPresent();
        assertThat(startedTrickWithClubAsTrump.getWinner()).containsSame(player4);

    }

    @DisplayName("Trump trick where player playing ACE must win")
    @Test
    void testExpectedWinner_trump_ace() {

        startedTrickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player4, ClassicalCard.EIGHT_CLUB);

        assertThat(startedTrickWithClubAsTrump.isOver()).isTrue();
        assertThat(startedTrickWithClubAsTrump.getWinner()).isPresent();
        assertThat(startedTrickWithClubAsTrump.getWinner()).containsSame(player1);

    }

    @DisplayName("Trump trick where player playing TEN must win")
    @Test
    void testExpectedWinner_trump_ten() {

        startedTrickWithClubAsTrump.playerPlays(player1, ClassicalCard.QUEEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player4, ClassicalCard.EIGHT_CLUB);

        assertThat(startedTrickWithClubAsTrump.isOver()).isTrue();
        assertThat(startedTrickWithClubAsTrump.getWinner()).isPresent();
        assertThat(startedTrickWithClubAsTrump.getWinner()).containsSame(player3);

    }

    @DisplayName("Trump trick where player playing KING must win")
    @Test
    void testExpectedWinner_trump_king() {

        startedTrickWithClubAsTrump.playerPlays(player1, ClassicalCard.QUEEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player3, ClassicalCard.KING_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player4, ClassicalCard.EIGHT_CLUB);

        assertThat(startedTrickWithClubAsTrump.isOver()).isTrue();
        assertThat(startedTrickWithClubAsTrump.getWinner()).isPresent();
        assertThat(startedTrickWithClubAsTrump.getWinner()).containsSame(player3);

    }

    @DisplayName("Trumped trick where trumping player must win")
    @Test
    void testExpectedWinner_trumpedTrick() {

        startedTrickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_DIAMOND);
        startedTrickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_DIAMOND);
        startedTrickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player4, ClassicalCard.JACK_DIAMOND);

        assertThat(startedTrickWithClubAsTrump.isOver()).isTrue();
        assertThat(startedTrickWithClubAsTrump.getWinner()).isPresent();
        assertThat(startedTrickWithClubAsTrump.getWinner()).containsSame(player3);

    }

    @DisplayName("Trump trick where player playing highest trump must win")
    @Test
    void testExpectedWinner_multiTrumpedTrick() {

        startedTrickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_DIAMOND);
        startedTrickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player4, ClassicalCard.ACE_CLUB);

        assertThat(startedTrickWithClubAsTrump.isOver()).isTrue();
        assertThat(startedTrickWithClubAsTrump.getWinner()).isPresent();
        assertThat(startedTrickWithClubAsTrump.getWinner()).containsSame(player4);

    }

    @DisplayName("""
If a player plays a card with the highest rank of the trick
but the suit of his card is not the wanted suit of the trick, he does not win
    """)
    @Test
    void testExpectedWinner_highestCardOnNotWantedSuit() {

        startedTrickWithClubAsTrump.playerPlays(player1, ClassicalCard.JACK_DIAMOND);
        startedTrickWithClubAsTrump.playerPlays(player2, ClassicalCard.TEN_HEART);
        startedTrickWithClubAsTrump.playerPlays(player3, ClassicalCard.ACE_HEART);
        startedTrickWithClubAsTrump.playerPlays(player4, ClassicalCard.SEVEN_SPADE);

        assertThat(startedTrickWithClubAsTrump.isOver()).isTrue();
        assertThat(startedTrickWithClubAsTrump.getWinner()).isPresent();
        assertThat(startedTrickWithClubAsTrump.getWinner()).containsSame(player1);
    }

    @DisplayName("A low rank trump card will win a trick, even if higher wanted suit card has a higher rank than the trump card")
    @Test
    void testExpectedWinner_trumpedTrick_trumpIsLowerRankThanHighestWantedSuitCard() {

        startedTrickWithClubAsTrump.playerPlays(player1, ClassicalCard.ACE_DIAMOND);
        startedTrickWithClubAsTrump.playerPlays(player2, ClassicalCard.SEVEN_DIAMOND);
        startedTrickWithClubAsTrump.playerPlays(player3, ClassicalCard.SEVEN_CLUB);
        startedTrickWithClubAsTrump.playerPlays(player4, ClassicalCard.TEN_DIAMOND);

        assertThat(startedTrickWithClubAsTrump.isOver()).isTrue();
        assertThat(startedTrickWithClubAsTrump.getWinner()).isPresent();
        assertThat(startedTrickWithClubAsTrump.getWinner()).containsSame(player3);
    }

}
