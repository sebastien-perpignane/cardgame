package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.*;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

class ContreeDeal {

    private enum DealStep {
        NOT_STARTED,
        BID,
        PLAY,
        OVER
    }

    private String dealId;

    private DealStep dealStep;

    private ContreeDealPlayers players;

    private final ContreeDealScore score;

    private final ContreeDealBids bids;

    private CardSuit trumpSuit;

    private final ContreeTricks tricks;

    private final ContreeGameEventSender eventSender;

    public ContreeDeal(
            ContreeDealBids contreeDealBids,
            ContreeTricks tricks,
            ContreeDealScore dealScore,
            ContreeGameEventSender gameEventSender
    ) {
        this.dealStep        = DealStep.NOT_STARTED;
        this.eventSender     = gameEventSender;
        this.bids            = contreeDealBids;
        this.tricks          = tricks;
        this.score           = dealScore;
    }

    void startDeal(
            String dealId,
            ContreeDealPlayers dealPlayers
    ) {

        this.dealId = dealId;
        this.players = dealPlayers;

        dealStep = DealStep.BID;

        eventSender.sendStartOfDealEvent(dealId);
        eventSender.sendBidStepStartedEvent(dealId);

        CardSetShuffler shuffler    = new CardSetShufflerImpl();
        List<ClassicalCard> cards   = shuffler.shuffle(CardSet.GAME_32);
        distributeCardsToPlayers(cards);

        bids.startBids(dealPlayers.buildBidPlayers());
    }

    private void distributeCardsToPlayers(List<ClassicalCard> cards) {

        int numberOfPlayers = players.getNumberOfPlayers();

        if (numberOfPlayers == 0) {
            throw new IllegalStateException("Cannot distribute cards if no player joined the game");
        }

        if (cards.size() % numberOfPlayers != 0) {
            throw new IllegalStateException("The cards cannot be equally distributed to all players");
        }

        // TODO make it configurable
        List<Integer> distributionConfiguration = List.of(3, 3, 2);

        CardDealer cardDealer = new CardDealer(cards, numberOfPlayers, distributionConfiguration);
        var distributedCards = cardDealer.dealCards();

        for (int i = 0 ; i < distributedCards.size() ; i++) {
            players.receiveHandForPlayer(i, distributedCards.get(i));
        }

    }

    public void placeBid(ContreeBid bid) {

        if (isPlayStep()) {
            throw new IllegalStateException(
                String.format("Cheater detected: %s ? A bid cannot be placed during PLAY step", bid.player())
            );
        }

        eventSender.sendPlacedBidEvent(dealId, bid);
        bids.placeBid(bid);

        if ( bids.bidsAreOver() ) {
            if ( bids.hasOnlyNoneBids() ) {
                manageEndOfDeal();
            }
            else {
                startPlay();
            }
        }

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
        score.computeScore(this);
        dealStep = DealStep.OVER;
        endOfStepEventSender.accept(dealId);
        eventSender.sendEndOfDealEvent(dealId);
    }

    private void startPlay() {

        trumpSuit   = bids.highestBid().orElseThrow().cardSuit();

        dealStep    = DealStep.PLAY;
        eventSender.sendBidStepEndedEvent(dealId);
        eventSender.sendPlayStepStartedEvent(dealId, trumpSuit);

        tricks.startTricks(this, players.buildTrickPlayers());
    }

    public void playerPlays(ContreePlayer player, ClassicalCard card) {

        if (!isPlayStep()) {
            throw new IllegalStateException(
                    String.format("Cheater detected : %s is trying to play a card on a deal not in PLAY step", player)
            );
        }

        tricks.playerPlays(player, card);

        if (tricks.isMaxNbOverTricksReached()) {
            manageEndOfDeal();
        }

    }

    public String getDealId() {
        return dealId;
    }

    public CardSuit getTrumpSuit() {
        return trumpSuit;
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

    public boolean hasOnlyNoneBids()  {
        return bids.hasOnlyNoneBids();
    }

    public boolean isCapot() {
        return tricks.isCapot();
    }

    public boolean isAnnouncedCapot() {
        return bids.isAnnouncedCapot();
    }

    public boolean isDoubleBidExists() {
        return bids.isDoubleBidExists();
    }

    public boolean isRedoubleBidExists() {
        return bids.isRedoubleBidExists();
    }

    public Optional<ContreeBid> getContractBid() {
        return bids.findDealContractBid();
    }

    public Optional<ContreeTeam> getAttackTeam() {
        return players.getCurrentDealAttackTeam();
    }

    public Optional<ContreeTeam> getDefenseTeam() {
        return players.getCurrentDealDefenseTeam();
    }

    public Map<Team, Set<ContreeCard>> wonCardsByTeam() {
        return tricks.wonCardsByTeam();
    }

    public Optional<ContreeTrick> lastTrick() {
        return tricks.lastTrick();
    }

    public int getTeamScore(Team t) {
        return score.getTeamScore(t);
    }

    ContreeGameEventSender getEventSender() {
        return eventSender;
    }

}
