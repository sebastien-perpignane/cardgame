package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.card.contree.ValuableCard;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.*;
import java.util.stream.Collectors;

class DealScoreCalculator {

    private final ContreeDeal deal;

    private Team dixDeDerTeam;

    private Map<Team, Integer> cardScoreByTeam = new HashMap<>();

    private final Map<Team, Integer> scoreByTeam = new HashMap<>();

    private Map<Team, ? extends Collection<ContreeCard>> cardsByTeam = new HashMap<>();

    public DealScoreCalculator(ContreeDeal deal) {
        this.deal = deal;
    }

    public Map<Team, Integer> computeDealScores() {

        Set<ContreeTeam> allTeams = ContreeTeam.getTeams();

        if (deal.hasOnlyNoneBids()) {
            return allTeams.stream().collect(Collectors.toMap(t -> t, t -> 0));
        }

        dixDeDerTeam = deal.lastTrick().orElseThrow().getWinnerTeam();

        Optional<ContreeBid> contractBid = deal.findDealContractBid();
        if (contractBid.isEmpty()) {
            throw new IllegalStateException("It does not make sense to compute points if the deal has no contract");
        }

        cardsByTeam = deal.wonCardsByTeam();

        cardScoreByTeam = allTeams.stream().collect(Collectors.toMap(
                team -> team,
                team -> computeCardPoints(cardsByTeam.get(team)) + (team == dixDeDerTeam ? 10 : 0)
        ));

        if (deal.isDoubleBidExists() || deal.isRedoubleBidExists() || deal.isCapot()) {
            computeDoubledOrRedoubledOrCapotDeal();
            return scoreByTeam;
        }
        else if (!contractIsReached()) {
            scoreByTeam.put(deal.getAttackTeam().orElseThrow(), 0);
            scoreByTeam.put(deal.getDefenseTeam().orElseThrow(), ContreeBidValue.HUNDRED_SIXTY.getExpectedScore());
        }
        else {
            scoreByTeam.putAll(cardScoreByTeam);

        }

        roundScores();
        return scoreByTeam;

    }

    Integer computeCardPoints(Collection<? extends ValuableCard> contreeCards) {
        return contreeCards.stream().mapToInt(ValuableCard::getGamePoints).sum();
    }

    private void computeDoubledOrRedoubledOrCapotDeal() {
        Team winnerTeam;
        Team loserTeam;
        if (contractIsReached()) {
            winnerTeam = deal.getAttackTeam().orElseThrow();
            loserTeam = deal.getDefenseTeam().orElseThrow();
        }
        else {
            winnerTeam = deal.getDefenseTeam().orElseThrow();
            loserTeam = deal.getAttackTeam().orElseThrow();
        }

        int multiplier = 1;
        if (deal.isDoubleBidExists()) {
            multiplier = 2;
        }
        if (deal.isRedoubleBidExists()) {
            multiplier = 4;
        }

        int winnerBaseScore = ContreeBidValue.HUNDRED_SIXTY.getExpectedScore();

        if (deal.isCapot()) {
            winnerBaseScore = ContreeBidValue.CAPOT.getExpectedScore();
            if (deal.isAnnouncedCapot()) {
                winnerBaseScore *= 2;
            }
        }

        scoreByTeam.put(winnerTeam, winnerBaseScore * multiplier);
        scoreByTeam.put(loserTeam, 0);

    }

    private boolean contractIsReached() {
        var attackTeam = deal.getAttackTeam().orElseThrow();
        if (deal.findDealContractBid().isEmpty()) {
            throw new IllegalStateException("Computing score for a no bid deal does not make sense");
        }
        if (deal.isAnnouncedCapot()) {
            return cardsByTeam.get(attackTeam).size() == CardSet.GAME_32.getGameCards().size();
        }
        return cardScoreByTeam.get(attackTeam) >= deal.findDealContractBid().get().bidValue().getExpectedScore();
    }

    private void roundScores() {
        for (Team t : scoreByTeam.keySet()) {
            int score = scoreByTeam.get(t);
            var roundScore = ((score+5)/10)*10;
            scoreByTeam.put(t, roundScore);
        }
    }

}
