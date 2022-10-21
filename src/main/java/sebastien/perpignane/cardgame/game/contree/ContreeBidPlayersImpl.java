package sebastien.perpignane.cardgame.game.contree;

import org.apache.commons.collections4.iterators.LoopingListIterator;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

public class ContreeBidPlayersImpl implements ContreeBidPlayers {

    private ContreePlayer currentBidder;

    private final LoopingListIterator<ContreePlayer> biddersIterator;

    public ContreeBidPlayersImpl(ContreeDealPlayers dealPlayers) {
        biddersIterator = new LoopingListIterator<>(dealPlayers.getCurrentDealPlayers());
        currentBidder = biddersIterator.next();
    }

    public void goToNextBidder() {
        currentBidder = biddersIterator.next();
    }

    public ContreePlayer getCurrentBidder() {
        return currentBidder;
    }

    public void onCurrentBidderTurnToBid() {
        currentBidder.onPlayerTurnToBid();
    }

}