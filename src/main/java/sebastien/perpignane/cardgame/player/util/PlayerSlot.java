package sebastien.perpignane.cardgame.player.util;

import sebastien.perpignane.cardgame.player.Player;

import java.util.Optional;

public class PlayerSlot<P extends Player<?, ?>> {

    private final int slotNumber;
    private P player;

    public PlayerSlot(int slotNumber) {
        this(slotNumber, null);
    }

    public PlayerSlot(int slotNumber, P player) {
        this.slotNumber = slotNumber;
        this.player = player;
    }

    public PlayerSlot() {
        this(-1, null);
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public Optional<P> getPlayer() {
        return Optional.ofNullable(player);
    }

    public void setPlayer(P player) {
        this.player = player;
    }

    public boolean isEmpty() {
        return player == null;
    }

    public boolean isPresent() {
        return !isEmpty();
    }

}
