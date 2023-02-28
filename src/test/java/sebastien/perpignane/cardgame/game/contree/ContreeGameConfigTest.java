package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ContreeGameConfigTest  {

    private ContreeGameConfig config;

    @BeforeEach
    public void setUp() {
        config = new ContreeGameConfig();
    }

    @DisplayName("Loaded config values match values of the test config file")
    @Test
    void testConfigParametersValuesSameAsTestConfigFile() {

        assertThat( config.isFileNotFound() ).isFalse();

        assertThat( config.maxScore() ).isEqualTo(1_000_000);
        assertThat( config.distributionConfiguration() ).isEqualTo(List.of(3, 3, 2));

    }

    @DisplayName("Comma separated strings without spaces are correctly converted to list of Integer")
    @Test
    void testComaSeparatedToIntegerList_noSpaces() {

        assertThat( config.isFileNotFound() ).isFalse();

        List<Integer> mappedIntegerList = config.mapComaSeparatedStringToIntegerList("12,6,22");
        assertThat( mappedIntegerList ).isEqualTo( List.of(12, 6, 22) );
    }

    @DisplayName("Comma separated strings with spaces are correctly converted to list of Integer")
    @Test
    void testComaSeparatedToIntegerList_withSpaces() {
        List<Integer> mappedIntegerList = config.mapComaSeparatedStringToIntegerList("12, 6, 22");

        assertThat( config.isFileNotFound() ).isFalse();
        assertThat(mappedIntegerList).isEqualTo( List.of(12, 6, 22) );
    }

    @DisplayName("If a contree game config cannot load the config file, fileNotFound flag is true")
    @Test
    void testCreateConfigWithBadFileName() {
        ContreeGameConfig gameConfig = new ContreeGameConfig("non-existing-file");
        assertThat( gameConfig.isFileNotFound() ).isTrue();
    }

}
