package sebastien.perpignane.cardgame.player.contree.refactor;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.player.contree.PlayerMessage;

import java.util.Iterator;
import java.util.Random;

public class ContreeBotPlayerEventHandler extends ThreadLocalContreePlayerEventHandler {

    public ContreeBotPlayerEventHandler() {
        super();
    }

    @Override
    void managePlayMessage(PlayerMessage playerMessage) {
        int cardIndex = new Random().nextInt(playerMessage.allowedCards().size());
        Iterator<ClassicalCard> cardIterator = playerMessage.allowedCards().iterator();
        ClassicalCard playedCard = null;
        int i = 0;
        while (i < (cardIndex == 0 ? 1 : cardIndex)) {
            playedCard = cardIterator.next();
            i++;
        }
        getGame().playCard(getPlayer(), playedCard);
    }

    protected final void placePassBid(ContreeBidValue bidValue, CardSuit cardSuit) {
        // FIXME create a placeBid method on contree player
        getGame().placeBid(getPlayer(), bidValue, cardSuit);
    }

    protected void placePassBid() {
        placePassBid(ContreeBidValue.PASS, null);
    }

    @Override
    void manageBidMessage(PlayerMessage bidMessage) {
        placePassBid();
    }

    @Override
    public boolean isBot() {
        return true;
    }
}
