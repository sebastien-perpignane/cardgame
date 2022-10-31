package sebastien.perpignane.cardgame.player.war;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.Team;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

enum MessageType {
    PLAY,
    END_OF_GAME
}

public class WarBotPlayer extends AbstractWarPlayer implements Runnable {

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

    private Deque<ClassicalCard> hand;
    private final List<ClassicalCard> cardStock = new CopyOnWriteArrayList<>();

    private final String name;

    public WarBotPlayer() {
        super();
        this.name = fakeNames.isEmpty() ? UUID.randomUUID().toString() : fakeNames.pop();
    }

    private String getName() {
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

    public ClassicalCard play() {
        manageEmptyHandIfRelevant();
        return hand.pop();
    }

    private void manageEmptyHandIfRelevant() {
        if (hand.isEmpty() && !cardStock.isEmpty()) {
            Collections.shuffle(cardStock);
            hand.addAll(cardStock);
            cardStock.clear();
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
    public void run() {

        while (!gameIsOver()) {
            try {
                String msg = gameMsgQueue.take();
                MessageType messageType = MessageType.valueOf(msg);
                switch (messageType) {
                    case PLAY:
                        if (isCurrentPlayer()) {
                            play(play());
                        }
                        break;
                    case END_OF_GAME:
                        return;
                    default:
                        System.err.println("Unknown message : " + msg);
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

    @Override
    public Optional<Team> getTeam() {
        return Optional.empty();
    }

    @Override
    public void setTeam(Team team) {

    }

    @Override
    public boolean isBot() {
        return true;
    }
}
