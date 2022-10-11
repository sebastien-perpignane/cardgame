package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ContreeDealBids {

    private final static int INITIAL_MAX_BIDS = 4;

    private int maxBids = INITIAL_MAX_BIDS;

    private final List<ContreeBid> bids = new ArrayList<>();

    private final ContreeBidPlayers bidPlayers;

    public ContreeDealBids(ContreeBidPlayers bidPlayers) {
        this.bidPlayers = bidPlayers;
    }

    public void startBids() {
        bidPlayers.goToNextBidder();
        bidPlayers.onCurrentBidderTurnToBid();
    }

    public void placeBid(ContreeBid bid) {

        // FIXME find a way to share event sender
        // eventSender.sendPlacedBidEvent(dealId, bid);

        throwsExceptionIfBidIsInvalid(bid);

        bids.add(bid);

        if (bid.isRedouble()) {
            return;
        }

        if (!bid.isNone()) {
            maxBids = Math.max(INITIAL_MAX_BIDS, bids.size() + ContreeGame.NB_PLAYERS - 1);
        }

        if (!bidsAreOver()) {
            bidPlayers.goToNextBidder();
            bidPlayers.onCurrentBidderTurnToBid();
        }

    }

    public boolean bidsAreOver() {
        return bids.size() == maxBids || bids.stream().anyMatch(ContreeBid::isRedouble);
    }

    private void throwsExceptionIfBidIsInvalid(ContreeBid bid) {

        if (bidsAreOver()) {
            throw new IllegalStateException("Bids are over");
        }

        if (bidPlayers.getCurrentBidder() != bid.player()) {
            throw new IllegalStateException(String.format("Cheater detected : player %s is not the current bidder. Current bidder is: %s", bid.player(), bidPlayers.getCurrentBidder()));
        }

        // Cannot double if no bid <> NONE exists
        // Cannot redouble if no double bid exists
        if (bid.isDouble()) {
            var highestBid = highestBid();
            if (highestBid.isEmpty() || highestBid().orElseThrow().bidValue() == ContreeBidValue.NONE) {
                throw new IllegalStateException("Double is not allowed if no player has bid before");
            }
            if (highestBid().orElseThrow().player().getTeam().orElseThrow() == bid.player().getTeam().orElseThrow()) {
                throw new IllegalStateException("A player cannot double his team mate");
            }

        }

        // Cannot double if highest bid if from team mate
        // Cannot redouble if double bid if from team mate
        if (bid.isRedouble()) {
            if (!isDoubleBidExists()) {
                throw new IllegalStateException("Redouble is not allowed if no player has doubled before");
            }

            if (isDoubleBidExists() && doublePlayer().orElseThrow().getTeam().orElseThrow() == bid.player().getTeam().orElseThrow()) {
                throw new IllegalStateException("Redouble is not allowed if doubling player is in the same team");
            }
        }

        if (!bids.isEmpty() && !bid.isNone()) {
            var highestBid = highestBid();
            boolean overBid = highestBid.orElseThrow().bidValue().ordinal() < bid.bidValue().ordinal();
            if (!overBid) {
                throw new IllegalArgumentException(String.format("Illegal bid as last bid value %s is higher than current bid value (%s)", highestBid.orElseThrow().bidValue(), bid.bidValue()));
            }
        }
    }

    public boolean hasOnlyNoneBids()  {
        var highestBid = highestBid();
        if (highestBid.isEmpty()) {
            throw new IllegalStateException("No bid");
        }
        return highestBid.get().isNone();
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
        return highestBid().filter(Predicate.not(b -> b.bidValue() == ContreeBidValue.NONE));
    }

    public boolean containsBidValue(final ContreeBidValue bidValue) {
        return bids.stream().anyMatch(cb -> cb.bidValue() == bidValue);
    }

    private Optional<? extends Player> doublePlayer() {
        return bids.stream().filter(cb -> cb.bidValue() == ContreeBidValue.DOUBLE).map(ContreeBid::player).findFirst();
    }


}
