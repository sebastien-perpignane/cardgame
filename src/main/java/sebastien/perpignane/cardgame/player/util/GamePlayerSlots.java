package sebastien.perpignane.cardgame.player.util;

import sebastien.perpignane.cardgame.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class GamePlayerSlots<P extends Player<?, ?>>  {

    private final List<PlayerSlot<P>> slots;

    public GamePlayerSlots(int nbSlots) {
        List<PlayerSlot<P>> tmpSlots = new ArrayList<>();
        for (int i = 0; i < nbSlots; i++) {
            tmpSlots.add(new PlayerSlot<>(i));
        }
        this.slots = Collections.unmodifiableList(tmpSlots);
    }

    public boolean contains(P player) {
        return slots.stream().anyMatch(s -> s.getPlayer().isPresent() && s.getPlayer().get() == player);
    }

    public Optional<P> addPlayerToSlotIndex(int slotNumber, P newPlayer) {
        var slot = getSlot(slotNumber);
        var replacedPlayer = slot.getPlayer();
        slot.setPlayer(newPlayer);
        return replacedPlayer;
    }

    public PlayerSlot<P> getSlot(int slotNumber) {
        checkSlotNumber(slotNumber);
        return slots.stream().filter(ps -> ps.getSlotNumber() == slotNumber).findFirst().orElseThrow();
    }

    public PlayerSlot<P> getSlot(P player) {
        return slots.stream().filter(ps -> ps.getPlayer().isPresent() && ps.getPlayer().get() == player).findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown player"));
    }

    private void checkSlotNumber(int slotNumber) throws IllegalArgumentException {
        if (slotNumber + 1 > slots.size()) {
            throw new IllegalArgumentException(String.format("slotIndex %d is not valid", slotNumber));
        }
    }

    public boolean isFull() {
        return slots.stream().noneMatch(s -> s.getPlayer().isEmpty() );
    }

    public boolean isEmpty() {
        return slots.stream().noneMatch( s -> s.getPlayer().isPresent() );
    }

    public boolean isJoinable() {
        return slots.stream().anyMatch( s -> s.getPlayer().isEmpty() || s.getPlayer().get().isBot() );
    }

    public Stream<PlayerSlot<P>> stream() {
        return slots.stream();
    }
}
