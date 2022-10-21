package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

public interface ContreeBidPlayers extends ContreePlayers {

    void goToNextBidder();

    ContreePlayer getCurrentBidder();

    void onCurrentBidderTurnToBid();

}
