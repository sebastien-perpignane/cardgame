package sebastien.perpignane.cardgame.card;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.SortedSet;

import static org.assertj.core.api.Assertions.assertThat;

public class CardSetTest {

    @Test
    @DisplayName("Check the content of a 32 card set")
    public void test32CardGame() {
        SortedSet<ClassicalCard> gameCards = CardSet.GAME_32.getGameCards();

        assertThat(gameCards).hasSize(32);
        assertThat(gameCards).contains(ClassicalCard.EIGHT_CLUB);
        assertThat(gameCards).doesNotContain(ClassicalCard.SIX_CLUB, ClassicalCard.JOKER1);
    }

    @Test
    @DisplayName("Check the content of a 52 card set")
    public void test52CardGame() {
        SortedSet<ClassicalCard> gameCards = CardSet.GAME_52.getGameCards();

        assertThat(gameCards).hasSize(52);
        assertThat(gameCards).contains(ClassicalCard.SIX_CLUB);
        assertThat(gameCards).doesNotContain(ClassicalCard.JOKER1, ClassicalCard.JOKER2);
    }

    @Test
    @DisplayName("Check the content of a 54 card set")
    public void test54CardGame() {
        SortedSet<ClassicalCard> gameCards = CardSet.GAME_54.getGameCards();

        assertThat(ClassicalCard.values()).hasSize(gameCards.size());
        assertThat(ClassicalCard.values()).containsAll(gameCards);
    }

}
