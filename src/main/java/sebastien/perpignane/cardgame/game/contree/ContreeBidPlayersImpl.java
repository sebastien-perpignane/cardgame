package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.util.PlayerSlot;

import java.util.Set;

class ContreeBidPlayersImpl implements ContreeBidPlayers {

    private final ContreeDealPlayers dealPlayers;

    private int currentBidderIndex;

    public ContreeBidPlayersImpl(ContreeDealPlayers dealPlayers) {
        this.dealPlayers = dealPlayers;
        currentBidderIndex = 0;
    }

    @Override
    public void goToNextBidder() {
        if (currentBidderIndex + 1 == dealPlayers.getCurrentDealPlayerSlots().size()) {
            currentBidderIndex = 0;

        }
        else {
            currentBidderIndex++;
        }
        var currentBidder = this.dealPlayers.getCurrentDealPlayers().get(currentBidderIndex);
        dealPlayers.setBiddingPlayer(currentBidder);
    }

    @Override
    public PlayerSlot<ContreePlayer> getCurrentBidderSlot() {
        return dealPlayers.getCurrentDealPlayerSlots().get(currentBidderIndex);
    }

    @Override
    public void onCurrentBidderTurnToBid(Set<ContreeBidValue> allowedBidValues) {
        getCurrentBidderSlot().getPlayer().orElseThrow().onPlayerTurnToBid(allowedBidValues);
    }

}