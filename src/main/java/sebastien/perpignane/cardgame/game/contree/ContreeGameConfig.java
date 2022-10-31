package sebastien.perpignane.cardgame.game.contree;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ContreeGameConfig {

    private final static String DEFAULT_CONFIG_FILE = "contree-game.properties";

    private final static String MAX_SCORE_PROP = "contree-game.max.score";

    private final static String DISTRIBUTION_CONFIG_PROP = "contree-game.distribution.config";

    private boolean fileNotFound = false;

    private final int maxScore;
    private final List<Integer> distributionConfiguration;

    public ContreeGameConfig()  {
        this(DEFAULT_CONFIG_FILE);
    }

    public ContreeGameConfig(String fileName) {

        int tmpMaxScore = 1500;
        List<Integer> tmpDistributionConfig = List.of(3,3,2);

        try {
            Properties properties = new Properties();
            String finalFileName = fileName == null ? DEFAULT_CONFIG_FILE : fileName;
            var in = getClass().getResourceAsStream("/" + finalFileName);
            properties.load(in);

            tmpMaxScore = Integer.parseInt(properties.getProperty(MAX_SCORE_PROP));

            tmpDistributionConfig = mapComaSeparatedStringToIntegerList(properties.getProperty(DISTRIBUTION_CONFIG_PROP));
        }
        catch(Exception e) {
            fileNotFound = true;
        }

        maxScore = tmpMaxScore;
        distributionConfiguration = tmpDistributionConfig;

    }

    List<Integer> mapComaSeparatedStringToIntegerList(String comaSeparated) {
        return Arrays.stream(
                        comaSeparated.split(",")
                ).map(String::trim).map(Integer::valueOf)
                .collect(Collectors.toList());
    }


    public int maxScore() {
        return maxScore;
    }

    public List<Integer> distributionConfiguration() {
        return distributionConfiguration;
    }

    public boolean isFileNotFound() {
        return fileNotFound;
    }

}
