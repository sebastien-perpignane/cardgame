package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.util.PlayerSlot;

import java.util.Set;

class ContreeTrickPlayersImpl implements ContreeTrickPlayers {

    private final ContreeDealPlayers dealPlayers;

    private ContreeTrick currentTrick;

    private int currentPlayerIndex;

    public ContreeTrickPlayersImpl(ContreeDealPlayers dealPlayers) {
        this.dealPlayers = dealPlayers;
    }

    @Override
    public void setCurrentTrick(ContreeTrick currentTrick) {
        if (this.currentTrick == null) {
            currentPlayerIndex = 0;
        }
        else {
            currentPlayerIndex = dealPlayers.indexOf(this.currentTrick.getWinner().orElseThrow());
        }
        updatePlayerStates();
        this.currentTrick = currentTrick;
    }

    @Override
    public PlayerSlot<ContreePlayer> getCurrentPlayerSlot() {
        return dealPlayers.getCurrentDealPlayerSlots().get(currentPlayerIndex);
    }

    @Override
    public void gotToNextPlayer() {
        if (currentPlayerIndex + 1 == dealPlayers.getCurrentDealPlayerSlots().size()) {
            currentPlayerIndex = 0;
        }
        else {
            currentPlayerIndex++;
        }
        updatePlayerStates();
    }

    private void updatePlayerStates() {
        var currentPlayer = dealPlayers.getCurrentDealPlayers().get(currentPlayerIndex);
        dealPlayers.setPlayingPlayer(currentPlayer);
    }

    @Override
    public void notifyCurrentPlayerTurn(Set<ClassicalCard> allowedCards) {
        var currentPlayer = dealPlayers.getCurrentDealPlayerSlots().get(currentPlayerIndex).getPlayer().orElseThrow();
        currentPlayer.onPlayerTurn(allowedCards);
    }
}