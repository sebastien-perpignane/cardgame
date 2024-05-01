package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Compute allowed bids depending on the current deal state
 */
class BiddableValuesFilter {

    /**
     * Contains the biddable values and the cause of exclusion for the not biddable ones.
     */
    public static class BidFilterResult {

        private final Set<ContreeBidValue> biddableValues;

        private final Map<ContreeBidValue, String> exclusionCauseByBidValue;

        public BidFilterResult() {
            biddableValues = new HashSet<>();
            exclusionCauseByBidValue = new EnumMap<>(ContreeBidValue.class);
        }

        public Set<ContreeBidValue> biddableValues() {
            return biddableValues;
        }

        public Map<ContreeBidValue, String> exclusionCauseByBidValue() {
            return exclusionCauseByBidValue;
        }

        private void addBiddableValues(Set<ContreeBidValue> biddableValues) {
            this.biddableValues.addAll(biddableValues);
        }

        private void addBiddableValue(ContreeBidValue biddableValue) {
            biddableValues.add(biddableValue);
        }

        private void addExclusionCauseForBidValue(String exclusionCause, ContreeBidValue bidValue) {
            exclusionCauseByBidValue.put(bidValue, exclusionCause);
        }

        private void addExclusionCausesForBidValues(Map<ContreeBidValue, String> newExclusionCausesByBidValue) {
            exclusionCauseByBidValue.putAll(newExclusionCausesByBidValue);
        }

        private void removeExclusionCauseForBidValue(ContreeBidValue bidValue) {
            exclusionCauseByBidValue.remove(bidValue);
        }

    }

    private static final Set<ContreeBidValue> allBidValuesExceptDoubleAndRedouble;

    static {
        allBidValuesExceptDoubleAndRedouble =
                Arrays.stream(ContreeBidValue.values())
                        .filter(bv -> bv != ContreeBidValue.DOUBLE && bv != ContreeBidValue.REDOUBLE)
                        .collect(Collectors.toSet());
    }

    /**
     *
     * @param currentPlayer biddable values are computed for this player
     * @param bids The bids of the current deal
     * @return see {@link BidFilterResult} description
     */
    public BidFilterResult biddableValues(ContreePlayer currentPlayer, ContreeDealBids bids) {

        Objects.requireNonNull(currentPlayer);
        Objects.requireNonNull(bids);

        BidFilterResult bidFilterResult = new BidFilterResult();

        setInitialResultAsPassAllowedAndDoubleRedoubleNotAllowed(bidFilterResult);

        if (bids.noBidsExceptPass()) {
            bidFilterResult.addBiddableValues(allBidValuesExceptDoubleAndRedouble);
            return bidFilterResult;
        }

        var highestBid = bids.highestBid().orElseThrow();

        if (bids.noDoubleNorRedoubleBid()) {
            bidFilterResult.addBiddableValues(
                getBidValuesHigherThan(highestBid.bidValue())
            );
        }

        setExclusionCauseForNotBiddableValues(bidFilterResult, highestBid.bidValue());

        if (opponentHasHighestNotDoubledBid(bids, currentPlayer)) {
            setDoubleBidAsAllowed(bidFilterResult);
        }

        if (teamMateIsDoubled(bids, currentPlayer)) {
            setRedoubleBidAsAllowed(bidFilterResult);
        }

        return bidFilterResult;

    }

    private void setInitialResultAsPassAllowedAndDoubleRedoubleNotAllowed(BidFilterResult bidFilterResult) {

        bidFilterResult.addBiddableValue(ContreeBidValue.PASS);

        bidFilterResult.addExclusionCauseForBidValue("Double is only possible if an opponent bade before and this opponent made the highest bid", ContreeBidValue.DOUBLE);
        bidFilterResult.addExclusionCauseForBidValue("Redouble is only possible if an opponent doubled before", ContreeBidValue.REDOUBLE);

    }

    private Set<ContreeBidValue> getBidValuesHigherThan(ContreeBidValue bidValue) {
        return allBidValuesExceptDoubleAndRedouble.stream()
                .filter(bv -> bv.compareTo(bidValue) > 0)
                .collect(Collectors.toSet());
    }

    private void setExclusionCauseForNotBiddableValues(BidFilterResult bidFilterResult, ContreeBidValue highestBidValue) {
        bidFilterResult.addExclusionCausesForBidValues(
            allBidValuesExceptDoubleAndRedouble.stream()
                .filter(Predicate.not(bidFilterResult.biddableValues::contains))
                .collect(
                    Collectors.toMap(
                        bv -> bv,
                        bv -> String.format("Illegal bid as last bid value %s is higher or equals to your bid value %s", highestBidValue, bv)
                    )
                )
        );
    }

    private boolean opponentHasHighestNotDoubledBid(ContreeDealBids bids, ContreePlayer currentPlayer) {
        return isHighestBidMadeByOpponent(bids.highestBid().orElseThrow(), currentPlayer) && !bids.isDoubleBidExists();
    }

    private boolean teamMateIsDoubled(ContreeDealBids bids, ContreePlayer currentPlayer) {
        var highestBid = bids.highestBid().orElseThrow();
        return isHighestBidMadeByTeamMate(highestBid, currentPlayer) && bids.isDoubleBidExists();
    }

    private boolean isHighestBidMadeByTeamMate(ContreeBid highestBid, ContreePlayer currentPlayer) {
        return highestBid.player().sameTeam(currentPlayer);
    }

    private boolean isHighestBidMadeByOpponent(ContreeBid highestBid, ContreePlayer currentPlayer) {
        return !isHighestBidMadeByTeamMate(highestBid, currentPlayer);
    }

    private void setRedoubleBidAsAllowed(BidFilterResult bidFilterResult) {
        bidFilterResult.addBiddableValue(ContreeBidValue.REDOUBLE);
        bidFilterResult.removeExclusionCauseForBidValue(ContreeBidValue.REDOUBLE);
    }

    private void setDoubleBidAsAllowed(BidFilterResult bidFilterResult) {
        bidFilterResult.addBiddableValue(ContreeBidValue.DOUBLE);
        bidFilterResult.removeExclusionCauseForBidValue(ContreeBidValue.DOUBLE);
    }

}