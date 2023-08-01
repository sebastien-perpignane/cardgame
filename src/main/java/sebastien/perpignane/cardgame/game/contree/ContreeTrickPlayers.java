package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.util.PlayerSlot;

import java.util.Set;

interface ContreeTrickPlayers extends ContreePlayers {

    void setCurrentTrick(ContreeTrick currentTrick);

    PlayerSlot<ContreePlayer> getCurrentPlayerSlot();

    void gotToNextPlayer();

    void notifyCurrentPlayerTurn(Set<ClassicalCard> allowedCards);

}
