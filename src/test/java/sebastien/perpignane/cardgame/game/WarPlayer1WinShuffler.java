package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.card.Card;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSetShuffler;
import sebastien.perpignane.cardgame.card.CardValue;

import java.util.ArrayList;
import java.util.List;

public class WarPlayer1WinShuffler implements CardSetShuffler {

    public final static List<CardValue> bestCardValues = List.of(CardValue.JACK, CardValue.QUEEN, CardValue.KING, CardValue.ACE);
    public final static List<CardValue> worstCardValues = List.of(CardValue.SIX, CardValue.SEVEN, CardValue.EIGHT, CardValue.NINE);

    @Override
    public List<Card> shuffle(CardSet cSet) {
        List<Card> bestCards = new ArrayList<>();
        List<Card> worstCards = new ArrayList<>();


        for (Card card : Card.values()) {

            if (bestCardValues.contains(card.getValue())) {
                bestCards.add(card);
            }
            else if (worstCardValues.contains(card.getValue())) {
                worstCards.add(card);
            }

        }

        bestCards.addAll(worstCards);

        return bestCards;

    }
}
