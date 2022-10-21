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

    private final String dealId;

    private CardSuit trumpSuit;

    private final ContreeTricks contreeTricks;

    DealStep dealStep;

    private final ContreeGameEventSender eventSender;

    private final ContreeDealScore score;

    private final ContreeDealPlayers dealPlayers;

    private final ContreeDealBids dealBids;

    public Optional<ContreeTeam> getAttackTeam() {
        return dealPlayers.getCurrentDealAttackTeam();
    }

    public Optional<ContreeTeam> getDefenseTeam() {
        return dealPlayers.getCurrentDealDefenseTeam();
    }

    public ContreeDeal(String dealId, ContreeDealPlayers dealPlayers, ContreeGameEventSender eventSender) {

        this.dealId = dealId;
        dealStep = DealStep.NOT_STARTED;
        this.dealPlayers = dealPlayers;
        this.dealBids = new ContreeDealBids(dealPlayers.buildBidPlayers());
        this.eventSender = eventSender;

        this.contreeTricks = new ContreeTricks(this, dealPlayers.buildTrickPlayers(), new PlayableCardsFilter());
        score = new ContreeDealScore(this);

    }

    void startDeal() {
        eventSender.sendStartOfDealEvent(dealId);
        dealStep = DealStep.BID;
        eventSender.sendBidStepStartedEvent(dealId);
        CardSetShuffler shuffler = new CardSetShufflerImpl();
        List<ClassicalCard> cards = shuffler.shuffle(CardSet.GAME_32);
        distributeCardsToPlayers(cards);
        //this.dealBids = new ContreeDealBids( new ContreeBidPlayersImpl( dealPlayers.getDealPlayers(), this ) );
        dealBids.startBids();
    }

    private void distributeCardsToPlayers(List<ClassicalCard> cards) {

        int numberOfPlayers = dealPlayers.getNumberOfPlayers();

        if (numberOfPlayers == 0) {
            throw new IllegalStateException("Cannot distribute cards if no player joined the game");
        }

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
        return contreeTricks.teamDoingCapot();
    }

    public Map<Team, Set<ContreeCard>> wonCardsByTeam() {
        return contreeTricks.wonCardsByTeam();
    }

    public Optional<ContreeTrick> lastTrick() {
        return contreeTricks.lastTrick();
    }

    public boolean isCapot() {
        return contreeTricks.isCapot();
    }

    public Optional<ContreeBid> findDealContractBid() {
        return dealBids.findDealContractBid();
    }

    private void startPlay() {
        trumpSuit = dealBids.highestBid().orElseThrow().cardSuit();
        dealStep = DealStep.PLAY;
        eventSender.sendBidStepEndedEvent(dealId);
        eventSender.sendPlayStepStartedEvent(dealId, trumpSuit);
        contreeTricks.startTricks();
    }

    public void playerPlays(ContreePlayer player, ClassicalCard card) {

        if (!isPlayStep()) {
            throw new IllegalStateException(String.format("Cheater detected : %s is trying to play a card on a deal not in PLAY step", player));
        }

        contreeTricks.playerPlays(player, card);

        if (contreeTricks.isMaxNbOverTricksReached()) {
            manageEndOfDeal();
        }

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

    // TODO think about a better lifecycle
    public boolean isNotStarted() {
        return dealStep == DealStep.NOT_STARTED;
    }

    public String getDealId() {
        return dealId;
    }

    public CardSuit getTrumpSuit() {
        return trumpSuit;
    }

    ContreeGameEventSender getEventSender() {
        return eventSender;
    }

}
