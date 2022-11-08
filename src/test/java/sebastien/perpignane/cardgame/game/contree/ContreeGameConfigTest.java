package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContreeGameConfigTest  {

    private ContreeGameConfig config;

    @BeforeEach
    public void setUp() {
        config = new ContreeGameConfig();
    }

    @DisplayName("Loaded config values match values of the test config file")
    @Test
    void testConfigParametersValuesSameAsTestConfigFile() {

        assertFalse(config.isFileNotFound());

        assertEquals(1_000_000, config.maxScore());
        assertEquals(List.of(3, 3, 2), config.distributionConfiguration());

    }

    @DisplayName("Comma separated strings without spaces are correctly converted to list of Integer")
    @Test
    void testComaSeparatedToIntegerList_noSpaces() {

        assertFalse(config.isFileNotFound());

        List<Integer> mappedIntegerList = config.mapComaSeparatedStringToIntegerList("12,6,22");
        assertEquals(List.of(12, 6, 22), mappedIntegerList);
    }

    @DisplayName("Comma separated strings with spaces are correctly converted to list of Integer")
    @Test
    void testComaSeparatedToIntegerList_withSpaces() {
        List<Integer> mappedIntegerList = config.mapComaSeparatedStringToIntegerList("12, 6, 22");

        assertFalse(config.isFileNotFound());
        assertEquals(List.of(12, 6, 22), mappedIntegerList);
    }

    @DisplayName("If a contree game config cannot load the config file, fileNotFound flag is true")
    @Test
    void testCreateConfigWithBadFileName() {
        ContreeGameConfig gameConfig = new ContreeGameConfig("non-existing-file");
        assertTrue(gameConfig.isFileNotFound());
    }

}
