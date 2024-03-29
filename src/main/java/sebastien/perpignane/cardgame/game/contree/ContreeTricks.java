package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class ContreeTricks {

    static final int NB_TRICKS_PER_DEAL = 8;

    private final List<ContreeTrick> tricks;

    private final ContreeGameEventSender eventSender;

    private ContreeDeal deal;

    private ContreeTrick currentTrick;

    private ContreeTrickPlayers trickPlayers;

    private final PlayableCardsFilter playableCardsFilter;

    public ContreeTricks(PlayableCardsFilter playableCardsFilter, ContreeGameEventSender eventSender) {
        this.playableCardsFilter = playableCardsFilter;
        this.eventSender = eventSender;
        tricks = new ArrayList<>();
    }

    public void startTricks(ContreeDeal deal, ContreeTrickPlayers trickPlayers) {
        this.deal = deal;
        this.trickPlayers = trickPlayers;
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
        eventSender.sendNewTrickEvent(currentTrick.getTrickId(), currentTrick.getTrumpSuit());
        currentTrick.startTrick();
    }

    private String trickId() {
        return deal.getDealId() + "-" + (tricks.size() + 1);
    }

    private boolean tricksAreOver = false;

    public void playerPlays(ContreePlayer player, ClassicalCard card) {
        currentTrick.playerPlays(player, card);
        if ( currentTrick.isOver() ) {
            eventSender.sendEndOfTrickEvent(currentTrick.getTrickId(), currentTrick.getWinner().orElseThrow());
            if ( tricks.size() == NB_TRICKS_PER_DEAL ) {
                tricksAreOver = true;
                currentTrick = null;
            }
            else {
                startNewTrick();
            }

        }
    }

    public boolean tricksAreOver() {
        return tricksAreOver;
    }

    public Optional<ContreeTeam> teamWhoDidCapot() {

        if  (!tricksAreOver) {
            return Optional.empty();
        }

        var teamsWinningTrick = tricks.stream()
                .map(t -> t.getWinner().orElseThrow())
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
                tricks.stream()
                    .filter(t -> t.getWinnerTeam().isPresent())
                    .collect(
                        Collectors.groupingBy( t -> t.getWinnerTeam().get() )
                    );

        return ContreeTeam.getTeams().stream().collect(Collectors.toMap(
                team -> team,
                team -> tricksByWinnerTeam.getOrDefault(team, Collections.emptyList()).stream()
                        .map(trick -> trick.getPlayedCards().stream().map(ContreePlayedCard::card).toList())
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet())
        ));
    }

    Optional<ContreeTrick> lastTrick() {
        return tricks.isEmpty() ? Optional.empty() : Optional.of(tricks.get(tricks.size() - 1));
    }

    public boolean isCapot() {
        return teamWhoDidCapot().isPresent();
    }

    public boolean isCapotMadeByAttackTeam() {
        var attackTeam = deal.getAttackTeam().orElseThrow(); // When managing tricks, an attack team *must* be defined.
        return teamWhoDidCapot().map(teamCapot -> teamCapot == attackTeam).orElse(false);
    }

    public int nbOverTricks() {
        return tricks.stream().filter(ContreeTrick::isOver).mapToInt(t -> 1).sum();
    }

    public int nbOngoingTricks() {
        return tricks.stream().filter(Predicate.not(ContreeTrick::isOver)).mapToInt(t -> 1).sum();
    }

    public Optional<ContreePlayer> getCurrentPlayer() {
        return currentTrick == null ? Optional.empty() : currentTrick.getCurrentPlayer();
    }

}
