package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ValuableCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.*;
import java.util.stream.Collectors;

record DealScoreResult(
        Map<ContreeTeam, Integer> rawScoreByTeam,
        Map<ContreeTeam, Integer> finalNotRoundedScoreByTeam,
        Map<ContreeTeam, Integer> finalRoundedScoreByTeam,
        boolean contractIsReached) {}

class DealScoreCalculator {

    static final int EXPECTED_CARD_SCORE_SUM = 162;

    static final int DIX_DE_DER_BONUS = 10;

    public DealScoreResult computeDealScores(ContreeDeal deal) {
        Map<Team, ? extends Collection<ContreeCard>> cardsByTeam;
        Team dixDeDerTeam;

        Objects.requireNonNull(deal);

        Map<ContreeTeam, Integer> scoreByTeam = new EnumMap<>(ContreeTeam.class);

        Set<ContreeTeam> allTeams = ContreeTeam.getTeams();

        if (deal.hasOnlyPassBids()) {
            var zeroScore = Map.of(ContreeTeam.TEAM1, 0, ContreeTeam.TEAM2, 0);
            return new DealScoreResult(
                    zeroScore,
                    zeroScore,
                    zeroScore,
                    false
            );
        }

        dixDeDerTeam = deal.lastTrick().orElseThrow().getWinnerTeam().orElseThrow();

        Optional<ContreeBid> contractBid = deal.getContractBid();
        if (contractBid.isEmpty()) {
            throw new IllegalStateException("It does not make sense to compute points if the deal has no contract");
        }

        cardsByTeam = deal.wonCardsByTeam();

        Map<ContreeTeam, Integer> cardScoreByTeam = allTeams.stream().collect(Collectors.toMap(
                team -> team,
                team -> computeCardPoints(cardsByTeam.get(team)) + (team == dixDeDerTeam ? DIX_DE_DER_BONUS : 0)
        ));

        boolean contractIsReached = contractIsReached(deal, cardScoreByTeam);

        int cardScoreSum = cardScoreByTeam.values().stream().mapToInt(i -> i).sum();

        if (cardScoreSum != EXPECTED_CARD_SCORE_SUM) {
            throw new IllegalStateException(String.format("Cheating detected : cardScore sum (including 10 de der) must be %d. Calculated sum is %d", EXPECTED_CARD_SCORE_SUM, cardScoreSum));
        }

        if ( deal.isDoubleBidExists() || deal.isRedoubleBidExists() || deal.isAnnouncedCapot() || deal.isCapot() ) {
            Map<ContreeTeam, Integer> doubledOrRedoubledOrCapotScore = computeDoubledOrRedoubledOrCapotDeal(deal, cardScoreByTeam);
            return new DealScoreResult(cardScoreByTeam, doubledOrRedoubledOrCapotScore, doubledOrRedoubledOrCapotScore, contractIsReached);
        }
        else if (!contractIsReached) {
            scoreByTeam.put(deal.getAttackTeam().orElseThrow(), 0);
            var winnerScore = deal.isCapot() ? ContreeBidValue.CAPOT.getExpectedScore() : ContreeBidValue.HUNDRED_SIXTY.getExpectedScore();
            scoreByTeam.put(deal.getDefenseTeam().orElseThrow(), winnerScore);
        }
        else {
            scoreByTeam = new EnumMap<>(cardScoreByTeam);
        }

        Map<ContreeTeam, Integer> finalScoreBeforeRound = new EnumMap<>(scoreByTeam);

        roundScores(scoreByTeam);

        return new DealScoreResult(cardScoreByTeam, finalScoreBeforeRound, scoreByTeam, contractIsReached);

    }

    Integer computeCardPoints(Collection<? extends ValuableCard> contreeCards) {
        return contreeCards.stream().mapToInt(ValuableCard::getGamePoints).sum();
    }

    private Map<ContreeTeam, Integer> computeDoubledOrRedoubledOrCapotDeal(ContreeDeal deal, Map<ContreeTeam, Integer> cardScoreByTeam) {
        ContreeTeam winnerTeam;
        ContreeTeam loserTeam;
        if (contractIsReached(deal, cardScoreByTeam)) {
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
        }
        if (deal.isAnnouncedCapot()) {
            winnerBaseScore = ContreeBidValue.CAPOT.getExpectedScore() * 2;
        }

        Map<ContreeTeam, Integer> scoreByTeam = new EnumMap<>(ContreeTeam.class);

        scoreByTeam.put(winnerTeam, winnerBaseScore * multiplier);
        scoreByTeam.put(loserTeam, 0);

        return scoreByTeam;

    }

    private boolean contractIsReached(ContreeDeal deal, Map<ContreeTeam, Integer> cardScoreByTeam) {

        if (deal.isAnnouncedCapot()) {
            return deal.isCapotMadeByAttackTeam();
        }

        var attackTeam = deal.getAttackTeam().orElseThrow();
        var contractBid = deal.getContractBid().orElseThrow(
                () -> new IllegalStateException("Computing score for a no bid deal does not make sense")
        );

        int attackTeamScore = cardScoreByTeam.get(attackTeam);
        int expectedScore = contractBid.bidValue().getExpectedScore();

        return attackTeamScore >= expectedScore;
    }

    private void roundScores(Map<ContreeTeam, Integer> scoreByTeam) {
        for (Map.Entry<ContreeTeam, Integer> teamScore : scoreByTeam.entrySet()) {
            int score = teamScore.getValue();
            // round to the ten
            var roundScore = ((score+5)/10)*10;
            scoreByTeam.put(teamScore.getKey(), roundScore);
        }
    }

}
