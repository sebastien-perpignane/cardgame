package sebastien.perpignane.cardgame.game.contree;

import java.util.List;

public interface ContreeGameConfig {

    int DEFAULT_MAX_SCORE = 1_500;
    List<Integer> DEFAULT_DISTRIBUTION_CONFIG = List.of(3,3,2);


    default int getMaxScore() {
        return DEFAULT_MAX_SCORE;
    }

    default List<Integer> getDistributionConfiguration() {
        return DEFAULT_DISTRIBUTION_CONFIG;
    }
}
