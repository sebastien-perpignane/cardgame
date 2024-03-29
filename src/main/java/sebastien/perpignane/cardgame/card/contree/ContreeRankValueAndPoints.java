package sebastien.perpignane.cardgame.card.contree;

import sebastien.perpignane.cardgame.card.CardRank;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * Card value and point configuration depending on the trump suit of the deal in which it is used.
 *
 (!!!!!) Please note that trumpValues are increased +20 in the constructor.

 It makes card comparison easier -> a trump has always a higher value against a non trump card.
    -> All trump cards stays comparable between each other as they all get a 20 bonus.
    -> All trump cards are considered stronger than any non trump card.

 */
public enum ContreeRankValueAndPoints {

    ACE(CardRank.ACE, 8, 6, 11),
    TEN(CardRank.TEN, 7, 5, 10),
    KING(CardRank.KING, 6, 4, 4),
    QUEEN(CardRank.QUEEN, 5, 3, 3),
    JACK(CardRank.JACK, 4, 8, 2, 20 ),
    NINE(CardRank.NINE, 3, 7, 0, 14),
    EIGHT(CardRank.EIGHT, 2, 0),
    SEVEN(CardRank.SEVEN, 1, 0)
    ;

    private final CardRank cardRank;
    private final int standardValue;
    private final int trumpValue;
    private final int standardPoints;
    private final int trumpPoints;

    private static final Map<CardRank, ContreeRankValueAndPoints> contreeCardEnumByValue;

    static {
        contreeCardEnumByValue = Arrays.stream(ContreeRankValueAndPoints.values()).collect(Collectors.toMap(
                cc -> cc.cardRank,
                cc -> cc
        ));
    }

    ContreeRankValueAndPoints(CardRank cardRank, int standardAndTrumpValues, int standardAndTrumpPoints) {
        this(cardRank, standardAndTrumpValues, standardAndTrumpValues, standardAndTrumpPoints, standardAndTrumpPoints);
    }

    ContreeRankValueAndPoints(CardRank rank, int standardValue, int trumpValue, int standardAndTrumpPoints) {
        this(rank, standardValue, trumpValue, standardAndTrumpPoints, standardAndTrumpPoints);
    }

    ContreeRankValueAndPoints(CardRank cardRank, int standardValue, int trumpValue, int standardPoints, int trumpPoints) {
        this.cardRank = cardRank;
        this.standardValue = standardValue;

        // Make card comparison easier -> a trump has always a higher value against a non trump card.
        // -> All trump cards stays comparable between each other as they all get a 20 bonus.
        // -> All trump cards are considered stronger than any non trump card.
        this.trumpValue = trumpValue + 20;
        this.standardPoints = standardPoints;
        this.trumpPoints = trumpPoints;
    }

    public int getStandardGameValue() {
        return standardValue;
    }

    public int getTrumpGameValue() {
        return trumpValue;
    }

    public int getStandardGamePoints() {
        return standardPoints;
    }

    public int getTrumpGamePoints() {
        return trumpPoints;
    }

    public static ContreeRankValueAndPoints getByCardValue(CardRank cardRank) {
        return contreeCardEnumByValue.get(cardRank);
    }

}
