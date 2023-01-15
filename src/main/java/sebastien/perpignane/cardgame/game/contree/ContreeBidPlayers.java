package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.util.PlayerSlot;

import java.util.Set;

interface ContreeBidPlayers extends ContreePlayers {

    void goToNextBidder();

    PlayerSlot<ContreePlayer> getCurrentBidderSlot();

    void onCurrentBidderTurnToBid(Set<ContreeBidValue> allowedBidValues);

}
