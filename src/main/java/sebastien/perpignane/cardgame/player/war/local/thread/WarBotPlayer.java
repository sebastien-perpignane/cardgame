package sebastien.perpignane.cardgame.player.war.local.thread;

import com.github.javafaker.Faker;
import sebastien.perpignane.cardgame.player.war.MessageType;

import java.util.Objects;



public class WarBotPlayer extends AbstracLocalThreadWarPlayer {

    private final static Faker faker = new Faker();

    private final String name;

    public WarBotPlayer() {
        super();
        this.name = faker.name().firstName();
    }

    public String getName() {
        return name;
    }

    @Override
    void managePlayMessage(MessageType playMessage) {
        manageEmptyHandIfRelevant();
        playCard();
    }

    @Override
    public String toString() {
        return getName() + "_WarBot";
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
}
