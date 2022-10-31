package sebastien.perpignane.cardgame.game.contree;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;
import sebastien.perpignane.cardgame.player.contree.ContreeBotPlayer;

public class ContreeGameMain {

    public static void main(String[] args) {

        Weld weld = new Weld();

        try(final WeldContainer container = weld.initialize()) {

            int i = 0;
            while (i < 1000) {
                startAGame(container);
                i++;
            }

        }

    }

    private static void startAGame( WeldContainer container) {
        try {
            var game = container.select(ContreeGame.class).get();

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
