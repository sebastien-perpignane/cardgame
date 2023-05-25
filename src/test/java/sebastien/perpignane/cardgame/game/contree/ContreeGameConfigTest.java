package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContreeGameConfigTest {

    private ContreeGameConfig contreeGameConfig;

    @BeforeEach
    void setUp() {
        contreeGameConfig = new ContreeGameConfig() {
            @Override
            public int getMaxScore() {
                return ContreeGameConfig.super.getMaxScore();
            }
        };
    }

    @DisplayName("Default max score")
    @Test
    void testDefaultMaxScore() {
        assertThat(contreeGameConfig.getMaxScore())
                .isEqualTo(ContreeGameConfig.DEFAULT_MAX_SCORE);
    }

    @DisplayName("Default distribution config")
    @Test
    void testDefaultDistributionConfig()  {
        assertThat(contreeGameConfig.getDistributionConfiguration())
                .isEqualTo(ContreeGameConfig.DEFAULT_DISTRIBUTION_CONFIG);
    }

}