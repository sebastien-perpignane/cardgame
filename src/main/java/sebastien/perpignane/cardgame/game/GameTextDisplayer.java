package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.game.contree.ContreeDealObserver;
import sebastien.perpignane.cardgame.game.contree.ContreeDealScore;
import sebastien.perpignane.cardgame.game.contree.ContreeGame;
import sebastien.perpignane.cardgame.game.contree.ContreeTrickObserver;
import sebastien.perpignane.cardgame.game.war.WarGame;
import sebastien.perpignane.cardgame.game.war.WarPlayedCard;
import sebastien.perpignane.cardgame.game.war.WarTrickObserver;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.List;

import static java.lang.System.out;

// FIXME use loggers
public class GameTextDisplayer implements GameObserver, WarTrickObserver, ContreeDealObserver, ContreeTrickObserver {

    private static final GameTextDisplayer INSTANCE;

    static final String CAPOT_ASCII = """
                    ###      ###      ###              # ###                                                ###      ###      ###
                     ###      ###      ###           /  /###  /                                              ###      ###      ###
                      ##       ##       ##          /  /  ###/                                    #           ##       ##       ##
                      ##       ##       ##         /  ##   ##                                    ##           ##       ##       ##
                      ##       ##       ##        /  ###                                         ##           ##       ##       ##
                      ##       ##       ##       ##   ##          /###       /###     /###     ########       ##       ##       ##
                      ##       ##       ##       ##   ##         / ###  /   / ###  / / ###  / ########        ##       ##       ##
                      ##       ##       ##       ##   ##        /   ###/   /   ###/ /   ###/     ##           ##       ##       ##
                      ##       ##       ##       ##   ##       ##    ##   ##    ## ##    ##      ##           ##       ##       ##
                      ### /    ### /    ### /    ##   ##       ##    ##   ##    ## ##    ##      ##           ### /    ### /    ### /
                       ##/      ##/      ##/      ##  ##       ##    ##   ##    ## ##    ##      ##            ##/      ##/      ##/
                                                   ## #      / ##    ##   ##    ## ##    ##      ##
                       #        #        #          ###     /  ##    /#   ##    ## ##    ##      ##            #        #        #
                      ###      ###      ###          ######/    ####/ ##  #######   ######       ##           ###      ###      ###
                       #        #        #             ###       ###   ## ######     ####         ##           #        #        #
                                                                          ##
                                                                          ##
                                                                          ##
                                                                           ##

                   """.stripIndent();

    static final String TEAM_1_WINNER_ASCII = """
     ##### /    ##   ###                                                                                 /###           /                                             # ###
  ######  /  #####    ###     #                                                    #                    /  ############/                                            /  /###
 /#   /  /     #####   ###   ###                                                  ###                  /     #########                                             /  /  ###
/    /  ##     # ##      ##   #                                                    #                   #     /  #                                                 /  ##   ###
    /  ###     #         ##                                                                             ##  /  ##                                                /  ###    ###
   ##   ##     #         ## ###   ###  /###   ###  /###     /##  ###  /###       ###        /###           /  ###          /##       /###   ### /### /###       ##   ##     ## ###  /###     /##
   ##   ##     #         ##  ###   ###/ #### / ###/ #### / / ###  ###/ #### /     ###      / #### /       ##   ##         / ###     / ###  / ##/ ###/ /##  /    ##   ##     ##  ###/ #### / / ###
   ##   ##     #         ##   ##    ##   ###/   ##   ###/ /   ###  ##   ###/       ##     ##  ###/        ##   ##        /   ###   /   ###/   ##  ###/ ###/     ##   ##     ##   ##   ###/ /   ###
   ##   ##     #         ##   ##    ##    ##    ##    ## ##    ### ##              ##    ####             ##   ##       ##    ### ##    ##    ##   ##   ##      ##   ##     ##   ##    ## ##    ###
   ##   ##     #         ##   ##    ##    ##    ##    ## ########  ##              ##      ###            ##   ##       ########  ##    ##    ##   ##   ##      ##   ##     ##   ##    ## ########
    ##  ##     #         ##   ##    ##    ##    ##    ## #######   ##              ##        ###           ##  ##       #######   ##    ##    ##   ##   ##       ##  ##     ##   ##    ## #######
     ## #      #         /    ##    ##    ##    ##    ## ##        ##              ##          ###          ## #      / ##        ##    ##    ##   ##   ##        ## #      /    ##    ## ##
      ###      /##      /     ##    ##    ##    ##    ## ####    / ##              ##     /###  ##           ###     /  ####    / ##    /#    ##   ##   ##         ###     /     ##    ## ####    /
       #######/ #######/      ### / ###   ###   ###   ### ######/  ###             ### / / #### /             ######/    ######/   ####/ ##   ###  ###  ###         ######/      ###   ### ######/
         ####     ####         ##/   ###   ###   ###   ### #####    ###             ##/     ###/                ###       #####     ###   ##   ###  ###  ###          ###         ###   ### #####

""";

