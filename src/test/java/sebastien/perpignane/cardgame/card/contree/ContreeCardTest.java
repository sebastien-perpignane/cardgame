package sebastien.perpignane.cardgame.card.contree;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;

import static org.assertj.core.api.Assertions.assertThat;

class ContreeCardTest {

    @ParameterizedTest
    @EnumSource(names = {"DIAMONDS", "HEARTS"})
    void testOf(CardSuit trumpSuit) {

        var result = ContreeCard.of(trumpSuit, CardSet.GAME_32.getGameCards());

        assertThat(result.stream().filter(ContreeCard::isTrump).toList()).hasSize(8);

    }

    @Test
    void testOfAllTrumps() {

        var result = ContreeCard.ofAllTrumps(CardSet.GAME_32.getGameCards());

        assertThat(result.stream()).allMatch(ContreeCard::isTrump);
        assertThat(result).hasSize(32);

    }

    @Test
    void testOfNoTrumps() {

        var result = ContreeCard.ofNoTrumps(CardSet.GAME_32.getGameCards());

        assertThat(result.stream()).noneMatch(ContreeCard::isTrump);
        assertThat(result).hasSize(32);

    }
}