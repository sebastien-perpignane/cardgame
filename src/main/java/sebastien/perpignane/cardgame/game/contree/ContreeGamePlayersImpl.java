package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreePlayerFactory;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;
import sebastien.perpignane.cardgame.player.util.PlayerSlot;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

class ContreeGamePlayersImpl implements ContreeGamePlayers {

    private final ContreeGamePlayerSlots playerSlots = new ContreeGamePlayerSlots();

    private volatile int nbPlayers = 0;

    public synchronized JoinGameResult joinGame(final ContreePlayer joiningPlayer, final ContreeTeam wantedTeam) {

        Objects.requireNonNull(joiningPlayer, "joiningPlayer cannot be null");

        throwsExceptionIfPlayerAlreadyJoined(joiningPlayer);

        Optional<ContreePlayer> replacedPlayer = Optional.empty();

        boolean wantedTeamIsFull = wantedTeamIsFull(wantedTeam);

        var eligibleSlots = playerSlots.stream()
                .filter(ps -> wantedTeam == null || teamByPlayerIndex(ps.getSlotNumber()) == wantedTeam)
                .toList();

        boolean successfulJoin = false;
        int playerIndex = -1;

        for (PlayerSlot<ContreePlayer> currentSlot : eligibleSlots) {

            boolean currentSlotIsJoinable;

            if (wantedTeamIsFull && !joiningPlayer.isBot()) {
                currentSlotIsJoinable = currentSlot.getPlayer().orElseThrow().isBot();
            }
            else {
                currentSlotIsJoinable = currentSlot.isEmpty();
            }

            boolean currentSlotIsEmpty = currentSlot.isEmpty();

            if ( currentSlotIsJoinable) {
                playerIndex = currentSlot.getSlotNumber();
                replacedPlayer = currentSlot.getPlayer();
                assignPlayerToIndex(playerIndex, joiningPlayer);
                successfulJoin = true;

                if (currentSlotIsEmpty) {
                    nbPlayers++;
                }
                break;
            }
        }

        if (!successfulJoin) {
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

    private void throwsExceptionIfPlayerAlreadyJoined(ContreePlayer joiningPlayer) {
        if (playerSlots.contains(joiningPlayer)) {
            throw new IllegalArgumentException( String.format("Player %s already joined the game", joiningPlayer) );
        }
    }

    private boolean wantedTeamIsFull(ContreeTeam wantedTeam) {

        if (wantedTeam == null) {
            return isFull();
        }

        return playerSlots.stream().filter(ps -> ps.getPlayer().isPresent()
                && ps.getPlayer().get().getTeam().isPresent()
                && ps.getPlayer().get().getTeam().get() == wantedTeam).toList().size() == 2;
    }

    @Override
    public synchronized ContreePlayer leaveGameAndReplaceWithBotPlayer(ContreePlayer player) {
        if (player.isBot()) {
            throw new IllegalArgumentException("WTF ? A bot wants to leave the game ?");
        }
        ContreePlayer newBotPlayer = ContreePlayerFactory.createBotPlayer(player.getName());
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
