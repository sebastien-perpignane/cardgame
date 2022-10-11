package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSetShuffler;
import sebastien.perpignane.cardgame.card.CardRank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WarPlayer1WinShuffler implements CardSetShuffler {

    public final static List<CardRank> BEST_CARD_RANKS = Arrays.asList(CardRank.JACK, CardRank.QUEEN, CardRank.KING, CardRank.ACE);
    public final static List<CardRank> WORST_CARD_RANKS =Arrays.asList(CardRank.SIX, CardRank.SEVEN, CardRank.EIGHT, CardRank.NINE);

    @Override
    public List<ClassicalCard> shuffle(CardSet cSet) {
        List<ClassicalCard> bestCards = new ArrayList<>();
        List<ClassicalCard> worstCards = new ArrayList<>();


        for (ClassicalCard card : ClassicalCard.values()) {

            if (BEST_CARD_RANKS.contains(card.getRank())) {
                bestCards.add(card);
            }
            else if (WORST_CARD_RANKS.contains(card.getRank())) {
                worstCards.add(card);
            }

        }

        bestCards.addAll(worstCards);

        return bestCards;

    }
}
