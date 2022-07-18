package sebastien.perpignane.cardgame.card;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

public class CardSetShufflerImplTest {

    private final CardSetShufflerImpl cardSetShuffler = new CardSetShufflerImpl();

    @Test
    @DisplayName("Shuffle of a 32 card set")
    public void testMix_32cards() {
        var result = cardSetShuffler.shuffle(CardSet.GAME_32);

        Assertions.assertEquals(32, result.size());
        Assertions.assertEquals(32, new HashSet<>(result).size());

    }

    @Test
    @DisplayName("Shuffle of a 52 card set")
    public void testMix_52cards() {
        var result = cardSetShuffler.shuffle(CardSet.GAME_52);

        Assertions.assertEquals(52, result.size());
        Assertions.assertEquals(52, new HashSet<>(result).size());

    }

    @Test
    @DisplayName("Shuffle of a 54 card set")
    public void testMix_54cards() {
        var result = cardSetShuffler.shuffle(CardSet.GAME_52);

        Assertions.assertEquals(52, result.size());
        Assertions.assertEquals(52, new HashSet<>(result).size());

    }

}
