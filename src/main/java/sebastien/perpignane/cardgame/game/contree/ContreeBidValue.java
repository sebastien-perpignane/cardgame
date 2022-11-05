package sebastien.perpignane.cardgame.game.contree;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

public enum ContreeBidValue {

    PASS(null, false),
    EIGHTY(80),
    NINETY(90),
    HUNDRED(100),
    HUNDRED_TEN(110),
    HUNDRED_TWENTY(120),
    HUNDRED_THIRTY(130),
    HUNDRED_FORTY(140),
    HUNDRED_FIFTY(150),
    HUNDRED_SIXTY(160),
    CAPOT(250),
    DOUBLE(null, false),
    REDOUBLE(null, false);

    private final boolean cardSuitRequired;

    private final Integer expectedScore;

    ContreeBidValue(Integer expectedScore) {
        this( expectedScore,true);
    }

    ContreeBidValue(Integer expectedScore, boolean cardSuitRequired) {
        this.expectedScore = expectedScore;
        this.cardSuitRequired = cardSuitRequired;
    }

    public boolean isCardSuitRequired() {
        return cardSuitRequired;
    }

    public Integer getExpectedScore() {
        return expectedScore;
    }

    public static Collection<ContreeBidValue> bidValuesNotRequiringCardSuit() {
        return Arrays.stream(ContreeBidValue.values()).filter(Predicate.not(ContreeBidValue::isCardSuitRequired)).toList();
    }

}
