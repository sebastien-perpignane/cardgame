package sebastien.perpignane.cardgame.card.contree;

import sebastien.perpignane.cardgame.card.Card;

public interface ValuableCard extends Card {

    int getGameValue();

    int getGamePoints();

}
