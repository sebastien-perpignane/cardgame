package sebastien.perpignane.cardgame.game.contree;
import sebastien.perpignane.cardgame.card.contree.ValuableCard;

import java.util.Collection;

class CardsPointCalculator {

    public int computePoints(Collection<? extends ValuableCard> contreeCards) {

        return contreeCards.stream().mapToInt(ValuableCard::getGamePoints).sum();

    }

}
