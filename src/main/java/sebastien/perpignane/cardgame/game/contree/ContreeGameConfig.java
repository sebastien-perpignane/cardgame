package sebastien.perpignane.cardgame.game.contree;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@ApplicationScoped
public class ContreeGameConfig {

    private final int maxScore;
    private final List<Integer> distributionConfiguration;

    public ContreeGameConfig()  {

        int tmpMaxScore = 1500;
        List<Integer> tmpDistribConfig = List.of(3,3,2);

        try {
            Properties properties = new Properties();
            var in = getClass().getResourceAsStream("/contree-game.properties");
            properties.load(in);

            tmpMaxScore = Integer.parseInt(properties.getProperty("contree-game.max.score", "1500"));

            tmpDistribConfig =
                    Arrays.stream(
                        properties.getProperty("contree-game.distribution.config").split(",")
                    ).map(Integer::valueOf)
                    .collect(Collectors.toList());
        }
        catch(Exception e) {
            System.err.println("Could not read contree game configuration");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        maxScore = tmpMaxScore;
        distributionConfiguration = tmpDistribConfig;

    }


    @Produces // @Named("maxScore")
    public int maxScore() {
        return maxScore;
    }

    @Produces
    public List<Integer> getDistributionConfiguration() {
        return distributionConfiguration;
    }

}
