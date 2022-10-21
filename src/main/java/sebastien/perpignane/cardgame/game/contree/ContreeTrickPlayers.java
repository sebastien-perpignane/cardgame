package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Collection;

public interface ContreeTrickPlayers extends ContreePlayers {

    void setCurrentTrick(ContreeTrick currentTrick);

    ContreePlayer getCurrentPlayer();

    void gotToNextPlayer();

    void notifyCurrentPlayerTurn(Collection<ClassicalCard> allowedCards);

}