package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.player.contree.ContreeBotPlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeLocalConsoleHumanPlayer;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Scanner;

public class ContreeGameMain {

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
                game.joinGame(new ContreeBotPlayer());
                i++;
            }

            boolean onlyBots = Boolean.getBoolean("only-bots");

            ContreePlayer lastPlayer;
            if (onlyBots) {
                lastPlayer = new ContreeBotPlayer() {
                    @Override
                    protected void placeBid() {
                        placeBid(ContreeBidValue.EIGHTY, CardSuit.HEARTS);
                    }
                };

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

    private static ContreePlayer manageHumanPlayer() {

        Scanner scanner = new Scanner(System.in);

        String humanPlayerName = "";
        while (humanPlayerName.isBlank()) {
            System.out.println("Please enter your player name :");
            humanPlayerName = scanner.nextLine();
        }

        return new ContreeLocalConsoleHumanPlayer(humanPlayerName);

    }

}
