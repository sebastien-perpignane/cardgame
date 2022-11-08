package sebastien.perpignane.cardgame.player.contree;

import com.github.javafaker.Faker;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;

import java.util.Iterator;
import java.util.Random;


public class ContreeBotPlayer extends AbstractLocalThreadContreePlayer {

    @Override
    public boolean isBot() {
        return true;
    }

    private final String name;

    private final static Faker faker = new Faker();

    public ContreeBotPlayer() {
        this.name = faker.name().firstName();
    }

    protected void placeBid() {
        placeBid(ContreeBidValue.PASS, null);
    }

    protected final void placeBid(ContreeBidValue bidValue, CardSuit cardSuit) {
        getGame().placeBid(this, bidValue, cardSuit);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{name=" + name + "-BOT, " +
                "team=" + getTeam().orElseThrow() +'}';
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
        getHand().remove(playedCard);
        getGame().playCard(this, playedCard);
    }

    @Override
    void manageBidMessage(PlayerMessage bidMessage) {
        placeBid();
    }
}
