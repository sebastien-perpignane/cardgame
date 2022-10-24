package sebastien.perpignane.cardgame.game.contree;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.player.contree.ContreeBotPlayer;

import java.util.concurrent.Executors;

public class ContreeGameMain {

    public static void main(String[] args) {

        var pool = Executors.newFixedThreadPool(1000);

        Weld weld = new Weld();

        try(final WeldContainer container = weld.initialize()) {

            int i = 0;
            while (i < 1000) {
                pool.submit(() -> startAGame(container));
                i++;
            }

        }

        pool.shutdown();

    }

    private static ContreeGame startAGame(WeldContainer container) {
        var game = container.select(ContreeGame.class).get();

        game.joinGame(new ContreeBotPlayer() {
            @Override
            protected void placeBid() {
                placeBid(ContreeBidValue.EIGHTY, CardSuit.HEARTS);
            }
        });

        int i = 0;
        while (i < 3) {
            game.joinGame(new ContreeBotPlayer());
            i++;
        }

        game.registerAsGameObserver(GameTextDisplayer.getInstance());

        game.startGame();

        return game;

    }

}
