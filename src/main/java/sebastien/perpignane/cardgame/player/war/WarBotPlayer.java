package sebastien.perpignane.cardgame.player.war;

import com.github.javafaker.Faker;
import sebastien.perpignane.cardgame.card.ClassicalCard;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;



public class WarBotPlayer extends AbstracLocalThreadWarPlayer {

    private final static Faker faker = new Faker();

    private Deque<ClassicalCard> hand;
    private final List<ClassicalCard> cardStock = new CopyOnWriteArrayList<>();

    private final String name;

    public WarBotPlayer() {
        super();
        this.name = faker.name().firstName();
    }

    public String getName() {
        return name;
    }

    @Override
    public Collection<ClassicalCard> getHand() {
        return hand;
    }

    @Override
    public void receiveHand(Collection<ClassicalCard> hand) {
        this.hand = new LinkedList<>(hand);
    }

    @Override
    public void receiveNewCards(Collection<ClassicalCard> cards) {
        cardStock.addAll(cards);
    }

    private void manageEmptyHandIfRelevant() {
        if (hand.isEmpty() && !cardStock.isEmpty()) {
            Collections.shuffle(cardStock);
            hand.addAll(cardStock);
            cardStock.clear();
        }
    }

    @Override
    public String toString() {
        return getName() + "_WarBot";
    }

    @Override
    public boolean hasNoMoreCard() {
        return (hand == null || hand.isEmpty()) && cardStock.isEmpty();
    }

    @Override
    public int nbAvailableCards() {
        return cardStock.size() + (hand == null ? 0 : hand.size());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WarBotPlayer)) return false;
        if (!super.equals(o)) return false;
        return name.equals(((WarBotPlayer)o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public boolean isBot() {
        return true;
    }

    @Override
    public void playCard(ClassicalCard card) {

    }
}
