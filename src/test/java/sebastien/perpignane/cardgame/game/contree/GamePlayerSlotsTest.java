package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.util.GamePlayerSlots;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for PlayerSlots")
class GamePlayerSlotsTest extends TestCasesManagingPlayers {

    private GamePlayerSlots<ContreePlayer> emptySlots;

    private GamePlayerSlots<ContreePlayer> fullSlots;

    @BeforeAll
    static void beforeAll() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {
        emptySlots = new GamePlayerSlots<>(4);
        fullSlots = new GamePlayerSlots<>(4);
        fullSlots.addPlayerToSlotIndex(0, player1);
        fullSlots.addPlayerToSlotIndex(1, player2);
        fullSlots.addPlayerToSlotIndex(2, player3);
        fullSlots.addPlayerToSlotIndex(3, player4);
    }

    @Test
    void testAddPlayer() {

        int slotNumber = 0;

        var slot0 = emptySlots.getSlot(slotNumber);

        assertThat(slot0.getPlayer()).isEmpty();

        var replacedPlayer = emptySlots.addPlayerToSlotIndex(slotNumber, player1);

        assertThat(replacedPlayer).isEmpty();
        assertThat(slot0.getPlayer()).isPresent();
        assertThat(slot0.getPlayer()).containsSame(player1);

    }

    @Test
    void testReplacePlayer() {

        int slotNumber = 2;

        var slot2 = fullSlots.getSlot(slotNumber);
        assertThat(slot2.getPlayer()).isPresent();

        var replacedPlayer = fullSlots.addPlayerToSlotIndex( slotNumber, player1 );

        assertThat(replacedPlayer).isPresent();
        assertThat(slot2.getPlayer()).isPresent();

        assertThat(replacedPlayer).containsSame(player3);
        assertThat(slot2.getPlayer()).containsSame(player1);

    }

    @Test
    void testGetSlotByPlayer() {

        var player1Slot = fullSlots.getSlot(player1);
        assertThat(player1Slot.getSlotNumber()).isZero();

        var player2Slot = fullSlots.getSlot(player2);
        assertThat(player2Slot.getSlotNumber()).isEqualTo(1);

        var player3Slot = fullSlots.getSlot(player3);
        assertThat(player3Slot.getSlotNumber()).isEqualTo(2);

        var player4Slot = fullSlots.getSlot(player4);
        assertThat(player4Slot.getSlotNumber()).isEqualTo(3);

    }

}