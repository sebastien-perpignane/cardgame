package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;
import sebastien.perpignane.cardgame.player.util.PlayerSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class ContreeDealPlayersImpl implements ContreeDealPlayers {

    private final ContreeGamePlayers gamePlayers;

    private int currentDealerIndex = 3;

    private ContreeDeal currentDeal;

    private ContreeTeam attackTeam;

    private ContreeTeam defenseTeam;

    public ContreeDealPlayersImpl(ContreeGamePlayers gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    @Override
    public void setCurrentDeal(ContreeDeal newDeal) {
        if (currentDeal == newDeal) {
            throw new IllegalArgumentException(String.format("deal %s is already the current deal", newDeal));
        }
        if (currentDeal != null) {
            updateCurrentDealerIndex();
        }
        this.currentDeal = newDeal;

    }

    private void updateCurrentDealerIndex() {
        currentDealerIndex = nextPlayerIndex(currentDealerIndex);
    }

    @Override
    public int getNumberOfPlayers() {
        return gamePlayers.getNbPlayers();
    }

    @Override
    public void receiveHandForPlayer(int playerIndex, List<ClassicalCard> hand) {
        gamePlayers.receiveHandForPlayer(playerIndex, hand);
    }

    @Override
    public Optional<ContreeTeam> getCurrentDealAttackTeam() {
        if (attackTeam == null && !currentDeal.isBidStep() && !currentDeal.isNotStarted()) {
            computeAttackAndDefenseTeams();
        }
        return Optional.ofNullable(attackTeam);
    }

    @Override
    public Optional<ContreeTeam> getCurrentDealDefenseTeam() {
        if (defenseTeam == null && !currentDeal.isBidStep() && !currentDeal.isNotStarted()) {
            computeAttackAndDefenseTeams();
        }
        return Optional.ofNullable(defenseTeam);
    }

    private void computeAttackAndDefenseTeams() {

        var optionalContractBid = currentDeal.getContractBid();
        if (optionalContractBid.isPresent()) {

            var contractBid = optionalContractBid.get();
            ContreeTeam.getTeams().forEach(t -> {
                if (t == contractBid.player().getTeam().orElseThrow()) {
                    attackTeam = t;
                }
                else {
                    defenseTeam = t;
                }
            });

        }

    }

    List<ContreePlayer> rollPlayerFromIndex(int playerIndex) {

        if (playerIndex + 1 > ContreeGamePlayers.NB_PLAYERS) {
            throw new IllegalArgumentException(String.format("Player index %d is not valid", playerIndex));
        }

        List<ContreePlayer> newPlayerList = new ArrayList<>();

        for (int i = playerIndex ; newPlayerList.size() < ContreeGamePlayers.NB_PLAYERS ; ) {
            newPlayerList.add(gamePlayers.getGamePlayers().get(i));
            i = nextPlayerIndex(i);
        }

        return newPlayerList;

    }

    List<PlayerSlot<ContreePlayer>> rollPlayerSlotsFromIndex(int playerIndex) {

        if (playerIndex + 1 > ContreeGamePlayers.NB_PLAYERS) {
            throw new IllegalArgumentException(String.format("Player index %d is not valid", playerIndex));
        }

        List<PlayerSlot<ContreePlayer>> newPlayerList = new ArrayList<>();

        for (int i = playerIndex ; newPlayerList.size() < ContreeGamePlayers.NB_PLAYERS ; ) {
            newPlayerList.add(gamePlayers.getPlayerSlots().getSlot(i));
            i = nextPlayerIndex(i);
        }

        return newPlayerList;

    }

    private int nextPlayerIndex(int playerIndex) {
        if (playerIndex + 1 == NB_PLAYERS) {
            return 0;
        }
        else {
            return playerIndex + 1;
        }
    }

    @Override
    public List<ContreePlayer> getCurrentDealPlayers() {
        return rollPlayerFromIndex( nextPlayerIndex(currentDealerIndex) );
    }

    @Override
    public List<PlayerSlot<ContreePlayer>> getCurrentDealPlayerSlots() {
        return rollPlayerSlotsFromIndex(nextPlayerIndex(currentDealerIndex));
        //return gamePlayers.getPlayerSlots().stream().toList();
    }

    @Override
    public ContreeBidPlayers buildBidPlayers() {
        return new ContreeBidPlayersImpl(this);
    }

    @Override
    public ContreeTrickPlayers buildTrickPlayers() {
        return new ContreeTrickPlayersImpl(this);
    }

    @Override
    public int indexOf(ContreePlayer player) {
        return getCurrentDealPlayers().indexOf(player);
    }

}
