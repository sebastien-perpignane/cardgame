package sebastien.perpignane.cardgame.player.contree.handlers;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.player.contree.ContreePlayerStatus;
import sebastien.perpignane.cardgame.player.contree.PlayerMessage;

import java.util.Collection;
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
        getPlayer().playCard(playedCard);
    }

    private void placePassBid() {
        getPlayer().placeBid(ContreeBidValue.PASS, null);
    }

    @Override
    void manageBidMessage(PlayerMessage bidMessage) {
        placePassBid();
    }

    @Override
    public void onReceivedHand(Collection<ClassicalCard> hand) {
        // Do nothing
    }

    @Override
    public void onStatusUpdate(ContreePlayerStatus oldStatus, ContreePlayerStatus newStatus) {
        // Do nothing
    }

    @Override
    public boolean isBot() {
        return true;
    }
}
