package sebastien.perpignane.cardgame.card;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CardSetShufflerImpl implements CardSetShuffler {

    @Override
    public List<Card> shuffle(CardSet cSet) {
        List<Card> result = new ArrayList<>(cSet.getGameCards());
        Collections.shuffle(result);
        return result;
    }

}
