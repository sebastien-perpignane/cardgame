package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreePlayerImpl;
import sebastien.perpignane.cardgame.player.contree.handlers.BiddingBotEventHandler;
import sebastien.perpignane.cardgame.player.contree.handlers.ContreeBotPlayerEventHandler;
import sebastien.perpignane.cardgame.player.contree.handlers.ContreeLocalPlayerEventHandler;

import java.util.Scanner;

public class ContreeGameMain {

    private final static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        startAGame();
    }

    private static void startAGame() {
        try {

            int maxScore = Integer.getInteger("max-score", 1000);

            System.out.printf("Game will start. Max score is %d%n", maxScore);

            var game = ContreeGameFactory.createGame(maxScore);

            game.registerAsGameObserver(GameTextDisplayer.getInstance());

            int i = 0;
            while (i < 3) {

                game.joinGame(createBotPlayer());
                i++;
            }

            boolean onlyBots = Boolean.getBoolean("only-bots");

            sebastien.perpignane.cardgame.player.contree.ContreePlayer lastPlayer;
            if (onlyBots) {
                lastPlayer = new ContreePlayerImpl(new BiddingBotEventHandler());

            }
            else {
                lastPlayer = manageHumanPlayer();
            }

            game.joinGame(lastPlayer);
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }

    }

    private static ContreePlayer createBotPlayer() {
        return new ContreePlayerImpl(new ContreeBotPlayerEventHandler());
    }

    private static ContreePlayer manageHumanPlayer() {

        String humanPlayerName = "";
        while (humanPlayerName.isBlank()) {
            System.out.println("Please enter your player name :");
            humanPlayerName = scanner.nextLine();
        }

        return new ContreePlayerImpl(humanPlayerName, new ContreeLocalPlayerEventHandler(scanner, humanPlayerName));

    }

}
