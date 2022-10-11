package sebastien.perpignane.cardgame.card;

import java.util.List;

public interface CardSetShuffler {
    List<ClassicalCard> shuffle(CardSet cSet);
}
