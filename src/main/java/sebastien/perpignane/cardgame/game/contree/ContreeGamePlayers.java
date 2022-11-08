package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.List;

public interface ContreeGamePlayers extends ContreePlayers {

    void joinGame(ContreePlayer joiningPlayer);

    void joinGame(ContreePlayer joiningPlayer, ContreeTeam wantedTeam);

    void receiveHandForPlayer(int playerIndex, List<ClassicalCard> hand);

    /**
     *
     * @param player player leaving the game
     * @return the player who replaced the leaving player
     * @throws IllegalArgumentException if the player is not playing this game
     */
    ContreePlayer leaveGameAndReplaceWithBotPlayer(ContreePlayer player) throws IllegalArgumentException;

    boolean isFull();

    boolean isJoinableByHumanPlayers();

    List<ContreePlayer> getGamePlayers();

    int getNbPlayers();

    ContreeDealPlayers buildDealPlayers();


}
