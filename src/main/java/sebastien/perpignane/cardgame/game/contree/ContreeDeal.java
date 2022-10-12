package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.*;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/*

    End of deal :
        - Only "NONE" bids
        - All cards played
 */

enum DealStep {
    NOT_STARTED,
    BID,
    PLAY,
    OVER
}

class ContreeDeal {

    private final static int NB_TRICKS_PER_DEAL = 8;

    private final String dealId;

    private CardSuit trumpSuit;

    private final List<ContreeTrick> tricks = new ArrayList<>(NB_TRICKS_PER_DEAL);

    private ContreeTrick currentTrick = null;

    DealStep dealStep;

    private final ContreeGameEventSender eventSender;

    private final ContreeDealScore score;

    private final ContreeDealPlayers dealPlayers;

    private ContreeDealBids dealBids;

    public Optional<ContreeTeam> getAttackTeam() {
        return dealPlayers.getAttackTeam();
    }

    public Optional<ContreeTeam> getDefenseTeam() {
        return dealPlayers.getDefenseTeam();
    }

    public ContreeDeal(String dealId, List<ContreePlayer> players, ContreeGameEventSender eventSender) {

        if (players.size() != ContreeGame.NB_PLAYERS) {
            throw new IllegalArgumentException(String.format("%d players are expected. Found : %d", ContreeGame.NB_PLAYERS, players.size()));
        }

        this.dealId = dealId;
        dealStep = DealStep.NOT_STARTED;
        dealPlayers = new ContreeDealPlayers(players, this);

        this.eventSender = eventSender;

        score = new ContreeDealScore(this);

    }

    void startDeal() {
        eventSender.sendStartOfDealEvent(dealId);
        dealStep = DealStep.BID;
        eventSender.sendBidStepStartedEvent(dealId);
        CardSetShuffler shuffler = new CardSetShufflerImpl();
        List<ClassicalCard> cards = shuffler.shuffle(CardSet.GAME_32);
        distributeCardsToPlayers(cards);
        this.dealBids = new ContreeDealBids(new ContreeBidPlayers(dealPlayers.getPlayers()));
        dealBids.startBids();
    }

    private void distributeCardsToPlayers(List<ClassicalCard> cards) {

        int numberOfPlayers = dealPlayers.getNumberOfPlayers();

        if (cards.size() % numberOfPlayers != 0) {
            throw new IllegalStateException("The cards cannot be equally distributed to all players");
        }

        // TODO make it configurable
        List<Integer> cardGroups = List.of(3, 3, 2);

        CardDealer cardDealer = new CardDealer(cards, numberOfPlayers, cardGroups);
        var distributedCards = cardDealer.dealCards();

        for (int i = 0 ; i < distributedCards.size() ; i++) {
            dealPlayers.receiveHandForPlayer(i, distributedCards.get(i));
        }

    }

    public void placeBid(ContreeBid bid) {

        eventSender.sendPlacedBidEvent(dealId, bid);
        dealBids.placeBid(bid);


        if ( dealBids.bidsAreOver() ) {

            if ( dealBids.hasOnlyNoneBids() ) {
                manageEndOfDeal();
            }
            else {
                startPlay();
            }

        }

    }

    public boolean hasOnlyNoneBids()  {
        return dealBids.hasOnlyNoneBids();
    }


    private void manageEndOfDeal() {

        Consumer<String> endOfStepEventSender;
        if (isPlayStep()) {
            endOfStepEventSender = eventSender::sendPlayStepEndedEvent;
        }
        else if (isBidStep()) {
            endOfStepEventSender = eventSender::sendBidStepEndedEvent;
        }
        else {
            throw new IllegalStateException(String.format("Unexpected deal step while managing end of deal : %s", dealStep));
        }
        endOfStepEventSender.accept(dealId);
        dealStep = DealStep.OVER;
        eventSender.sendEndOfDealEvent(dealId);
    }

    Optional<ContreeBid> highestBid() {
        return dealBids.highestBid();
    }

    public boolean isAnnouncedCapot() {
        return dealBids.isAnnouncedCapot();
    }

    public boolean isDoubleBidExists() {
        return dealBids.isDoubleBidExists();
    }

    public boolean isRedoubleBidExists() {
        return dealBids.isRedoubleBidExists();
    }

    public Optional<ContreeTeam> teamDoingCapot() {

        if  (!isOver()) {
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
                getTricks().stream().collect(Collectors.groupingBy(ContreeTrick::getWinnerTeam));

        return ContreeTeam.getTeams().stream().collect(Collectors.toMap(
                team -> team,
                team -> tricksByWinnerTeam.getOrDefault(team, Collections.emptyList()).stream()
                        .map(trick -> trick.getPlayedCards().stream().map(PlayedCard::card).toList())
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet())
        ));
    }

    public Optional<ContreeTrick> lastTrick() {
        if (tricks.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(tricks.get(tricks.size() - 1));
    }

    public boolean isCapot() {
        return teamDoingCapot().isPresent();
    }

    public Optional<ContreeBid> findDealContractBid() {
        return dealBids.findDealContractBid();
    }

    private void startPlay() {
        trumpSuit = dealBids.highestBid().orElseThrow().cardSuit();
        dealStep = DealStep.PLAY;
        eventSender.sendBidStepEndedEvent(dealId);
        eventSender.sendPlayStepStartedEvent(dealId, trumpSuit);
        initNewCurrentTrick();
        currentTrick.startTrick();
    }

    public void playerPlays(ContreePlayer player, ClassicalCard card) {

        if (!isPlayStep()) {
            throw new IllegalStateException(String.format("Cheater detected : %s is trying to play a card on a deal not in PLAY step", player));
        }

        currentTrick.playerPlays(player, card);
        if (currentTrick.isEndOfTrick()) {

            var displayCards = currentTrick.getPlayedCards().stream().map(pc -> String.format("Player %s : %s", pc.player(), pc.card().getCard())).collect(Collectors.joining(", "));
            System.out.printf("Trick %s won by %s. Cards : %s%n", currentTrick, currentTrick.getWinner(), displayCards);

            if (isMaxNbTricksReached()) {
                manageEndOfDeal();
            }
            else {
                initNewCurrentTrick();
                currentTrick.startTrick();
            }

        }

    }

    private boolean isMaxNbTricksReached() {
        return tricks.size() == NB_TRICKS_PER_DEAL;
    }

    private void initNewCurrentTrick() {
        if (currentTrick == null) {
            currentTrick = new ContreeTrick(trickId(), dealPlayers.getPlayers(), trumpSuit, eventSender);
        }
        else {
            currentTrick = new ContreeTrick(trickId(), dealPlayers.getPlayers(), currentTrick.getWinner(), trumpSuit, eventSender);
        }
        tricks.add(currentTrick);
        eventSender.sendNewTrickEvent(currentTrick.getTrickId(), trumpSuit);
    }

    private String trickId() {
        return dealId + "-" + (tricks.size() + 1);
    }

    public List<ContreeTrick> getTricks() {
        return tricks;
    }

    public ContreeDealScore getScore() {
        return score;
    }

    public boolean isBidStep() {
        return dealStep == DealStep.BID;
    }

    public boolean isPlayStep() {
        return dealStep == DealStep.PLAY;
    }

    public boolean isOver() {
        return dealStep == DealStep.OVER;
    }

    public boolean isNotStarted() {
        return dealStep == DealStep.NOT_STARTED;
    }

}
