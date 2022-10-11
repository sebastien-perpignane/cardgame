package sebastien.perpignane.cardgame.game.contree;

import org.apache.commons.collections4.iterators.LoopingListIterator;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.List;
import java.util.Optional;

public class ContreeDealPlayers {

    private final ContreeDeal deal;

    private final List<ContreePlayer> players;

    private ContreeTeam attackTeam;

    private ContreeTeam defenseTeam;

    public ContreeDealPlayers(List<ContreePlayer> players, ContreeDeal deal) {

        this.deal = deal;
        this.players = players;

    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public void receiveHandForPlayer(int playerIndex, List<ClassicalCard> hand) {
        players.get(playerIndex).receiveHand(hand);
    }

    public Optional<ContreeTeam> getAttackTeam() {
        if (attackTeam == null && !deal.isBidStep() && !deal.isNotStarted()) {
            computeAttackAndDefenseTeams();
        }
        return Optional.ofNullable(attackTeam);
    }

    public Optional<ContreeTeam> getDefenseTeam() {
        if (defenseTeam == null && !deal.isBidStep() && !deal.isNotStarted()) {
            computeAttackAndDefenseTeams();
        }
        return Optional.ofNullable(defenseTeam);
    }

    private void computeAttackAndDefenseTeams() {

        var optionalContractBid = deal.findDealContractBid();
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

    public List<ContreePlayer> getPlayers() {
        return players;
    }

}