    static final String TEAM2_WINNER_ASCII = """
     ##### /    ##   ###                                                                                 /###           /                                         /###           /
  ######  /  #####    ###     #                                                    #                    /  ############/                                         /  ############/
 /#   /  /     #####   ###   ###                                                  ###                  /     #########                                          /     #########
/    /  ##     # ##      ##   #                                                    #                   #     /  #                                               #     /  #     ##
    /  ###     #         ##                                                                             ##  /  ##                                                ##  /  ##     ##
   ##   ##     #         ## ###   ###  /###   ###  /###     /##  ###  /###       ###        /###           /  ###          /##       /###   ### /### /###           /  ###      ##    ###    ####      /###
   ##   ##     #         ##  ###   ###/ #### / ###/ #### / / ###  ###/ #### /     ###      / #### /       ##   ##         / ###     / ###  / ##/ ###/ /##  /       ##   ##       ##    ###     ###  / / ###  /
   ##   ##     #         ##   ##    ##   ###/   ##   ###/ /   ###  ##   ###/       ##     ##  ###/        ##   ##        /   ###   /   ###/   ##  ###/ ###/        ##   ##       ##     ###     ###/ /   ###/
   ##   ##     #         ##   ##    ##    ##    ##    ## ##    ### ##              ##    ####             ##   ##       ##    ### ##    ##    ##   ##   ##         ##   ##       ##      ##      ## ##    ##
   ##   ##     #         ##   ##    ##    ##    ##    ## ########  ##              ##      ###            ##   ##       ########  ##    ##    ##   ##   ##         ##   ##       ##      ##      ## ##    ##
    ##  ##     #         ##   ##    ##    ##    ##    ## #######   ##              ##        ###           ##  ##       #######   ##    ##    ##   ##   ##          ##  ##       ##      ##      ## ##    ##
     ## #      #         /    ##    ##    ##    ##    ## ##        ##              ##          ###          ## #      / ##        ##    ##    ##   ##   ##           ## #      / ##      ##      ## ##    ##
      ###      /##      /     ##    ##    ##    ##    ## ####    / ##              ##     /###  ##           ###     /  ####    / ##    /#    ##   ##   ##            ###     /  ##      /#      /  ##    ##
       #######/ #######/      ### / ###   ###   ###   ### ######/  ###             ### / / #### /             ######/    ######/   ####/ ##   ###  ###  ###            ######/    ######/ ######/    ######
         ####     ####         ##/   ###   ###   ###   ### #####    ###             ##/     ###/                ###       #####     ###   ##   ###  ###  ###             ###       #####   #####      ####
""";

    static final String SKULL_ASCII = """
                              _________-----_____
                   _____------           __      ----_
            ___----             ___------              \\
               ----________        ----                 \\
                           -----__    |             _____)
                                __-                /     \\
                    _______-----    ___--          \\    /)\\
              ------_______      ---____            \\__/  /
                           -----__    \\ --    _          /\\
                                  --__--__     \\_____/   \\_/\\
                                          ----|   /          |
                                              |  |___________|
                                              |  | ((_(_)| )_)
                                              |  \\_((_(_)|/(_)
                                              \\             (
                                               \\_____________)

            """.stripIndent();

    static {
        INSTANCE = new GameTextDisplayer();
    }

    public static GameTextDisplayer getInstance() {
        return INSTANCE;
    }

    private GameTextDisplayer() {

    }

    @Override
    public void onJoinedGame(ContreeGame contreeGame, int playerIndex, ContreePlayer player) {
        out.printf("Player #%d (%s) joined the game %s%n", playerIndex + 1, player.getName(), contreeGame.getGameId());
    }

    @Override
    public void onCardPlayed(Player<?, ?> player, ClassicalCard card) {
        out.printf("%s - %s played %s%n", Thread.currentThread().getName(), player.toString(), card);
        if ((player.hasNoMoreCard())) {
            out.printf("Player %s has no more card%n", player);
        }
    }

