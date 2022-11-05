package sebastien.perpignane.cardgame.game.contree;

import org.apache.commons.collections4.iterators.LoopingListIterator;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Set;

public class ContreeBidPlayersImpl implements ContreeBidPlayers {

    private ContreePlayer currentBidder;

    private final LoopingListIterator<ContreePlayer> biddersIterator;

    public ContreeBidPlayersImpl(ContreeDealPlayers dealPlayers) {
        biddersIterator = new LoopingListIterator<>(dealPlayers.getCurrentDealPlayers());
        currentBidder = biddersIterator.next();
    }

    @Override
    public void goToNextBidder() {
        currentBidder = biddersIterator.next();
    }

    @Override
    public ContreePlayer getCurrentBidder() {
        return currentBidder;
    }

    @Override
    public void onCurrentBidderTurnToBid(Set<ContreeBidValue> allowedBidValues) {
        currentBidder.onPlayerTurnToBid(allowedBidValues);
    }

}