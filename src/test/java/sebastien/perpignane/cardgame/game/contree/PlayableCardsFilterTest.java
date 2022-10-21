package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PlayableCardsFilterTest extends TestCasesManagingPlayers {

    private CardSuit trickTrumpSuit;

    private ContreePlayer trickWinningPlayer;

    private List<PlayedCard> trickPlayedCards;

    private boolean isTrumpTrick;

    private ContreePlayer testedPlayer;

    private List<ClassicalCard> testedPlayerHand;

    private PlayableCardsFilter playableCardsFilter;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    public void setUp() {
        trickWinningPlayer = null;
        trickTrumpSuit = null;
        isTrumpTrick = false;
        playableCardsFilter = new PlayableCardsFilter();
    }

    private Collection<ClassicalCard> buildMocksAndRunTestOnTestedPlayer() {
        ContreeTrick trick = MockTrickBuilder.builder()
                .withTrumpSuit(trickTrumpSuit)
                .withPlayedCards(trickPlayedCards)
                .withWinningPlayer(trickWinningPlayer)
                .withIsTrumpTrick(isTrumpTrick)
                .build();

        when(testedPlayer.getHand()).thenReturn(testedPlayerHand);

        return playableCardsFilter.playableCards(trick, testedPlayer);
    }

    @Test
    @DisplayName("The first played card can be any card")
    public void testFirstPlayedCard() {

        trickTrumpSuit = CardSuit.HEARTS;
        trickPlayedCards = Collections.emptyList();

        testedPlayer = player1;
        testedPlayerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE);


        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertEquals(testedPlayerHand, playableCards);

    }

    @Test
    @DisplayName("When a fist card was played and the user has cards with same suit, he must play a card with same suit")
    public void testFirstCardAlreadyPlayed_compatibleCardAvailable_noTrump() {

        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer opponent = player1;
        trickPlayedCards = List.of(
                new PlayedCard(opponent, new ContreeCard(ClassicalCard.JACK_CLUB, trickTrumpSuit))
        );

        testedPlayer = player2;
        testedPlayerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE);

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertEquals(testedPlayerHand.stream().filter(c -> c.getSuit() == CardSuit.CLUBS).toList(), playableCards);


    }

    @Test
    @DisplayName("When a first card was played by opponent and the user does not have cards with same suit nor trumps, he can play any card")
    public void testFirstCardIsPlayed_noCompatibleCardAvailable_noTrump() {

        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer opponent = player1;

        testedPlayer = player2;
        testedPlayerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE);

        trickPlayedCards = List.of(
                new PlayedCard(opponent, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit))
        );

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertEquals(testedPlayerHand, playableCards);
    }

    @Test
    @DisplayName("When a fist card was played and the user does not have cards with same suit but has trump, he must trump")
    public void testFirstCardIsPlayed_noCompatibleCardAvailable_hasTrump() {

        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer opponent = player1;

        testedPlayer = player2;
        testedPlayerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_HEART);

        trickPlayedCards = List.of(
                new PlayedCard(opponent, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit))
        );

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertEquals(List.of(ClassicalCard.ACE_HEART), playableCards);

    }

    @DisplayName("An opponent played trump against the player teammate, player must over trump")
    @Test
    public void testFirstCardPlayed_notTrumpTrick_trumpCardPlayed_playerHasHigherCard() {
        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer teammate = player1;

        ContreePlayer opponent = player2;

        testedPlayer = player3;
        testedPlayerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.EIGHT_HEART, ClassicalCard.ACE_HEART);

        trickPlayedCards = List.of(
                new PlayedCard(teammate, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit)),
                new PlayedCard(opponent, new ContreeCard(ClassicalCard.TEN_HEART, trickTrumpSuit))
        );

        trickWinningPlayer = opponent;

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertEquals(List.of(ClassicalCard.ACE_HEART), playableCards);

    }

    @DisplayName("An opponent played trump against the player teammate, player must under trump")
    @Test
    public void testFirstCardPlayed_notTrumpTrick_trumpCardPlayed_playerHasNotHigherCard() {
        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer teammate = player1;

        ContreePlayer opponent = player2;

        testedPlayer = player3;
        testedPlayerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.EIGHT_HEART);

        trickPlayedCards = List.of(
                new PlayedCard(teammate, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit)),
                new PlayedCard(opponent, new ContreeCard(ClassicalCard.TEN_HEART, trickTrumpSuit))
        );

        trickWinningPlayer = opponent;

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertEquals(List.of(ClassicalCard.EIGHT_HEART), playableCards);
    }

    @DisplayName("An opponent played trump against the player teammate, player has no trump, he can play any card in his hand")
    @Test
    public void testNotTrumpTrick_trumpCardPlayed_playerHasNotTrump() {

        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer teammate = player1;

        ContreePlayer opponent = player2;

        testedPlayer = player3;
        testedPlayerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.EIGHT_SPADE);

        trickPlayedCards = List.of(
                new PlayedCard(teammate, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit)),
                new PlayedCard(opponent, new ContreeCard(ClassicalCard.TEN_HEART, trickTrumpSuit))
        );

        trickWinningPlayer = opponent;

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertEquals(testedPlayerHand, playableCards);

    }

    @DisplayName("The teammate is winning the trick, player has no card in the suit, any card can be played")
    @Test
    public void testNotTrumpTrick_teamMateIsWinningWithoutTrump() {

        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer opponent1 = player1;
        ContreePlayer teamMate = player2;
        ContreePlayer opponent2 = player3;

        trickPlayedCards = List.of(
                new PlayedCard(opponent1, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit)),
                new PlayedCard(teamMate, new ContreeCard(ClassicalCard.ACE_DIAMOND, trickTrumpSuit)),
                new PlayedCard(opponent2, new ContreeCard(ClassicalCard.EIGHT_DIAMOND, trickTrumpSuit))
        );

        trickWinningPlayer = teamMate;

        testedPlayer = player4;

        testedPlayerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE, ClassicalCard.EIGHT_HEART);

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertEquals(testedPlayerHand, playableCards);

    }

    @DisplayName("The teammate trumped and is winning the trick, any card can be played")
    @Test
    public void testFirstCardPlayed_notTrumpTrick_teamMateIsWinning() {
        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer opponent1 = player1;
        ContreePlayer teamMate = player2;
        ContreePlayer opponent2 = player3;

        trickPlayedCards = List.of(
                new PlayedCard(opponent1, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit)),
                new PlayedCard(teamMate, new ContreeCard(ClassicalCard.TEN_HEART, trickTrumpSuit)),
                new PlayedCard(opponent2, new ContreeCard(ClassicalCard.SEVEN_HEART, trickTrumpSuit))
        );

        trickWinningPlayer = teamMate;

        isTrumpTrick = false;

        testedPlayer = player4;

        testedPlayerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE, ClassicalCard.EIGHT_HEART);

        when(testedPlayer.sameTeam(teamMate)).thenReturn(true);
        when(teamMate.sameTeam(testedPlayer)).thenReturn(true);

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertEquals(testedPlayerHand, playableCards);

    }

}
