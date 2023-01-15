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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContreeTrickTest extends TestCasesManagingPlayers {

    private PlayableCardsFilter playableCardsFilter;

    private ContreeTrick trickWithHeartAsTrump;


    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    public void setUp() {
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
    public void testThrowsExceptionWhenNotExpectedPlayerPlays() {
        trickWithHeartAsTrump.startTrick();

        assertThrows(
            RuntimeException.class,
            () -> trickWithHeartAsTrump.playerPlays(player2, ClassicalCard.JACK_HEART)
        );
    }

    @DisplayName("winning player on a trick without played card is null")
    @Test
    public void testWinningPlayerWhenNoPlayedCardThrowsException() {
        assertNull(trickWithHeartAsTrump.winningPlayer());
    }

    @DisplayName("winning player on a trick with played card is consistent")
    @Test
    public void testWinningPlayerWhenCardsWerePlayed() {
        trickWithHeartAsTrump.startTrick();

        trickWithHeartAsTrump.playerPlays(player1, ClassicalCard.JACK_CLUB);
        assertSame(player1, trickWithHeartAsTrump.winningPlayer());

        trickWithHeartAsTrump.playerPlays(player2, ClassicalCard.TEN_CLUB);
        assertSame(player2, trickWithHeartAsTrump.winningPlayer());

        trickWithHeartAsTrump.playerPlays(player3, ClassicalCard.ACE_CLUB);
        assertSame(player3, trickWithHeartAsTrump.winningPlayer());

        trickWithHeartAsTrump.playerPlays(player4, ClassicalCard.SEVEN_HEART);
        assertSame(player4, trickWithHeartAsTrump.winningPlayer());

    }

    @DisplayName("winning player on a trick with played card is consistent")
    @Test
    public void testPlayCardWhenTrickIsOver() {
        trickWithHeartAsTrump.startTrick();

        trickWithHeartAsTrump.playerPlays(player1, ClassicalCard.JACK_CLUB);
        trickWithHeartAsTrump.playerPlays(player2, ClassicalCard.TEN_CLUB);
        trickWithHeartAsTrump.playerPlays(player3, ClassicalCard.ACE_CLUB);
        trickWithHeartAsTrump.playerPlays(player4, ClassicalCard.SEVEN_HEART);

        assertThrows(
            RuntimeException.class,
            () -> trickWithHeartAsTrump.playerPlays(player1, ClassicalCard.JACK_HEART)
        );

    }

    @DisplayName("getAllCards reflects cards played during the trick")
    @Test
    public void testGetPlayedCards() {

        trickWithHeartAsTrump.startTrick();

        trickWithHeartAsTrump.playerPlays(player1, ClassicalCard.JACK_CLUB);
        trickWithHeartAsTrump.playerPlays(player2, ClassicalCard.ACE_CLUB);
        trickWithHeartAsTrump.playerPlays(player3, ClassicalCard.TEN_CLUB);

        assertEquals(
            Set.of(
                    ClassicalCard.JACK_CLUB,
                    ClassicalCard.ACE_CLUB,
                    ClassicalCard.TEN_CLUB
            ),
            trickWithHeartAsTrump.getAllCards()
        );

    }

    @DisplayName("Exception when a player plays a not allowed card")
    @Test
    public void testPlayNotAllowedCard() {

        trickWithHeartAsTrump.startTrick();

        when(playableCardsFilter.playableCards(any(), any())).thenReturn(Set.of(ClassicalCard.JACK_HEART));
        assertThrows(
            RuntimeException.class,
                () -> trickWithHeartAsTrump.playerPlays(player1, ClassicalCard.SEVEN_CLUB)
        );

    }

    @DisplayName("A not started trick has no current player")
    @Test
    public void testGetCurrentPlayer_notStartedTrick() {

        assertTrue(trickWithHeartAsTrump.getCurrentPlayer().isEmpty());

    }

    /*@DisplayName("Updating current player of a not started trick fails")
    @Test
    public void testUpdateCurrentPlayer_notStartedTrick() {

        ContreePlayer newPlayer = mock(ContreePlayer.class);
        when(newPlayer.isBot()).thenReturn(false);

        assertTrue(trickWithHeartAsTrump.getCurrentPlayer().isEmpty());

        assertThrows(
            RuntimeException.class,
            () -> trickWithHeartAsTrump.updateCurrentPlayer(newPlayer)
        );

    }


    @DisplayName("Updating current player of a started trick is OK")
    @Test
    public void testUpdateCurrentPlayer_startedTrick() {

        trickWithHeartAsTrump.startTrick();

        ContreePlayer newPlayer = mock(ContreePlayer.class);
        when(newPlayer.isBot()).thenReturn(false);

        assertTrue(trickWithHeartAsTrump.getCurrentPlayer().isPresent());
        assertSame(player1, trickWithHeartAsTrump.getCurrentPlayer().get());

        trickWithHeartAsTrump.updateCurrentPlayer(newPlayer);

        assertTrue(trickWithHeartAsTrump.getCurrentPlayer().isPresent());
        assertSame(newPlayer, trickWithHeartAsTrump.getCurrentPlayer().get());

    }

    @DisplayName("Updating current player of an over trick fails")
    @Test
    public void testUpdateCurrentPlayer_overTrick() {

        trickWithHeartAsTrump.startTrick();

        trickWithHeartAsTrump.playerPlays(player1, ClassicalCard.JACK_CLUB);
        trickWithHeartAsTrump.playerPlays(player2, ClassicalCard.JACK_CLUB);
        trickWithHeartAsTrump.playerPlays(player3, ClassicalCard.JACK_CLUB);
        trickWithHeartAsTrump.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertTrue(trickWithHeartAsTrump.isOver());

        ContreePlayer newPlayer = mock(ContreePlayer.class);
        when(newPlayer.isBot()).thenReturn(false);

        assertThrows(
            RuntimeException.class,
            () -> trickWithHeartAsTrump.updateCurrentPlayer(newPlayer)
        );

    }*/

}
