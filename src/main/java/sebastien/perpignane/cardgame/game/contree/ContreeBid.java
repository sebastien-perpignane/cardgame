package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.stream.Collectors;

record ContreeBid(ContreePlayer player, ContreeBidValue bidValue, CardSuit cardSuit) {

    public ContreeBid {
        if (cardSuit == null && bidValue.isCardSuitRequired()) {
            throw new IllegalArgumentException(
                String.format(
                    "null cardSuit is not allowed for bid with value %s. Allowed bid values with null cardSuit are : %s",
                    bidValue.name(),
                    ContreeBidValue.bidValuesNotRequiringCardSuit().stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(", "))
                )
            );
        }
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
