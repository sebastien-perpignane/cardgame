package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.List;

public interface ContreeGamePlayers extends ContreePlayers {

    void joinGame(ContreePlayer joiningPlayer);

    void joinGame(ContreePlayer joiningPlayer, ContreeTeam wantedTeam);

    boolean isFull();

    boolean isJoinableByHumanPlayers();

    List<ContreePlayer> getGamePlayers();

    int getNbPlayers();

    ContreeDealPlayers buildDealPlayers();

    void receiveHandForPlayer(int playerIndex, List<ClassicalCard> hand);

}
