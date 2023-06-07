package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.game.contree.ContreeGame;
import sebastien.perpignane.cardgame.player.Player;

import java.util.Set;

public interface ContreePlayer extends Player<ContreeGame, ContreeTeam> {

    String getId();

    String getName();

    void onPlayerTurnToBid(Set<ContreeBidValue> allowedBidValues);

    void onPlayerTurn(Set<ClassicalCard> allowedCards);

    boolean sameTeam(ContreePlayer otherPlayer);

    void playCard(ClassicalCard card);

    void placeBid(ContreeBidValue bidValue, CardSuit cardSuit);

    void leaveGame();

    void setWaiting();

    void setBidding();

    void setPlaying();

    boolean isWaiting();

    boolean isBidding();

    boolean isPlaying();

    /**
     * Provide minimal state data about the player state useful for client app. Can be sent to other players of the game.
     * @return minimal player state
     */
    ContreePlayerState toState();

    /**
     * Provide complete state data about the player state useful for client app. Must be sent to the given player only.
     * @return full player state
     */
    FullContreePlayerState toFullState();

}
