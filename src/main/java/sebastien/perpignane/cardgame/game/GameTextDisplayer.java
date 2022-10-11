package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.*;
import sebastien.perpignane.cardgame.game.war.WarGame;
import sebastien.perpignane.cardgame.game.war.WarTrickObserver;
import sebastien.perpignane.cardgame.player.Player;

import java.util.List;

public class GameTextDisplayer implements GameObserver, WarTrickObserver, ContreeDealObserver, ContreeTrickObserver {

    private final static GameTextDisplayer INSTANCE;

    static {
        INSTANCE = new GameTextDisplayer();
    }

    public static GameTextDisplayer getInstance() {
        return INSTANCE;
    }

    private GameTextDisplayer() {

    }

    @Override
    public void onCardPlayed(Player player, ClassicalCard card) {
        System.out.printf("%s - Player %s played %s%n", Thread.currentThread().getName(), player, card);
        if ((player.hasNoMoreCard())) {
            System.out.printf("Player %s has no more card%n", player);
        }
    }

    @Override
    public void onStateUpdated(GameState oldState, GameState newState) {
        System.out.printf("Game state changed from %s to %s%n", oldState, newState);
    }

    @Override
    public void onNextPlayer(Player p) {
        System.out.printf("%s plays.%n", p);
    }

    @Override
    public void onWonTrick(Trick trick) {
        System.out.println("Player " + trick.getWinner() + " won trick " + trick + System.lineSeparator());
    }

    @Override
    public void onEndOfGame(WarGame warGame) {
        System.out.printf("Game %s is over. The winner is %s!!%n", warGame, warGame.getWinner());
    }

    @Override
    public void onEndOfGame(ContreeGame contreeGame) {
        System.out.printf("Game %s is over. Winner is %s%n", contreeGame, contreeGame.getWinner());
    }

    @Override
    public void onWar(List<PlayedCard> cardsTriggeringWar) {

        System.out.println(" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  WAR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        cardsTriggeringWar.forEach(pc -> System.out.printf("Player %s : %s ; ", pc.player(), pc.card()));
        System.out.println();
    }

    @Override
    public void onDealStarted(String dealId) {
        System.out.printf("Deal %s is started%n", dealId);
    }

    @Override
    public void onDealOver(String dealId) {
        System.out.printf("Deal %s is over%n", dealId);
    }

    @Override
    public void onPlacedBid(String dealId, Player player, ContreeBidValue bidValue, CardSuit suit) {
        System.out.printf("Deal %s : Bid (%s, %s) placed by %s%n", dealId, bidValue, suit, player);
    }

    @Override
    public void onBidStepStarted(String dealId) {
        System.out.printf("Bid step is started on deal %s%n", dealId);
    }

    @Override
    public void onBidStepEnded(String dealId) {
        System.out.printf("Bid step is over on deal %s%n", dealId);
    }

    @Override
    public void onPlayStepStarted(String dealId, CardSuit trumpSuit) {
        System.out.printf("Play step is started on deal %s. Trump is %s.%n", dealId, trumpSuit);
    }

    @Override
    public void onPlayStepEnded(String dealId) {
        System.out.printf("Play step is over on deal %s%n", dealId);
    }

    @Override
    public void onTrumpedTrick(String trickId) {

    }
}
