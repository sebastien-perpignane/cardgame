package sebastien.perpignane.cardgame.card;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.*;

public class CardSetTest {

    @Test
    @DisplayName("Check the content of a 32 card set")
    public void test32CardGame() {

        SortedSet<ClassicalCard> gameCards = CardSet.GAME_32.getGameCards();

        assertEquals(32, gameCards.size());
        assertTrue(gameCards.contains(ClassicalCard.EIGHT_CLUB));
        assertFalse(gameCards.contains(ClassicalCard.SIX_CLUB));
        assertFalse(gameCards.contains(ClassicalCard.JOKER1));
    }

    @Test
    @DisplayName("Check the content of a 52 card set")
    public void test52CardGame() {

        SortedSet<ClassicalCard> gameCards = CardSet.GAME_52.getGameCards();

        assertEquals(52, gameCards.size());
        assertTrue(gameCards.contains(ClassicalCard.SIX_CLUB));
        assertFalse(gameCards.contains(ClassicalCard.JOKER1));
        assertFalse(gameCards.contains(ClassicalCard.JOKER2));
    }

    @Test
    @DisplayName("Check the content of a 54 card set")
    public void test54CardGame() {

        SortedSet<ClassicalCard> gameCards = CardSet.GAME_54.getGameCards();

        assertEquals(ClassicalCard.values().length, gameCards.size());
        assertTrue(Arrays.asList(ClassicalCard.values()).containsAll(gameCards));
    }

}
