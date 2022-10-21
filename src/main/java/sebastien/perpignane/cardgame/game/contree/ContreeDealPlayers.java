package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.List;
import java.util.Optional;

public interface ContreeDealPlayers extends ContreePlayers {

    void setCurrentDeal(ContreeDeal deal);

    int getNumberOfPlayers();

    void receiveHandForPlayer(int playerIndex, List<ClassicalCard> hand);

    Optional<ContreeTeam> getCurrentDealAttackTeam();

    Optional<ContreeTeam> getCurrentDealDefenseTeam();

    List<ContreePlayer> getCurrentDealPlayers();

    ContreeBidPlayers buildBidPlayers();

    ContreeTrickPlayers buildTrickPlayers();

}
