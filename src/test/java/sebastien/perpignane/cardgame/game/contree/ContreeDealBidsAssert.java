package sebastien.perpignane.cardgame.game.contree;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.description.Description;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.function.Supplier;

public class ContreeDealBidsAssert extends AbstractAssert<ContreeDealBidsAssert, ContreeDealBids> {

    public ContreeDealBidsAssert(ContreeDealBids contreeDealBids) {
        super(contreeDealBids, ContreeDealBidsAssert.class);
    }

    @Override
    public ContreeDealBidsAssert as(String description, Object... args) {
        return super.as(description, args);
    }

    @Override
    public ContreeDealBidsAssert as(Supplier<String> descriptionSupplier) {
        return super.as(descriptionSupplier);
    }

    @Override
    public ContreeDealBidsAssert as(Description description) {
        return super.as(description);
    }

    @Override
    public ContreeDealBidsAssert describedAs(String description, Object... args) {
        return super.describedAs(description, args);
    }

    public ContreeDealBidsAssert bidsAreOver() {
        isNotNull();
        if (!actual.bidsAreOver()) {
            failWithMessage("Bids are not over");
        }
        return this;
    }

    public ContreeDealBidsAssert bidsAreNotOver() {
        isNotNull();
        if (actual.bidsAreOver()) {
            failWithMessage("Bids are over");
        }
        return this;
    }

    public ContreeDealBidsAssert hasHighestBidValueAs(ContreeBidValue bidValue) {
        isNotNull();
        isHighestBidPresent();
        ContreeBidValue actualBidValue = actual.highestBid().get().bidValue();
        if (actualBidValue != bidValue) {
            throw failureWithActualExpected(actualBidValue, bidValue, "Expected bid value is %s but found %s", bidValue, actualBidValue);
        }
        return this;
    }

    public ContreeDealBidsAssert hasHighestBidSuitAs(CardSuit cardSuit) {
        isNotNull();
        isHighestBidPresent();
        CardSuit actualCardSuit = actual.highestBid().get().cardSuit();
        if (actualCardSuit != cardSuit) {
            throw failureWithActualExpected(actualCardSuit, cardSuit, "Expected card suit is %s but found %s", cardSuit, actualCardSuit);
        }
        return this;
    }

    private void isHighestBidPresent() {
        if (actual.highestBid().isEmpty()) {
            failWithMessage("highest bid is empty");
        }
    }

    public ContreeDealBidsAssert hasContractBidValueAs(ContreeBidValue bidValue) {
        isNotNull();
        isContractBidPresent();
        ContreeBidValue actualBidValue = actual.findDealContractBid().get().bidValue();
        if (actualBidValue != bidValue) {
            throw failureWithActualExpected(actualBidValue, bidValue, "Expected bid value is %s but found %s", bidValue, actualBidValue);
        }
        return this;
    }

    public ContreeDealBidsAssert hasContractBidSuitAs(CardSuit cardSuit) {
        isNotNull();
        isContractBidPresent();
        CardSuit actualCardSuit = actual.findDealContractBid().get().cardSuit();
        if (actualCardSuit != cardSuit) {
            throw failureWithActualExpected(actualCardSuit, cardSuit, "Expected card suit is %s but found %s", cardSuit, actualCardSuit);
        }
        return this;
    }


    private void isContractBidPresent() {
        if (actual.findDealContractBid().isEmpty()) {
            failWithMessage("contract bid is empty");
        }
    }


    public ContreeDealBidsAssert hasDealContractBidFound() {
        isNotNull();
        if (actual.findDealContractBid().isEmpty()) {
            failWithMessage("contract deal bid is not found");
        }
        return this;
    }

    public ContreeDealBidsAssert hasNoDealContractBidFound() {
        isNotNull();
        if (actual.findDealContractBid().isPresent()) {
            failWithMessage("contract deal bid is found");
        }
        return this;
    }

    public ContreeDealBidsAssert doubleBidExists() {
        isNotNull();
        if (!actual.isDoubleBidExists()) {
            failWithMessage("double bid does not exist");
        }
        return this;
    }

    public ContreeDealBidsAssert doubleBidDoesNotExist() {
        isNotNull();
        if (actual.isDoubleBidExists()) {
            failWithMessage("double bid exists");
        }
        return this;
    }

    public ContreeDealBidsAssert redoubleBidExists() {
        isNotNull();
        if (!actual.isRedoubleBidExists()) {
            failWithMessage("redouble bid does not exist");
        }
        return this;
    }

    public ContreeDealBidsAssert redoubleBidDoesNotExist() {
        isNotNull();
        if (actual.isRedoubleBidExists()) {
            failWithMessage("redouble bid exists");
        }
        return this;
    }

    public ContreeDealBidsAssert hasAnnouncedCapot() {
        isNotNull();
        if(!actual.isAnnouncedCapot()) {
            failWithMessage("No announced capot");
        }
        return this;
    }

    public ContreeDealBidsAssert hasNotAnnouncedCapot() {
        isNotNull();
        if(actual.isAnnouncedCapot()) {
            failWithMessage("Has announced capot");
        }
        return this;
    }

    public ContreeDealBidsAssert hasCurrentBidderAs(ContreePlayer expectedBidder) {
        isNotNull();
        hasCurrentBidder();
        var actualBidder = actual.getCurrentBidder().get();
        if (actualBidder != expectedBidder) {
            throw failureWithActualExpected(actualBidder, expectedBidder, "Expected current bidder is %s but actual current bidder is%s", expectedBidder, actualBidder);
        }
        return this;
    }

    private void hasCurrentBidder() {
        if (actual.getCurrentBidder().isEmpty()) {
            failWithMessage("There is no current bidder");
        }
    }

    public static ContreeDealBidsAssert assertThat(ContreeDealBids dealBids) {
        return new ContreeDealBidsAssert(dealBids);
    }

}
