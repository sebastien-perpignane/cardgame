package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.util.GamePlayerSlots;

import static org.junit.jupiter.api.Assertions.*;

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

        assertTrue( slot0.getPlayer().isEmpty() );

        var replacedPlayer = emptySlots.addPlayerToSlotIndex(slotNumber, player1);

        assertTrue( replacedPlayer.isEmpty() );
        assertTrue( slot0.getPlayer().isPresent() );
        assertSame( player1, slot0.getPlayer().get() );

    }

    @Test
    void testReplacePlayer() {

        int slotNumber = 2;

        var slot2 = fullSlots.getSlot(slotNumber);
        assertTrue( slot2.getPlayer().isPresent() );

        var replacedPlayer = fullSlots.addPlayerToSlotIndex( slotNumber, player1 );

        assertTrue( replacedPlayer.isPresent() );
        assertTrue( slot2.getPlayer().isPresent() );

        assertSame( player3, replacedPlayer.get() );
        assertSame( player1, slot2.getPlayer().get() );

    }

    @Test
    void testGetSlotByPlayer() {

        var player1Slot = fullSlots.getSlot(player1);
        assertEquals(0, player1Slot.getSlotNumber());

        var player2Slot = fullSlots.getSlot(player2);
        assertEquals(1, player2Slot.getSlotNumber());

        var player3Slot = fullSlots.getSlot(player3);
        assertEquals(2, player3Slot.getSlotNumber());

        var player4Slot = fullSlots.getSlot(player4);
        assertEquals(3, player4Slot.getSlotNumber());

    }

}