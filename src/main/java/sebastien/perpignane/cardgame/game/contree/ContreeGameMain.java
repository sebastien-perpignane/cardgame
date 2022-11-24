package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.player.contree.event.handler.ContreePlayerEventHandlerImpl;
import sebastien.perpignane.cardgame.player.contree.event.handler.handlers.ContreeBotPlayerEventHandler;
import sebastien.perpignane.cardgame.player.contree.event.handler.handlers.ContreeLocalPlayerEventHandler;
import sebastien.perpignane.cardgame.player.contree.local.thread.ThreadContreeBotPlayer;

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

                game.joinGame(new ContreePlayerEventHandlerImpl(new ContreeBotPlayerEventHandler()));
                i++;
            }

            boolean onlyBots = Boolean.getBoolean("only-bots");

            sebastien.perpignane.cardgame.player.contree.ContreePlayer lastPlayer;
            if (onlyBots) {
                lastPlayer = new ThreadContreeBotPlayer() {
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

    private static sebastien.perpignane.cardgame.player.contree.ContreePlayer manageHumanPlayer() {

        Scanner scanner = new Scanner(System.in);

        String humanPlayerName = "";
        while (humanPlayerName.isBlank()) {
            System.out.println("Please enter your player name :");
            humanPlayerName = scanner.nextLine();
        }

        return new ContreePlayerEventHandlerImpl(humanPlayerName, new ContreeLocalPlayerEventHandler(humanPlayerName));

        //return new ContreeLocalConsoleHumanPlayer(humanPlayerName);

    }

}
