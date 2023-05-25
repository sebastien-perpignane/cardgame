package sebastien.perpignane.cardgame.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardSetShufflerImpl implements CardSetShuffler {

    @Override
    public List<ClassicalCard> shuffle(CardSet cSet) {
        List<ClassicalCard> result = new ArrayList<>(cSet.getGameCards());
        Collections.shuffle(result);
        return result;
    }

}
