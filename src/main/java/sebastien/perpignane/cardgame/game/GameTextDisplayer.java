package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;

import java.util.List;

public class GameTextDisplayer implements GameObserver, WarTrickObserver {

    public GameTextDisplayer() {

    }

    @Override
    public void onCardPlayed(PlayedCard pc) {
        System.out.printf("Player %s played %s%n", pc.player(), pc.card());
        if ((pc.player().hasNoMoreCard())) {
            System.out.printf("Player %s has no more card%n", pc.player());
        }
    }

    @Override
    public void onStateUpdated(GameState oldState, GameState newState) {
        System.out.printf("Game state changed from %s to %s%n", oldState, newState);
    }

    @Override
    public void onNextPlayer(Player p) {
        // System.out.printf("%s plays.%n", p);
    }

    @Override
    public void onWonTrick(Trick trick) {
        System.out.printf("Player %s won trick %s%n", trick.getWinner(), trick);
    }

    @Override
    public void onEndOfGame(Game game) {
        System.out.printf("Game %s is over. The winner is %s!!%n", game, game.getWinner());
    }

    @Override
    public void onWar(List<PlayedCard> cardsTriggeringWar) {

        System.out.println(" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  WAR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        for (var pc: cardsTriggeringWar) {
            System.out.printf("Player %s : %s ; ", pc.player(), pc.card());
        }
        System.out.println();

    }
}
