package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.player.contree.ContreeBotPlayer;

public class ContreeGameMain {

    public static void main(String[] args) {

        int i = 0;
        while (i < 1000) {
            startAGame();
            i++;
        }

    }

    private static void startAGame() {
        try {
            var game = ContreeGameFactory.createGame(1000);

            game.registerAsGameObserver(GameTextDisplayer.getInstance());

            int i = 0;
            while (i < 3) {
                game.joinGame(new ContreeBotPlayer());
                i++;
            }

            game.joinGame(new ContreeBotPlayer() {
                @Override
                protected void placeBid() {
                    placeBid(ContreeBidValue.EIGHTY, CardSuit.HEARTS);
                }
            });
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }

    }

}
