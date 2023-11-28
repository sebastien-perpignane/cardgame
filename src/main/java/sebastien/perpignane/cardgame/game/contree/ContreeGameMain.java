package sebastien.perpignane.cardgame.game.contree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreePlayerImpl;
import sebastien.perpignane.cardgame.player.contree.handlers.BiddingBotEventHandler;
import sebastien.perpignane.cardgame.player.contree.handlers.ContreeBotPlayerEventHandler;
import sebastien.perpignane.cardgame.player.contree.handlers.ContreeLocalPlayerEventHandler;

import java.util.List;
import java.util.Scanner;

import static java.lang.System.*;

@Command(name = "contree-game", description = "Run a contree game", mixinStandardHelpOptions = true)
public class ContreeGameMain {

    private static final Scanner scanner = new Scanner(in);

    private  static final Logger log = LoggerFactory.getLogger(ContreeGameMain.class);

    public static void main(String[] args) {

        CliContreeGameConfig cliContreeGameConfig = new CliContreeGameConfig();

        log.debug("Parsing CLI options");
        ParseResult parseResult = new CommandLine(cliContreeGameConfig).parseArgs(args);

        if (!CommandLine.printHelpIfRequested(parseResult)) {
            try {
                startGame(cliContreeGameConfig);
            }
            catch(Exception e) {
                log.error("Game session failed", e);
                exit(1);
            }
        }

    }

    private static void startGame(CliContreeGameConfig cliContreeGameConfig) {

        int maxScore = cliContreeGameConfig.getMaxScore();
        boolean onlyBots = cliContreeGameConfig.isOnlyBots();

        ContreeGame game = null;

        try {

            out.printf("Game will start. Max score is %d%n", maxScore);

            game = ContreeGameBuilder.createGame(cliContreeGameConfig);

            game.registerAsGameObserver(GameTextDisplayer.getInstance());

            int i = 0;
            while (i < 3) {
                game.joinGame(createBotPlayer(i));
                i++;
            }

            sebastien.perpignane.cardgame.player.contree.ContreePlayer lastPlayer;
            if (onlyBots) {
                lastPlayer = new ContreePlayerImpl("*Player 4*", new BiddingBotEventHandler());
            }
            else {
                lastPlayer = manageHumanPlayer(cliContreeGameConfig.getPlayerName());
            }

            game.joinGame(lastPlayer);
        }
        catch(Exception e) {
            if (game != null) {
                game.forceEndOfGame();
            }
            throw e;
        }
    }

    private static ContreePlayer createBotPlayer(int playerIdx) {
        String playerName = String.format("Player %d", playerIdx + 1);
        return new ContreePlayerImpl(playerName, new ContreeBotPlayerEventHandler());
    }

    private static ContreePlayer manageHumanPlayer(String humanPlayerName) {

        while (humanPlayerName.isBlank()) {
            out.println("Please enter your player name :");
            humanPlayerName = scanner.nextLine();
        }

        return new ContreePlayerImpl(humanPlayerName, new ContreeLocalPlayerEventHandler(scanner, humanPlayerName));

    }
}

@Command(name = "contree-game", description = "Run a contree game", mixinStandardHelpOptions = true)
class CliContreeGameConfig implements ContreeGameConfig {

    @Option(names = {"--max-score"}, description = "When this score is reached, the game is over")
    private int maxScore = DEFAULT_MAX_SCORE;

    @Option(names = {"--distribution-configuration"}, description = "How cards are distributed to players", arity = "3")
    private List<Integer> distributionConfiguration = DEFAULT_DISTRIBUTION_CONFIG;

    @Option(names = {"--only-bots"}, defaultValue = "false", description = "4 bots play a game")
    private boolean onlyBots = false;

    @CommandLine.Parameters(arity = "0..1")
    private String playerName = "";

    @Override
    public int getMaxScore() {
        return maxScore;
    }

    @Override
    public List<Integer> getDistributionConfiguration() {
        return distributionConfiguration;
    }

    public boolean isOnlyBots() {
        return onlyBots;
    }

    public String getPlayerName() {
        return playerName;
    }
}
