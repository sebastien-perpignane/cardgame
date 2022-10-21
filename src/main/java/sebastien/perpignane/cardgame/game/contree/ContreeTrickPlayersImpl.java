package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Collection;

public class ContreeTrickPlayersImpl implements ContreeTrickPlayers {

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
            currentPlayerIndex = dealPlayers.getCurrentDealPlayers().indexOf(this.currentTrick.getWinner());
        }
        this.currentTrick = currentTrick;
    }

    @Override
    public ContreePlayer getCurrentPlayer() {
        return dealPlayers.getCurrentDealPlayers().get(currentPlayerIndex);
    }

    @Override
    public void gotToNextPlayer() {
        if (currentPlayerIndex + 1 == dealPlayers.getCurrentDealPlayers().size()) {
            currentPlayerIndex = 0;
        }
        else {
            currentPlayerIndex++;
        }

    }

    @Override
    public void notifyCurrentPlayerTurn(Collection<ClassicalCard> allowedCards) {
        var currentPlayer = dealPlayers.getCurrentDealPlayers().get(currentPlayerIndex);
        currentPlayer.onPlayerTurn(allowedCards);
    }
}