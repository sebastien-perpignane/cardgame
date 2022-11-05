package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;

import java.util.*;


public class ContreeBotPlayer extends AbstractLocalThreadContreePlayer implements ContreePlayer, Runnable {

    private final String name;

    private static final Deque<String> fakeNames = new LinkedList<>();

    private static void addFakeNames() {
        fakeNames.add("Yanis");
        fakeNames.add("Matis");
        fakeNames.add("Noé");
        fakeNames.add("Sébastien");
        fakeNames.add("Imène");
        fakeNames.add("Enzo");
    }

    static {
        addFakeNames();
    }

    public ContreeBotPlayer() {
        this.name = fakeNames.isEmpty() ? UUID.randomUUID().toString() : fakeNames.pop();
    }

    protected void placeBid() {
        placeBid(ContreeBidValue.NONE, null);
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
        // TODO to be traced in an event manager
        // System.err.printf("Allowed cards : %s%n", playerMessage.allowedCards().stream().map(ClassicalCard::toString).collect(Collectors.joining(",")));
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
        System.err.printf("%s reacting to BID event%n", this);
        placeBid();
    }
}