    @Override
    public void onStateUpdated(GameStatus oldState, GameStatus newState) {
        out.printf("Game state changed from %s to %s%n", oldState, newState);
    }

    @Override
    public void onNextPlayer(Player<?, ?> p) {
        out.printf("%s plays.%n", p);
    }

    @Override
    public void onWonTrick(Trick<?,?,?> trick) {
        out.println("Player " + trick.getWinner().orElseThrow() + " won trick " + trick + System.lineSeparator());
    }

    

    @Override
    public void onEndOfGame(WarGame warGame) {
        out.printf("Game %s is over. The winner is %s!!%n", warGame, warGame.getWinner());
    }

    @Override
    public void onEndOfGame(ContreeGame contreeGame) {

        if (contreeGame.getWinner().isEmpty()) {
            out.println(SKULL_ASCII);
            out.println("Unexpected end of game");
            return;
        }

        out.printf("Game %s is over!!%n", contreeGame);

        String ascii =
                contreeGame.getWinner()
                    .map( wt -> wt == ContreeTeam.TEAM1 ? TEAM_1_WINNER_ASCII : TEAM2_WINNER_ASCII )
                    .orElseThrow();

        out.println(ascii);


    }

    @Override
    public void onWar(List<WarPlayedCard> cardsTriggeringWar) {

        out.println(" !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!  WAR !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        cardsTriggeringWar.forEach(pc -> out.printf("Player %s : %s ; GameTextDisplayer", pc.player(), pc.card()));
        out.println();
    }

    @Override
    public void onDealStarted(int dealNumber, String dealId) {
        out.printf("""
************************************************************************************************************************
Deal #%d (%s) is started%n
************************************************************************************************************************
""",
                dealNumber, dealId);
    }

    @Override
    public void onEndOfDeal(String dealId, Team winnerTeam, ContreeDealScore dealScore, boolean capot) {
        String winnerText = winnerTeam == null ? "No winner." : String.format("Winner is %s.", winnerTeam);

        if (capot) {
            out.println(CAPOT_ASCII);
        }

        out.printf("""
************************************************************************************************************************
Deal %s is over. %s%n
Raw score :
    Team 1: %d
    Team 2: %d
%s. Score before rounding :
    Team 1: %d
    Team 2: %d
Final score :
    Team 1: %s
    Team 2: %s
************************************************************************************************************************
""",
            dealId,
            winnerText,
            dealScore.getRawTeamScore(ContreeTeam.TEAM1),
            dealScore.getRawTeamScore(ContreeTeam.TEAM2),
            dealScore.isContractReached() ? "Contract is reached" : "Contract is not reached",
            dealScore.getTeamNotRoundedScore(ContreeTeam.TEAM1),
            dealScore.getTeamNotRoundedScore(ContreeTeam.TEAM2),
            dealScore.getTeamScore(ContreeTeam.TEAM1),
            dealScore.getTeamScore(ContreeTeam.TEAM2)
        );
    }

    @Override
    public void onPlacedBid(String dealId, Player<?, ?> player, ContreeBidValue bidValue, CardSuit suit) {
        out.printf("Deal %s : Bid (%s, %s) placed by %s%n", dealId, bidValue, suit, player);
    }

    @Override
    public void onBidStepStarted(String dealId) {
        out.printf("""

***********************************************************************
* BID STEP started on deal %s
***********************************************************************

""", dealId);
    }

    @Override
    public void onBidStepEnded(String dealId) {
        out.printf("Bid step is over on deal %s%n", dealId);
    }

    @Override
    public void onPlayStepStarted(String dealId, CardSuit trumpSuit) {
        out.printf("""

***********************************************************************
* PLAY STEP started on deal %s. TRUMP is %s
***********************************************************************

""", dealId, trumpSuit);
    }

    @Override
    public void onPlayStepEnded(String dealId) {
        out.printf("Play step is over on deal %s%n", dealId);
    }

    @Override
    public void onTrumpedTrick(String trickId) {
        out.printf("Trick #%s is trumped!%n", trickId);
    }

    @Override
    public void onNewTrick(String trickId, CardSuit trumpSuit) {
        out.printf("""

***********************************************************************
* NEW TRICK #%s - Trump is %s
***********************************************************************

""",
        trickId, trumpSuit);
    }

    @Override
    public void onEndOfTrick(String trickId, ContreePlayer winner) {
        String winnerText = winner == null ? "" : String.format(" Winner is %s (%s)", winner.getName(), winner.getTeam().orElseThrow());
        out.printf("Trick %s is over.%s%n", trickId, winnerText);
    }

}
