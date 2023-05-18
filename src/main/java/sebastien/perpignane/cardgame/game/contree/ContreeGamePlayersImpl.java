package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreePlayerImpl;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;
import sebastien.perpignane.cardgame.player.contree.handlers.ContreeBotPlayerEventHandler;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

class ContreeGamePlayersImpl implements ContreeGamePlayers {

    private final ContreeGamePlayerSlots playerSlots = new ContreeGamePlayerSlots();

    private volatile int nbPlayers = 0;

    public synchronized JoinGameResult joinGame(ContreePlayer joiningPlayer, ContreeTeam wantedTeam) {

        Objects.requireNonNull(joiningPlayer, "joiningPlayer cannot be null");

        if (playerSlots.contains(joiningPlayer)) {
            throw new IllegalArgumentException( String.format("Player %s already joined the game", joiningPlayer) );
        }

        Optional<ContreePlayer> replacedPlayer = Optional.empty();

        boolean wantedTeamIsFull = wantedTeam == null ? isFull() : wantedTeamIsFull(wantedTeam);

        boolean joined = false;
        int playerIndex = -1;
        for (int i = 0 ; i < NB_PLAYERS ; i++) {

            boolean isExpectedTeam = wantedTeam == null || teamByPlayerIndex(i) == wantedTeam;

            if (!isExpectedTeam) {
                continue;
            }

            var currentSlot = playerSlots.getSlot(i);

            boolean currentIndexIsJoinable;

            if (wantedTeamIsFull && !joiningPlayer.isBot()) {
                currentIndexIsJoinable = currentSlot.getPlayer().orElseThrow().isBot();
            }
            else {
                currentIndexIsJoinable = currentSlot.getPlayer().isEmpty();
            }

            //boolean noPresentPlayer = players.get(i) == null;
            boolean noPresentPlayer = currentSlot.getPlayer().isEmpty();

            if ( currentIndexIsJoinable) {
                playerIndex = i;
                replacedPlayer = currentSlot.getPlayer();
                assignPlayerToIndex(playerIndex, joiningPlayer);
                joined = true;

                if (noPresentPlayer) {
                    nbPlayers++;
                }
                break;
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

        return new JoinGameResult(playerIndex, replacedPlayer);

    }

    public boolean wantedTeamIsFull(ContreeTeam wantedTeam) {
        return playerSlots.stream().filter(ps -> ps.getPlayer().isPresent()
                && ps.getPlayer().get().getTeam().isPresent()
                && ps.getPlayer().get().getTeam().get() == wantedTeam).toList().size() == 2;
    }

    @Override
    public synchronized ContreePlayer leaveGameAndReplaceWithBotPlayer(ContreePlayer player) {
        if (player.isBot()) {
            throw new IllegalArgumentException("WTF ? A bot wants to leave the game ?");
        }
        // TODO #45 : find a way to not instantiate the bot player there, so that this class is not aware of ContreePlayer implementation
        ContreePlayer newBotPlayer = new ContreePlayerImpl("Bot " + player.getName(), new ContreeBotPlayerEventHandler());
        replacePlayer(player, newBotPlayer);
        return newBotPlayer;
    }

    private void replacePlayer(ContreePlayer replacedPlayer, ContreePlayer newPlayer) {
        if (!playerSlots.contains(replacedPlayer)) {
            throw new IllegalArgumentException(String.format("The player %s does not play in this game", replacedPlayer));
        }
        int replacedPlayerIndex = playerSlots.getSlot(replacedPlayer).getSlotNumber();
        assignPlayerToIndex(replacedPlayerIndex, newPlayer);
    }

    private void assignPlayerToIndex(int playerIndex, ContreePlayer player) {
        playerSlots.addPlayerToSlotIndex(playerIndex, player);
        assignTeamToPlayer(playerIndex);
    }

    public synchronized JoinGameResult joinGame(ContreePlayer joiningPlayer) {
        return joinGame(joiningPlayer, null);
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
        return playerSlots.isFull();
    }

    public boolean isJoinableByHumanPlayers() {
        return playerSlots.isJoinable();
    }

    private void assignTeamToPlayer(int playerIndex) {
        ContreeTeam team = teamByPlayerIndex(playerIndex);
        playerSlots.getSlot(playerIndex).getPlayer().orElseThrow().setTeam(team);
    }

    private ContreeTeam teamByPlayerIndex(int playerIndex) {
        return playerIndex % 2 == 0 ? ContreeTeam.TEAM1 : ContreeTeam.TEAM2;
    }

    /**
     * @return an unmodifiable list of the players
     */
    public List<ContreePlayer> getGamePlayers() {
        return  playerSlots.stream().map(ps -> ps.getPlayer().orElse(null)).toList();
    }

    @Override
    public ContreeGamePlayerSlots getPlayerSlots() {
        return playerSlots;
    }

    @Override
    public void receiveHandForPlayer(int playerIndex, List<ClassicalCard> hand) {
        playerSlots.getSlot(playerIndex).getPlayer().orElseThrow().receiveHand(hand);
    }
}
