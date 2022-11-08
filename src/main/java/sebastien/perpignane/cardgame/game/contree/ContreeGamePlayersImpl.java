package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

class ContreeGamePlayersImpl implements ContreeGamePlayers {

    final static int NB_MAX_PLAYERS = 4;

    private final List<ContreePlayer> players = new CopyOnWriteArrayList<>();

    private volatile int nbPlayers = 0;

    public ContreeGamePlayersImpl() {
        for (int i = 0 ; i < NB_PLAYERS ; i++) {
            players.add(null);
        }
    }

    public synchronized void joinGame(ContreePlayer joiningPlayer, ContreeTeam wantedTeam) {

        Objects.requireNonNull(joiningPlayer, "joiningPlayer cannot be null");

        if (players.contains(joiningPlayer)) {
            throw new IllegalArgumentException( String.format("Player %s already joined the game", joiningPlayer) );
        }

        boolean joined = false;
        for (int i = 0 ; i < NB_PLAYERS ; i++) {

            boolean isExpectedTeam = wantedTeam == null || teamByPlayerIndex(i) == wantedTeam;

            if ( isExpectedTeam && (players.get(i) == null || players.get(i).isBot())) {
                var presentPlayer = players.get(i);
                boolean noPresentPlayer = presentPlayer == null;

                if ( noPresentPlayer || ( !joiningPlayer.isBot() && presentPlayer.isBot() ) ) {
                    players.set(i, joiningPlayer);
                    assignTeamToPlayer(i);
                    joined = true;
                    if (noPresentPlayer) {
                        nbPlayers++;
                    }

                    break;
                }

            }
        }

        if (!joined) {
            String exceptionMessage;
            if (wantedTeam == null) {
                exceptionMessage = "No slot available in the game";
            }
            else {
                exceptionMessage = String.format("No slot available in the wanted team: %s", wantedTeam);

            }
            throw new IllegalStateException(exceptionMessage);

        }

    }

    public boolean wantedTeamIsFull(ContreeTeam wantedTeam) {
        return players.stream().filter(cp -> cp != null && cp.getTeam().isPresent() &&  cp.getTeam().get() == wantedTeam).toList().size() == 2;
    }

    @Override
    public synchronized ContreePlayer leaveGameAndReplaceWithBotPlayer(ContreePlayer player) {
        if (player.isBot()) {
            throw new IllegalArgumentException("WTF ? A bot wants to leave the game ?");
        }
        if (!players.contains(player)) {
            throw new IllegalArgumentException(String.format("The player %s does not play in this game", player));
        }
        int playerIndex = players.indexOf(player);
        ContreePlayer newBotPlayer = new ContreeBotPlayer();
        players.set(playerIndex, newBotPlayer);
        assignTeamToPlayer(playerIndex);

        return newBotPlayer;

    }

    public synchronized void joinGame(ContreePlayer joiningPlayer) {
        joinGame(joiningPlayer, null);
    }

    @Override
    public ContreeDealPlayers buildDealPlayers() {
        if (!isFull()) {
            throw new IllegalStateException("%d players must join the game before ContreeDealPlayers can be built");
        }
        return new ContreeDealPlayersImpl(this);
    }

    @Override
    public int getNbPlayers() {
        return nbPlayers;
    }

    public boolean isFull() {
        return nbPlayers == NB_MAX_PLAYERS;
    }

    public boolean isJoinableByHumanPlayers() {
        return !isFull() || players.stream().anyMatch(ContreePlayer::isBot);
    }

    private void assignTeamToPlayer(int playerIndex) {
        ContreeTeam team = teamByPlayerIndex(playerIndex);
        players.get(playerIndex).setTeam(team);
    }

    private ContreeTeam teamByPlayerIndex(int playerIndex) {
        return playerIndex % 2 == 0 ? ContreeTeam.TEAM1 : ContreeTeam.TEAM2;
    }

    public List<ContreePlayer> getGamePlayers() {
        return players;
    }

    @Override
    public void receiveHandForPlayer(int playerIndex, List<ClassicalCard> hand) {
        players.get(playerIndex).receiveHand(hand);
    }
}
