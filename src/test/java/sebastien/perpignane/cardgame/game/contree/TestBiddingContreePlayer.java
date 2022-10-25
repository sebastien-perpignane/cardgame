package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreeBotPlayer;

/**
 *
 * ContreePlayer designed for testing the bidding step of a deal. bid placed is configurable wih the constructor.
 */
public class TestBiddingContreePlayer extends ContreeBotPlayer {

    private final ContreeBidValue bidValue;

    private final CardSuit cardSuit;

    public TestBiddingContreePlayer(ContreeBidValue bidValue, CardSuit cardSuit) {
        super();
        this.bidValue = bidValue;
        this.cardSuit = cardSuit;
    }

    @Override
    public void placeBid() {
        placeBid(bidValue, cardSuit);
    }

}
