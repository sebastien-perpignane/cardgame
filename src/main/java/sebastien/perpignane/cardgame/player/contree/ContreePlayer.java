package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.game.contree.ContreeGame;
import sebastien.perpignane.cardgame.player.Player;

import java.util.Set;

public interface ContreePlayer extends Player<ContreeGame, ContreeTeam> {

    void onPlayerTurnToBid(Set<ContreeBidValue> allowedBidValues);

    void onPlayerTurn(Set<ClassicalCard> allowedCards);

    boolean sameTeam(ContreePlayer otherPlayer);

    void playCard(ClassicalCard card);

    void placeBid(ContreeBidValue bidValue, CardSuit cardSuit);

    void leaveGame();

    String getName();

}
