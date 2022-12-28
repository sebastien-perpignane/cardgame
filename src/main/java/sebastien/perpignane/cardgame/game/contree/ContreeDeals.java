package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardDealer;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ContreeDeals {

    private String gameId;

    private ContreeDealPlayers dealPlayers;

    private final List<ContreeDeal> deals = new ArrayList<>();

    private final BiddableValuesFilter biddableValuesFilter;

    private final PlayableCardsFilter playableCardsFilter;

    private final CardDealer cardDealer;

    private final DealScoreCalculator dealScoreCalculator;

    private ContreeDeal currentDeal;

    private final ContreeGameScore gameScore;

    private final ContreeGameEventSender gameEventSender;

    public ContreeDeals(
            ContreeGameScore gameScore,
            DealScoreCalculator dealScoreCalculator,
            BiddableValuesFilter biddableValuesFilter,
            PlayableCardsFilter playableCardsFilter,
            CardDealer cardDealer,
            ContreeGameEventSender eventSender
    ) {
        this.gameScore              = gameScore;
        this.dealScoreCalculator    = dealScoreCalculator;
        this.biddableValuesFilter   = biddableValuesFilter;
        this.playableCardsFilter    = playableCardsFilter;
        this.cardDealer             = cardDealer;
        this.gameEventSender        = eventSender;
    }

    public void startDeals(
            String gameId,
            ContreeDealPlayers dealPlayers
    ) {
        this.gameId = gameId;
        this.dealPlayers = dealPlayers;
        createAndStartNewDeal();
    }

    private void createAndStartNewDeal() {

        if (isMaximumScoreReached()) {
            throw new IllegalStateException("Game is over, new deal cannot be started");
        }

        currentDeal = new ContreeDeal(
                new ContreeDealBids(biddableValuesFilter),
                new ContreeTricks(playableCardsFilter, gameEventSender),
                cardDealer,
                new ContreeDealScore(dealScoreCalculator),
                gameEventSender
        );
        deals.add(currentDeal);

        dealPlayers.setCurrentDeal(currentDeal);

        String dealId = gameId + "-" + deals.size();
        currentDeal.startDeal(deals.size(), dealId, dealPlayers);
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
            // TODO Send updated score event
            System.out.println("Game score:");
            ContreeTeam.getTeams().stream().sorted().forEach(t -> System.out.printf("\t%s: %d / %d%n", t, gameScore.getTeamScore(t), gameScore.getMaxScore()));

            if (!isMaximumScoreReached()) {
                createAndStartNewDeal();
            }
        }
    }

    public void manageLeavingPlayer(ContreePlayer leavingPlayer, ContreePlayer newPlayer) {
        updateCurrentBidderIfRequired(leavingPlayer, newPlayer);
        updateCurrentPlayerIfRequired(leavingPlayer, newPlayer);
    }

    private void updateCurrentPlayerIfRequired(ContreePlayer oldPlayer, ContreePlayer newPlayer) {
        if (isCurrentPlayer(oldPlayer)) {
            currentDeal.updateCurrentPlayer(newPlayer);
        }
    }

    private void updateCurrentBidderIfRequired(ContreePlayer oldPlayer, ContreePlayer newPlayer) {
        if (isCurrentBidder(oldPlayer)) {
            currentDeal.updateCurrentBidder(newPlayer);
        }
    }

    private boolean isCurrentBidder(ContreePlayer player) {
        if (currentDeal == null) {
            return false;
        }
        if (currentDeal.getCurrentBidder().isEmpty()) {
            return false;
        }
        return currentDeal.getCurrentBidder().get() == player;
    }

    private boolean isCurrentPlayer(ContreePlayer player) {
        if (currentDeal == null) {
            return false;
        }
        if (currentDeal.getCurrentPlayer().isEmpty()) {
            return false;
        }
        return currentDeal.getCurrentPlayer().get() == player;
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

    public Optional<ContreeTeam> getWinner() {
        return gameScore.getWinner();
    }

}
