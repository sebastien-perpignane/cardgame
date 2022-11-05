package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.*;
import java.util.function.Predicate;

public class ContreeDealBids {

    private final static int INITIAL_MAX_BIDS = 4;

    private int maxBids = INITIAL_MAX_BIDS;

    private final List<ContreeBid> bids;

    private ContreeBidPlayers bidPlayers;

    private ContreePlayer currentBidder;

    private BiddableValuesFilter.BidFilterResult currentBidderFilterResult;

    private final BiddableValuesFilter biddableValuesFilter;

    public ContreeDealBids(BiddableValuesFilter biddableValuesFilter) {
        bids = new ArrayList<>();
        this.biddableValuesFilter = biddableValuesFilter;
    }

    public void startBids(ContreeBidPlayers bidPlayers) {
        this.bidPlayers = bidPlayers;
        currentBidder = bidPlayers.getCurrentBidder();
        currentBidderFilterResult = biddableValuesFilter.biddableValues(currentBidder, this);
        currentBidder.onPlayerTurnToBid(currentBidderFilterResult.biddableValues());
    }

    public void placeBid(ContreeBid bid) {

        throwsExceptionIfBidIsInvalid(bid);

        bids.add(bid);

        if (bid.isRedouble()) {
            return;
        }

        if (!bid.isNone()) {
            maxBids = Math.max(INITIAL_MAX_BIDS, bids.size() + ContreePlayers.NB_PLAYERS - 1);
        }

        if (!bidsAreOver()) {
            bidPlayers.goToNextBidder();
            currentBidder = bidPlayers.getCurrentBidder();
            currentBidderFilterResult = biddableValuesFilter.biddableValues(currentBidder, this);
            currentBidder.onPlayerTurnToBid(currentBidderFilterResult.biddableValues());
        }

    }

    public boolean bidsAreOver() {
        return bids.size() == maxBids || bids.stream().anyMatch(ContreeBid::isRedouble);
    }

    static class BidNotAllowedException extends RuntimeException  {

        private final boolean suspectedCheat;

        public BidNotAllowedException(String message) {
            this(message, false);
        }

        public BidNotAllowedException(String message, boolean suspectedCheat) {
            super(message);
            this.suspectedCheat = suspectedCheat;
        }

        public boolean isSuspectedCheat() {
            return suspectedCheat;
        }
    }

    private void throwsExceptionIfBidIsInvalid(ContreeBid bid) {

        Map<ContreeBidValue, String> exclusionCauseByBidValue = currentBidderFilterResult.exclusionCauseByBidValue();

        if (bidsAreOver()) {
            throw new BidNotAllowedException("Bids are over", true);
        }

        if (currentBidder != bid.player()) {
            throw new BidNotAllowedException(String.format("Cheater detected : player %s is not the current bidder. Current bidder is: %s", bid.player(), currentBidder ), true);
        }

        if (exclusionCauseByBidValue.containsKey(bid.bidValue())) {
            throw new BidNotAllowedException(exclusionCauseByBidValue.get(bid.bidValue()));
        }

    }

    public boolean hasOnlyNoneBids()  {
        var highestBid = highestBid();
        return highestBid.map(ContreeBid::isNone).orElse(false);
    }

    Optional<ContreeBid> highestBid() {
        return bids.stream()
                .filter(Predicate.not(bv -> List.of(ContreeBidValue.DOUBLE, ContreeBidValue.REDOUBLE)
                        .contains(bv.bidValue())))
                .max(Comparator.comparingInt(a -> a.bidValue().ordinal()));
    }

    public boolean isAnnouncedCapot() {
        return containsBidValue(ContreeBidValue.CAPOT);
    }

    public boolean isDoubleBidExists() {
        return containsBidValue(ContreeBidValue.DOUBLE);
    }

    public boolean isRedoubleBidExists() {
        return containsBidValue(ContreeBidValue.REDOUBLE);
    }

    public Optional<ContreeBid> findDealContractBid() {
        if (!bidsAreOver()) {
            return Optional.empty();
        }
        return highestBid().filter(Predicate.not(b -> b.bidValue() == ContreeBidValue.NONE));
    }

    public boolean containsBidValue(final ContreeBidValue bidValue) {
        return bids.stream().anyMatch(cb -> cb.bidValue() == bidValue);
    }

}
