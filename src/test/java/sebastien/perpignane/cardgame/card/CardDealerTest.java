package sebastien.perpignane.cardgame.card;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CardDealerTest {

    @DisplayName("Standard card dealing, according to Fédération Française de Belote")
    @Test
    public void testDealBy3then3then2() {

        final List<Integer> distributeConfig = List.of(3,3,2);
        final int nbPlayers = 4;
        final int nbCardsPerPlayer = distributeConfig.stream().mapToInt(Integer::intValue).sum();

        CardDealer cardDealer = new CardDealer(distributeConfig);

        var hands = cardDealer.dealCards(CardSet.GAME_32.getGameCards().stream().toList(), nbPlayers);

        var player1Hand = hands.get(0);
        var player2Hand = hands.get(1);
        var player3Hand = hands.get(2);
        var player4Hand = hands.get(3);

        assertThat(hands).hasSize(nbPlayers);
        assertThat(player1Hand).hasSize(nbCardsPerPlayer);
        assertThat(player2Hand).hasSize(nbCardsPerPlayer);
        assertThat(player3Hand).hasSize(nbCardsPerPlayer);
        assertThat(player4Hand).hasSize(nbCardsPerPlayer);

        checkAllHandIntersections(hands);

    }

    @DisplayName("Invalid arguments -> cannot equally deal 9 cards to 4 players with a 32 card set")
    @Test
    public void testInvalidDistributionConfig() {

        // 9 (3 then 3 then 3) cards per player x 4 players != 32 cards -> invalid config
        final List<Integer> distributionConfig = List.of(3,3,3);
        final int nbPlayers = 4;

        CardDealer cardDealer = new CardDealer(distributionConfig);

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
            () -> cardDealer.dealCards(CardSet.GAME_32.getGameCards().stream().toList(), nbPlayers)
        );

    }

    @DisplayName("32 cards cannot equally be dealt to 3 players -> exception")
    @Test
    public void testInvalidNbPlayersForNumberOfCards() {

        final List<Integer> distributeConfig = List.of(3,3,2);
        final int nbPlayers = 3;

        CardDealer cardDealer = new CardDealer(distributeConfig);

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
            () -> cardDealer.dealCards(CardSet.GAME_32.getGameCards().stream().toList(), nbPlayers)
        );
    }

    private void checkAllHandIntersections(List<List<ClassicalCard>> hands) {

        assertThat(
            hasIntersection(hands.get(0), hands.get(1))
        ).isFalse();
        assertThat(
            hasIntersection(hands.get(0), hands.get(2))
        ).isFalse();
        assertThat(
            hasIntersection(hands.get(0), hands.get(3))
        ).isFalse();


        assertThat(
            hasIntersection(hands.get(1), hands.get(2))
        ).isFalse();
        assertThat(
            hasIntersection(hands.get(1), hands.get(3))
        ).isFalse();


        assertThat(
            hasIntersection(hands.get(2), hands.get(3))
        ).isFalse();

    }

    @DisplayName("Testing a method only used for testing :D")
    @Test
    public void testIntersection() {

        assertThat(
            hasIntersection(
                List.of(ClassicalCard.ACE_SPADE, ClassicalCard.TEN_HEART),
                List.of(ClassicalCard.ACE_HEART, ClassicalCard.TEN_SPADE)
            )
        ).isFalse();

        assertThat(
            hasIntersection(
                    List.of(ClassicalCard.ACE_SPADE, ClassicalCard.TEN_HEART, ClassicalCard.NINE_CLUB),
                    List.of(ClassicalCard.NINE_CLUB, ClassicalCard.ACE_HEART, ClassicalCard.TEN_SPADE)
            )
        ).isTrue();

    }

    private boolean hasIntersection(final Collection<ClassicalCard> cards1, Collection<ClassicalCard> cards2) {
        return cards2.stream().anyMatch(cards1::contains);
    }

}
