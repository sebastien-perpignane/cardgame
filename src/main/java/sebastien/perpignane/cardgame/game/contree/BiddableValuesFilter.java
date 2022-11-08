package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BiddableValuesFilter {

    public record BidFilterResult(Set<ContreeBidValue> biddableValues, Map<ContreeBidValue, String> exclusionCauseByBidValue) {
        public BidFilterResult {
            Objects.requireNonNull(biddableValues, "biddableValues cannot be null. It can be empty.");
            Objects.requireNonNull(exclusionCauseByBidValue, "exclusionCauseByBidValue cannot be null. It can be empty.");
        }
    }

    private final static Set<ContreeBidValue> allBidValuesExceptDoubleAndRedouble;

    static {
        allBidValuesExceptDoubleAndRedouble =
                Arrays.stream(ContreeBidValue.values())
                        .filter(bv -> bv != ContreeBidValue.DOUBLE && bv != ContreeBidValue.REDOUBLE)
                        .collect(Collectors.toSet());
    }

    public BidFilterResult biddableValues(ContreePlayer currentPlayer, ContreeDealBids bids) {

        Set<ContreeBidValue> biddableValues = new HashSet<>();
        Map<ContreeBidValue, String> exclusionCauseByBidValue = new HashMap<>();

        biddableValues.add(ContreeBidValue.PASS);

        exclusionCauseByBidValue.put(ContreeBidValue.DOUBLE, "Double is is only possible if an opponent bade before and this opponent made the highest bid");
        exclusionCauseByBidValue.put(ContreeBidValue.REDOUBLE, "Redouble is only possible if an opponent doubled before");

        biddableValues.add(ContreeBidValue.PASS);

        if (bids.highestBid().isEmpty() || bids.hasOnlyPassBids()) {
            biddableValues.addAll(allBidValuesExceptDoubleAndRedouble);
        }
        else {
            var highestBid = bids.highestBid().get();

            Predicate<ContreeBidValue> exclusionCausePredicate;

            if (!bids.isDoubleBidExists() && !bids.isRedoubleBidExists()) {
                biddableValues.addAll(
                        allBidValuesExceptDoubleAndRedouble.stream()
                                .filter(bv -> bv.compareTo(highestBid.bidValue()) > 0)
                                .collect(Collectors.toSet())
                );

                exclusionCausePredicate = bv -> bv.compareTo(highestBid.bidValue()) <= 0 && bv != ContreeBidValue.PASS;

            }

            else {
                exclusionCausePredicate = bv -> true;
            }

            exclusionCauseByBidValue.putAll(
                    allBidValuesExceptDoubleAndRedouble.stream()
                            .filter(exclusionCausePredicate)
                            .collect(Collectors.toMap(
                                    bv -> bv,
                                    bv -> String.format("Illegal bid as last bid value %s is higher or equals to your bid value", highestBid.bidValue())
                            ))
            );

            boolean isHighestBidMadeByTeamMate = highestBid.player().sameTeam(currentPlayer);

            if (isHighestBidMadeByTeamMate && bids.isDoubleBidExists()) {
                biddableValues.add(ContreeBidValue.REDOUBLE);
                exclusionCauseByBidValue.remove(ContreeBidValue.REDOUBLE);
            }

            if (!isHighestBidMadeByTeamMate && !bids.isDoubleBidExists()) {
                exclusionCauseByBidValue.remove(ContreeBidValue.DOUBLE);
                biddableValues.add(ContreeBidValue.DOUBLE);
            }
        }

        return new BidFilterResult(biddableValues, exclusionCauseByBidValue);

    }

}