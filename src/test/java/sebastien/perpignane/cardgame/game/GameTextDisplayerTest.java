package sebastien.perpignane.cardgame.game;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.game.contree.ContreeDealScore;
import sebastien.perpignane.cardgame.game.contree.TestCasesManagingPlayers;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Tests for the class in charge of displaying events in the console")
public class GameTextDisplayerTest extends TestCasesManagingPlayers {

    private static ByteArrayOutputStream bout;

    private static PrintStream stdout;

    private static PrintStream fakeStdOut;

    @BeforeAll
    public static void globalSetUp() {
        overrideStdOut();
    }

    private static void overrideStdOut() {
        stdout = System.out;
        bout = new ByteArrayOutputStream();
        fakeStdOut = new PrintStream(bout);
        System.setOut(fakeStdOut);
    }

    @AfterAll
    public static void globalCleanUp() {
        restoreStdOut();
    }

    private static void restoreStdOut() {
        System.setOut(stdout);
        fakeStdOut.close();
    }

    @DisplayName("A special text is displayed when the deal ends with a capot")
    @Test
    void testOnDealOver_capot() {

        ContreeDealScore dealScore = mock(ContreeDealScore.class);

        GameTextDisplayer gameTextDisplayer = GameTextDisplayer.getInstance();
        gameTextDisplayer.onEndOfDeal("TEST", ContreeTeam.TEAM1, dealScore, true);

        var output = bout.toString(StandardCharsets.UTF_8);

        System.err.println(output);

        assertThat(output).isNotBlank().contains(GameTextDisplayer.CAPOT_ASCII);

    }

    @DisplayName("No special text is displayed when the deal does not end with a capot")
    @Test
    void testOnDealOver_noCapot() {

        ContreeDealScore dealScore = mock(ContreeDealScore.class);

        GameTextDisplayer gameTextDisplayer = GameTextDisplayer.getInstance();
        gameTextDisplayer.onEndOfDeal("TEST", ContreeTeam.TEAM1, dealScore, false);

        var output = bout.toString(StandardCharsets.UTF_8);

        System.err.println(output);
        assertThat(output).isNotBlank().doesNotContain(GameTextDisplayer.CAPOT_ASCII);

    }

}