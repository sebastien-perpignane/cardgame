package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class ContreeDeals {

    private final ContreeGame game;

    private final ContreeDealPlayers dealPlayers;

    private final List<ContreeDeal> deals = new ArrayList<>();

    private ContreeDeal currentDeal;

    private final ContreeGameScore gameScore = new ContreeGameScore(1000); // TODO make max score configurable

    private final ContreeGameEventSender gameEventSender;

    public ContreeDeals(ContreeGame game, ContreeDealPlayers contreeDealPlayers) {
        this.game = game;
        this.dealPlayers = contreeDealPlayers;
        this.gameEventSender = game.getEventSender();
    }

    public void startDeals() {
        createAndStartNewDeal();
    }

    private void createAndStartNewDeal() {

        if (game.isOver()) {
            throw new IllegalStateException("Game is over, new deal cannot be started");
        }

        currentDeal = new ContreeDeal(game, dealPlayers);
        deals.add(currentDeal);

        dealPlayers.setCurrentDeal(currentDeal);

        currentDeal.startDeal();
    }

    public void placeBid(ContreePlayer player, ContreeBidValue bidValue, CardSuit cardSuit) {
        currentDeal.placeBid(new ContreeBid(player, bidValue, cardSuit));
        if (currentDeal.isOver()) {
            createAndStartNewDeal();
        }
    }

    public void playCard(ContreePlayer player, ClassicalCard card) {

        if (isMaximumScoreReached()) {
            throw new IllegalStateException("Maximum score is reached");
        }

        currentDeal.playerPlays(player, card);
        if (currentDeal.isOver()) {
            gameScore.addDealScore(currentDeal);
            // FIXME Send updated score event
            ContreeTeam.getTeams().forEach(t -> System.out.printf("Score of Team %s : %d%n", t, gameScore.getTeamScore(t)));

            if (!isMaximumScoreReached()) {
                createAndStartNewDeal();
            }
        }
    }

    public ContreeDeal getCurrentDeal() {
        return currentDeal;
    }

    public int getNbDeals() {
        return deals.size();
    }

    public int nbOverDeals() {
        return (int) deals.stream().filter(ContreeDeal::isOver).count();
    }

    public int nbOngoingDeals() {
        return (int) deals.stream().filter(Predicate.not(ContreeDeal::isOver)).count();
    }

    public boolean isMaximumScoreReached() {
        return gameScore.isMaximumScoreReached();
    }

    public ContreeTeam getWinner() {
        return gameScore.getWinner();
    }

}
