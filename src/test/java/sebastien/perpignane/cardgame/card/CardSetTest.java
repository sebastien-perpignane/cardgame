package sebastien.perpignane.cardgame.card;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CardSetTest {

    @Test
    @DisplayName("Check the content of a 32 card set")
    public void test32CardGame() {

        var gameCards = CardSet.GAME_32.getGameCards();

        assertEquals(32, gameCards.size());
        assertTrue(gameCards.contains(Card.EIGHT_CLUB));
        assertFalse(gameCards.contains(Card.SIX_CLUB));
        assertFalse(gameCards.contains(Card.JOKER1));
    }

    @Test
    @DisplayName("Check the content of a 52 card set")
    public void test52CardGame() {

        var gameCards = CardSet.GAME_52.getGameCards();

        assertEquals(52, gameCards.size());
        assertTrue(gameCards.contains(Card.SIX_CLUB));
        assertFalse(gameCards.contains(Card.JOKER1));
        assertFalse(gameCards.contains(Card.JOKER2));
    }

    @Test
    @DisplayName("Check the content of a 54 card set")
    public void test54CardGame() {

        var gameCards = CardSet.GAME_54.getGameCards();

        assertEquals(Card.values().length, gameCards.size());
        assertTrue(Arrays.asList(Card.values()).containsAll(gameCards));
    }

}
