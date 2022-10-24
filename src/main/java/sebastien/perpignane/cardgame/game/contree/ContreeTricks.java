package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ContreeTricks {

    final static int NB_TRICKS_PER_DEAL = 8;

    private final List<ContreeTrick> tricks = new ArrayList<>();

    private final ContreeGameEventSender eventSender;

    private final ContreeDeal deal;

    private ContreeTrick currentTrick;

    private final ContreeTrickPlayers trickPlayers;

    private final PlayableCardsFilter playableCardsFilter;

    public ContreeTricks(ContreeDeal deal, ContreeTrickPlayers trickPlayers, PlayableCardsFilter playableCardsFilter) {
        this.deal = deal;
        this.trickPlayers = trickPlayers;
        this.playableCardsFilter = playableCardsFilter;
        this.eventSender = deal.getEventSender();
    }

    public void startTricks() {
        if (!tricks.isEmpty()) {
            throw new IllegalStateException("Tricks are already started");
        }
        startNewTrick();
    }

    private void startNewTrick() {

        currentTrick = new ContreeTrick(
                deal,
                trickId(),
                trickPlayers,
                playableCardsFilter
        );

        tricks.add(currentTrick);
        eventSender.sendNewTrickEvent(currentTrick.getTrickId(), deal.getTrumpSuit());
        currentTrick.startTrick();
    }

    private String trickId() {
        return deal.getDealId() + "-" + (tricks.size() + 1);
    }

    private boolean maxNbOverTricksReached = false;

    public void playerPlays(ContreePlayer player, ClassicalCard card) {
        currentTrick.playerPlays(player, card);
        if ( currentTrick.isOver() ) {

            var displayCards = currentTrick.getPlayedCards().stream().map(pc -> String.format("Player %s : %s", pc.player(), pc.card().getCard())).collect(Collectors.joining(", "));
            // TODO send event
            System.out.printf("Trick %s won by %s. Cards : %s%n", currentTrick, currentTrick.getWinner(), displayCards);

            if ( tricks.size() == NB_TRICKS_PER_DEAL ) {
                maxNbOverTricksReached = true;
            }
            else {
                startNewTrick();
            }

        }
    }

    public boolean isMaxNbOverTricksReached() {
        return maxNbOverTricksReached;
    }

    public Optional<ContreeTeam> teamDoingCapot() {

        if  (!maxNbOverTricksReached) {
            throw new IllegalStateException("Team doing capot cannot be computed if the deal is not over");
        }

        var teamsWinningTrick = tricks.stream()
                .map(ContreeTrick::getWinner)
                .map(ContreePlayer::getTeam)
                .map(Optional::orElseThrow)
                .collect(Collectors.toSet());

        if (teamsWinningTrick.size() == 1) {
            return Optional.of(teamsWinningTrick.iterator().next());
        }
        return Optional.empty();
    }

    public Map<Team, Set<ContreeCard>> wonCardsByTeam() {

        Map<Team, List<ContreeTrick>> tricksByWinnerTeam =
                tricks.stream().collect(Collectors.groupingBy(ContreeTrick::getWinnerTeam));

        return ContreeTeam.getTeams().stream().collect(Collectors.toMap(
                team -> team,
                team -> tricksByWinnerTeam.getOrDefault(team, Collections.emptyList()).stream()
                        .map(trick -> trick.getPlayedCards().stream().map(PlayedCard::card).toList())
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet())
        ));
    }

    Optional<ContreeTrick> lastTrick() {
        if (tricks.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(tricks.get(tricks.size() - 1));
    }

    public boolean isCapot() {
        return teamDoingCapot().isPresent();
    }

    public int nbOverTricks() {
        return (int) tricks.stream().filter(ContreeTrick::isOver).count();
    }

    public int nbOngoingTricks() {
        return (int) tricks.stream().filter(Predicate.not(ContreeTrick::isOver)).count();
    }

}
