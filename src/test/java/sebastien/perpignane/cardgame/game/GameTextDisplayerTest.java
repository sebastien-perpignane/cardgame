package sebastien.perpignane.cardgame.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.game.contree.TestCasesManagingPlayers;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Tests for the class in charge of displaying events in the console")
class GameTextDisplayerTest extends TestCasesManagingPlayers {

    @DisplayName("A special text is displayed when the deal ends with a capot")
    @Test
    void testOnDealOver_capot() {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        var out = new PrintStream(bout);
        System.setOut(out);

        GameTextDisplayer gameTextDisplayer = GameTextDisplayer.getInstance();
        gameTextDisplayer.onDealOver("TEST", ContreeTeam.TEAM1, 250, 0, true);

        var output = bout.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("###      ###      ###              # ###                                                ###      ###      ###"));

    }

    @DisplayName("No special text is displayed when the deal does not end with a capot")
    @Test
    void testOnDealOver_noCapot() {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        var out = new PrintStream(bout);
        System.setOut(out);



        GameTextDisplayer gameTextDisplayer = GameTextDisplayer.getInstance();

        gameTextDisplayer.onDealOver("TEST", ContreeTeam.TEAM1, 250, 0, false);

        var output = bout.toString(StandardCharsets.UTF_8);
        assertFalse(output.contains("###      ###      ###              # ###                                                ###      ###      ###"));

    }

}