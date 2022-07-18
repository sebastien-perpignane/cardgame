package sebastien.perpignane.cardgame.player;

import sebastien.perpignane.cardgame.card.Card;
import sebastien.perpignane.cardgame.game.PlayedCard;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

enum MessageType {
    PLAY,
    END_OF_GAME
}

public class WarBotPlayer extends AbstractPlayer implements Runnable {

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

    private final BlockingQueue<String> gameMsgQueue = new ArrayBlockingQueue<>(54);

    private Deque<Card> hand;
    private final List<Card> cardStock = new ArrayList<>();

    private final String name;

    public WarBotPlayer() {
        super();
        this.name = fakeNames.isEmpty() ? UUID.randomUUID().toString() : fakeNames.pop();
    }

    private String getName() {
        return name;
    }

    @Override
    public void receiveHand(Collection<Card> hand) {
        this.hand = new LinkedList<>(hand);
    }

    @Override
    public void receiveNewCards(Collection<Card> cards) {
        synchronized (cardStock) {
            cardStock.addAll(cards);
        }

    }

    @Override
    public Collection<Card> getHand() {
        return hand;
    }

    public Card play() {
        manageEmptyHandIfRelevant();
        return hand.pop();
    }

    private void manageEmptyHandIfRelevant() {
        synchronized (cardStock) {
            if (hand.isEmpty() && !cardStock.isEmpty()) {
                Collections.shuffle(cardStock);
                hand.addAll(cardStock);
                cardStock.clear();
            }
        }
    }

    public void onPlayerTurn() {
        gameMsgQueue.add("PLAY");
    }

    @Override
    public void onGameOver() {
        gameMsgQueue.add("END_OF_GAME");
    }

    @Override
    public String toString() {
        return String.format("%s_WarBot", getName());
    }

    @Override
    public boolean hasNoMoreCard() {
        return (hand == null || hand.isEmpty()) && cardStock.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WarBotPlayer that)) return false;
        if (!super.equals(o)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    @Override
    public void run() {

        while (!gameIsOver()) {
            try {
                var msg = gameMsgQueue.take();
                MessageType messageType = MessageType.valueOf(msg);
                switch (messageType) {
                    case PLAY:
                        if (isCurrentPlayer()) {
                            play(new PlayedCard(this, play()));
                        }
                        break;
                    case END_OF_GAME:
                        return;
                    default:
                        System.err.printf("Unknown message : %s", msg);
                }
            }
            catch (InterruptedException ie) {
                System.out.println("I'm interrupted");
                return;
            }
        }
    }

    @Override
    public void onGameStarted() {
        new Thread(this).start();
    }
}
