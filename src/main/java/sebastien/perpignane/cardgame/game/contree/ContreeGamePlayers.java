package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.List;

interface ContreeGamePlayers extends ContreePlayers {

    /**
     * @param joiningPlayer the player who joins the game
     * @return the index of the joining player in the game
     */
    JoinGameResult joinGame(ContreePlayer joiningPlayer);

    /**
     * @param joiningPlayer the player who joins the game
     * @param wantedTeam the team the player wants to join
     * @return the index of the joining player in the game
     */
    JoinGameResult joinGame(ContreePlayer joiningPlayer, ContreeTeam wantedTeam);

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

    ContreeGamePlayerSlots getPlayerSlots();

    int getNbPlayers();

    ContreeDealPlayers buildDealPlayers();


}
