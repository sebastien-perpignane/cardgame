package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.util.PlayerSlot;

import java.util.*;
import java.util.function.Predicate;

class ContreeDealBids {

    private final static int INITIAL_MAX_BIDS = 4;

    private int maxBids = INITIAL_MAX_BIDS;

    private final List<ContreeBid> bids;

    private ContreeBidPlayers bidPlayers;

    private PlayerSlot<ContreePlayer> currentBidderSlot = new PlayerSlot<>();

    private BiddableValuesFilter.BidFilterResult currentBidderFilterResult;

    private final BiddableValuesFilter biddableValuesFilter;

    public ContreeDealBids(BiddableValuesFilter biddableValuesFilter) {
        bids = new ArrayList<>();
        this.biddableValuesFilter = biddableValuesFilter;
    }

    public void startBids(ContreeBidPlayers bidPlayers) {
        this.bidPlayers = bidPlayers;
        currentBidderSlot = bidPlayers.getCurrentBidderSlot();
        currentBidderFilterResult = biddableValuesFilter.biddableValues(currentBidderSlot.getPlayer().orElseThrow(), this);
        currentBidderSlot.getPlayer().orElseThrow().onPlayerTurnToBid(currentBidderFilterResult.biddableValues());
    }

    public void placeBid(ContreeBid bid) {

        throwsExceptionIfBidIsInvalid(bid);

        bids.add(bid);

        if (bid.isRedouble()) {
            return;
        }

        if (!bid.isPass()) {
            maxBids = Math.max(INITIAL_MAX_BIDS, bids.size() + ContreePlayers.NB_PLAYERS - 1);
        }

        if (bidsAreOver()) {
            currentBidderSlot = new PlayerSlot<>();
        }
        else {
            bidPlayers.goToNextBidder();
            currentBidderSlot = bidPlayers.getCurrentBidderSlot();
            currentBidderFilterResult = biddableValuesFilter.biddableValues(currentBidderSlot.getPlayer().orElseThrow(), this);
            currentBidderSlot.getPlayer().orElseThrow().onPlayerTurnToBid(currentBidderFilterResult.biddableValues());
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

        if (bidsAreOver()) {
            throw new BidNotAllowedException("Bids are over", true);
        }

        if (currentBidderSlot.getPlayer().orElseThrow() != bid.player()) {
            throw new BidNotAllowedException(String.format("Cheater detected : player %s is not the current bidder. Current bidder is: %s", bid.player(), currentBidderSlot.getPlayer().orElseThrow() ), true);
        }

        Map<ContreeBidValue, String> exclusionCauseByBidValue = currentBidderFilterResult.exclusionCauseByBidValue();
        if (exclusionCauseByBidValue.containsKey(bid.bidValue())) {
            throw new BidNotAllowedException(exclusionCauseByBidValue.get(bid.bidValue()));
        }

    }

    public boolean hasOnlyPassBids()  {
        var highestBid = highestBid();
        return highestBid.map(ContreeBid::isPass).orElse(false);
    }

    Optional<ContreeBid> highestBid() {
        return bids.stream()
                .filter(Predicate.not(bv -> List.of(ContreeBidValue.DOUBLE, ContreeBidValue.REDOUBLE)
                        .contains(bv.bidValue())))
                .max(Comparator.comparingInt(a -> a.bidValue().ordinal()));
    }

    boolean noBidsExceptPass() {
        return highestBid().isEmpty() || hasOnlyPassBids();
    }

    boolean noDoubleNorRedoubleBid() {
        return !isDoubleBidExists() && !isRedoubleBidExists();
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
        return highestBid().filter(Predicate.not(b -> b.bidValue() == ContreeBidValue.PASS));
    }

    public boolean containsBidValue(final ContreeBidValue bidValue) {
        return bids.stream().anyMatch(cb -> cb.bidValue() == bidValue);
    }

    public Optional<ContreePlayer> getCurrentBidder() {
        return currentBidderSlot.getPlayer();
    }

}
