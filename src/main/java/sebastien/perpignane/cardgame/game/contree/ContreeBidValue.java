package sebastien.perpignane.cardgame.game.contree;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

public enum ContreeBidValue {

    PASS("Pass"),
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
    DOUBLE("Double"),
    REDOUBLE("Redouble");

    private final boolean cardSuitRequired;

    private final Integer expectedScore;

    private final String label;

    ContreeBidValue(Integer expectedScore) {
        this( expectedScore,true, null);
    }

    ContreeBidValue(String label) {
        this(null, false, label);
    }

    ContreeBidValue(Integer expectedScore, boolean cardSuitRequired, String label) {
        this.expectedScore = expectedScore;
        this.cardSuitRequired = cardSuitRequired;
        if (label != null) {
            this.label = label;
        }
        else if (expectedScore != null) {
            this.label = expectedScore.toString();
        }
        else {
            throw new IllegalArgumentException("A non null value is required for expectedScore or label");
        }
    }

    public boolean isCardSuitRequired() {
        return cardSuitRequired;
    }

    public Integer getExpectedScore() {
        return expectedScore;
    }

    public String getLabel() {
        return label;
    }

    public static Collection<ContreeBidValue> bidValuesNotRequiringCardSuit() {
        return Arrays.stream(ContreeBidValue.values()).filter(Predicate.not(ContreeBidValue::isCardSuitRequired)).toList();
    }

}
