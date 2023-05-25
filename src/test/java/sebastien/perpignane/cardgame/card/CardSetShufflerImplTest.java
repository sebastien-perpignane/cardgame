package sebastien.perpignane.cardgame.card;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CardSetShufflerImplTest {

    private final CardSetShufflerImpl cardSetShuffler = new CardSetShufflerImpl();
    
    @Test
    @DisplayName("Shuffle of a 32 card set")
    void testShuffle_32cards() {
        List<ClassicalCard> shuffledCards = cardSetShuffler.shuffle(CardSet.GAME_32);
        Set<ClassicalCard> shuffledCardSet = new HashSet<>(shuffledCards);

        assertThat(shuffledCards).hasSize(32);
        assertThat(shuffledCardSet).hasSize(32);
    }

    @Test
    @DisplayName("Shuffle of a 52 card set")
    void testShuffle_52cards() {
        List<ClassicalCard> shuffledCards = cardSetShuffler.shuffle(CardSet.GAME_52);
        Set<ClassicalCard> shuffledCardSet = new HashSet<>(shuffledCards);

        assertThat(shuffledCards).hasSize(52);
        assertThat(shuffledCardSet).hasSize(52);
    }

    @Test
    @DisplayName("Shuffle of a 54 card set")
    void testShuffle_54cards() {
        List<ClassicalCard> shuffledCards = cardSetShuffler.shuffle(CardSet.GAME_54);
        Set<ClassicalCard> shuffledCardSet = new HashSet<>(shuffledCards);

        assertThat(shuffledCards).hasSize(54);
        assertThat(shuffledCardSet).hasSize(54);
    }

}
