package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Set;

public interface ContreeTrickPlayers extends ContreePlayers {

    void setCurrentTrick(ContreeTrick currentTrick);

    ContreePlayer getCurrentPlayer();

    void gotToNextPlayer();

    void notifyCurrentPlayerTurn(Set<ClassicalCard> allowedCards);

}