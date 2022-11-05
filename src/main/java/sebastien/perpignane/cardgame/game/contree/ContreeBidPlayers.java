package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Set;

public interface ContreeBidPlayers extends ContreePlayers {

    void goToNextBidder();

    ContreePlayer getCurrentBidder();

    void onCurrentBidderTurnToBid(Set<ContreeBidValue> allowedBidValues);

}
