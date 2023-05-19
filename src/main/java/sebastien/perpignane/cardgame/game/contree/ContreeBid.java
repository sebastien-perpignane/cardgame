package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public record ContreeBid(ContreePlayer player, ContreeBidValue bidValue, CardSuit cardSuit) {

    public ContreeBid {
        if ((cardSuit == null || cardSuit == CardSuit.NONE) && bidValue.isCardSuitRequired()) {
            throw new IllegalArgumentException(
                String.format(
                    "null and NONE cardSuits are not allowed for bid with value %s. Allowed bid values with null cardSuit are : %s",
                    bidValue.name(),
                    ContreeBidValue.bidValuesNotRequiringCardSuit().stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(", "))
                )
            );
        }
    }

    public static Set<CardSuit> allowedCardSuitsWhenCardSuiteRequired() {
        return Arrays.stream(CardSuit.values())
                .filter(cs -> cs != CardSuit.NONE)
                .collect(Collectors.toSet());
    }

    /**
     * Convenient constructor to build a PASS bid.
     * @param player the bidding player
     */
    public ContreeBid(ContreePlayer player) {
        this(player, ContreeBidValue.PASS, null);
    }

    public ContreeBid(ContreePlayer player, ContreeBidValue bidValue) {
        this(player, bidValue, null);
    }

    public boolean isPass() {
        return bidValue == ContreeBidValue.PASS;
    }

    public boolean isDouble() {
        return bidValue == ContreeBidValue.DOUBLE;
    }

    public boolean isRedouble() {
        return bidValue == ContreeBidValue.REDOUBLE;
    }

}
