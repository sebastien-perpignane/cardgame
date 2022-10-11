package sebastien.perpignane.cardgame.game.contree;

import org.apache.commons.collections4.iterators.LoopingListIterator;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.*;

class ContreeGamePlayers {

    final static int NB_MAX_PLAYERS = 4;

    private final ContreeGame contreeGame;

    private final ContreePlayer[] players = new ContreePlayer[4];

    private volatile int nbPlayers = 0;

    private ContreePlayer currentDealer;

    private LoopingListIterator<ContreePlayer> playerIterator;

    public ContreeGamePlayers(ContreeGame contreeGame) {
        this.contreeGame = contreeGame;
    }

    public synchronized void joinGame(ContreePlayer joiningPlayer, ContreeTeam wantedTeam) {

        Objects.requireNonNull(joiningPlayer, "joiningPlayer cannot be null");

        boolean joined = false;
        for (int i = 0 ; i < NB_MAX_PLAYERS ; i++) {

            boolean isExpectedTeam = wantedTeam == null || teamByPlayerIndex(i) == wantedTeam;

            if ( isExpectedTeam && (players[i] == null || players[i].isBot())) {
                var presentPlayer = players[i];
                boolean noPresentPlayer = presentPlayer == null;

                if ( noPresentPlayer || ( !joiningPlayer.isBot() && presentPlayer.isBot() ) ) {
                    players[i] = joiningPlayer;
                    assignTeamToPlayer(i);
                    joiningPlayer.setGame(contreeGame);
                    joined = true;
                    nbPlayers++;
                    break;
                }

            }
        }

        if (!joined) {
            throw new IllegalStateException(String.format("No slot available in the wanted team: %s", wantedTeam));
        }


        if (isFull()) {
            playerIterator = new LoopingListIterator<>(getPlayers());
            this.currentDealer = playerIterator.previous();
            // surprising behavior of iterators -> when you call previous then next, both calls give same element of the iterator
            playerIterator.next();
        }

    }

    public void joinGame(ContreePlayer joiningPlayer) {
        joinGame(joiningPlayer, null);
    }

    public List<ContreePlayer> nextPlayerList() {
        List<ContreePlayer> newDealPlayers = new ArrayList<>();
        currentDealer = playerIterator.next();
        int i = 0;
        while (i < NB_MAX_PLAYERS) {
            newDealPlayers.add(playerIterator.next());
            i++;
        }
        return newDealPlayers;
    }

    public boolean isFull() {
        return nbPlayers == NB_MAX_PLAYERS;
    }

    private void assignTeamToPlayer(int playerIndex) {
        ContreeTeam team = teamByPlayerIndex(playerIndex);
        players[playerIndex].setTeam(team);
    }

    private ContreeTeam teamByPlayerIndex(int playerIndex) {
        return playerIndex % 2 == 0 ? ContreeTeam.TEAM1 : ContreeTeam.TEAM2;
    }

    public List<ContreePlayer> getPlayers() {
        return Arrays.stream(players).toList();
    }

    public ContreePlayer getCurrentDealer() {
        return currentDealer;
    }

}
