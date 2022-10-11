package sebastien.perpignane.cardgame.game.contree;

import org.apache.commons.collections4.iterators.LoopingListIterator;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.List;

public class ContreeBidPlayers {

    private ContreePlayer currentBidder;

    private final LoopingListIterator<ContreePlayer> playerLoopingIterator;

    public ContreeBidPlayers(List<ContreePlayer> players) {
        playerLoopingIterator = new LoopingListIterator<>(players);
    }

    public void goToNextBidder() {
        currentBidder = playerLoopingIterator.next();
    }

    public ContreePlayer getCurrentBidder() {
        return currentBidder;
    }

    public void onCurrentBidderTurnToBid() {
        currentBidder.onPlayerTurnToBid();
    }

}
