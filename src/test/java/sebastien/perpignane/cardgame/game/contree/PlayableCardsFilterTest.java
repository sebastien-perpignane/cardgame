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
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class PlayableCardsFilterTest extends TestCasesManagingPlayers {

    private CardSuit trickTrumpSuit;

    private ContreePlayer trickWinningPlayer;

    private List<ContreePlayedCard> trickPlayedCards;

    private boolean isTrumpTrick;

    private ContreePlayer testedPlayer;

    private Set<ClassicalCard> testedPlayerHand;

    private PlayableCardsFilter playableCardsFilter;

    @BeforeAll
    static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {
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
    void testFirstPlayedCard() {

        trickTrumpSuit = CardSuit.HEARTS;
        trickPlayedCards = Collections.emptyList();

        testedPlayer = player1;
        testedPlayerHand = Set.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE);


        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertThat(playableCards).isEqualTo(testedPlayerHand);

    }

    @Test
    @DisplayName("When a fist card was played and the user has cards with same suit, he must play a card with same suit")
    void testFirstCardAlreadyPlayed_compatibleCardAvailable_noTrump() {

        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer opponent = player1;
        trickPlayedCards = List.of(
                new ContreePlayedCard(opponent, new ContreeCard(ClassicalCard.JACK_CLUB, trickTrumpSuit))
        );

        testedPlayer = player2;
        testedPlayerHand = Set.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE);

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertThat(playableCards).isEqualTo(testedPlayerHand.stream().filter(c -> c.getSuit() == CardSuit.CLUBS).collect(Collectors.toSet()));


    }

    @Test
    @DisplayName("When a first card was played by opponent and the user does not have cards with same suit nor trumps, he can play any card")
    void testFirstCardIsPlayed_noCompatibleCardAvailable_noTrump() {

        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer opponent = player1;

        testedPlayer = player2;
        testedPlayerHand = Set.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE);

        trickPlayedCards = List.of(
                new ContreePlayedCard(opponent, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit))
        );

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertThat(playableCards).isEqualTo(testedPlayerHand);
    }

    @Test
    @DisplayName("When a fist card was played and the user does not have cards with same suit but has trump, he must trump")
    void testFirstCardIsPlayed_noCompatibleCardAvailable_hasTrump() {

        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer opponent = player1;

        testedPlayer = player2;
        testedPlayerHand = Set.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_HEART);

        trickPlayedCards = List.of(
                new ContreePlayedCard(opponent, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit))
        );

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertThat(playableCards).isEqualTo(Set.of(ClassicalCard.ACE_HEART));

    }

    @DisplayName("An opponent played trump against the player teammate, player must over trump when he has trump cards")
    @Test
    void testFirstCardPlayed_notTrumpTrick_trumpCardPlayed_playerHasHigherCard() {
        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer teammate = player1;

        ContreePlayer opponent = player2;

        testedPlayer = player3;
        testedPlayerHand = Set.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.EIGHT_HEART, ClassicalCard.ACE_HEART);

        trickPlayedCards = List.of(
                new ContreePlayedCard(teammate, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit)),
                new ContreePlayedCard(opponent, new ContreeCard(ClassicalCard.TEN_HEART, trickTrumpSuit))
        );

        trickWinningPlayer = opponent;

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertThat(playableCards).isEqualTo(Set.of(ClassicalCard.ACE_HEART));

    }

    @DisplayName("An opponent played trump against the player teammate, player must under trump as he has trump")
    @Test
    void testFirstCardPlayed_notTrumpTrick_trumpCardPlayed_playerHasNotHigherCard() {
        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer teammate = player1;

        ContreePlayer opponent = player2;

        testedPlayer = player3;
        testedPlayerHand = Set.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.EIGHT_HEART);

        trickPlayedCards = List.of(
                new ContreePlayedCard(teammate, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit)),
                new ContreePlayedCard(opponent, new ContreeCard(ClassicalCard.TEN_HEART, trickTrumpSuit))
        );

        trickWinningPlayer = opponent;

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertThat(playableCards).isEqualTo(Set.of(ClassicalCard.EIGHT_HEART));
    }

    @DisplayName("An opponent played trump against the player teammate, player has no trump, he can play any card in his hand")
    @Test
    void testNotTrumpTrick_trumpCardPlayed_playerHasNotTrump() {

        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer teammate = player1;

        ContreePlayer opponent = player2;

        testedPlayer = player3;
        testedPlayerHand = Set.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.EIGHT_SPADE);

        trickPlayedCards = List.of(
                new ContreePlayedCard(teammate, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit)),
                new ContreePlayedCard(opponent, new ContreeCard(ClassicalCard.TEN_HEART, trickTrumpSuit))
        );

        trickWinningPlayer = opponent;

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertThat(playableCards).isEqualTo(testedPlayerHand);

    }

    @DisplayName("The teammate is winning the trick, player has no card in the suit, any card can be played")
    @Test
    void testNotTrumpTrick_teamMateIsWinningWithoutTrump() {

        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer opponent1 = player1;
        ContreePlayer teamMate = player2;
        ContreePlayer opponent2 = player3;

        trickPlayedCards = List.of(
                new ContreePlayedCard(opponent1, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit)),
                new ContreePlayedCard(teamMate, new ContreeCard(ClassicalCard.ACE_DIAMOND, trickTrumpSuit)),
                new ContreePlayedCard(opponent2, new ContreeCard(ClassicalCard.EIGHT_DIAMOND, trickTrumpSuit))
        );

        trickWinningPlayer = teamMate;

        testedPlayer = player4;

        testedPlayerHand = Set.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE, ClassicalCard.EIGHT_HEART);

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertThat(playableCards).isEqualTo(testedPlayerHand);

    }

    @DisplayName("The teammate trumped and is winning the trick, wanted suit not available, any card can be played")
    @Test
    void testFirstCardPlayed_notTrumpTrick_teamMateIsWinning() {
        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer opponent1 = player1;
        ContreePlayer teamMate = player2;
        ContreePlayer opponent2 = player3;

        trickPlayedCards = List.of(
                new ContreePlayedCard(opponent1, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit)),
                new ContreePlayedCard(teamMate, new ContreeCard(ClassicalCard.TEN_HEART, trickTrumpSuit)),
                new ContreePlayedCard(opponent2, new ContreeCard(ClassicalCard.SEVEN_HEART, trickTrumpSuit))
        );

        trickWinningPlayer = teamMate;

        isTrumpTrick = false;

        testedPlayer = player4;

        testedPlayerHand = Set.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE, ClassicalCard.EIGHT_HEART);

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertThat(playableCards).isEqualTo(testedPlayerHand);

    }

    @DisplayName("The teammate trumped and is winning the trick, the tested player has the wanted color, he must play the wanted color")
    @Test
    void testFirstCardPlayed_trumpedTrick_teamMateIsWinning_playerHasWantedColor() {
        trickTrumpSuit = CardSuit.HEARTS;

        ContreePlayer opponent1 = player1;
        ContreePlayer teamMate = player2;
        ContreePlayer opponent2 = player3;

        trickPlayedCards = List.of(
                new ContreePlayedCard(opponent1, new ContreeCard(ClassicalCard.JACK_DIAMOND, trickTrumpSuit)),
                new ContreePlayedCard(teamMate, new ContreeCard(ClassicalCard.TEN_HEART, trickTrumpSuit)),
                new ContreePlayedCard(opponent2, new ContreeCard(ClassicalCard.SEVEN_HEART, trickTrumpSuit))
        );

        trickWinningPlayer = teamMate;

        isTrumpTrick = false;

        testedPlayer = player4;

        testedPlayerHand = Set.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.TEN_DIAMOND, ClassicalCard.ACE_SPADE, ClassicalCard.EIGHT_HEART);

        var playableCards = buildMocksAndRunTestOnTestedPlayer();

        assertThat(playableCards).isEqualTo(Set.of(ClassicalCard.TEN_DIAMOND));

    }

}
