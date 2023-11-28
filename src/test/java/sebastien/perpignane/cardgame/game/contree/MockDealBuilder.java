package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Builder for mocked deal.
 *  * methods returning boolean default to false
 *  * methods returning objects default to null
 *  * methods returning collection default to empty collection
 */
class MockDealBuilder {
    private final ContreeDeal deal;
    private final ContreeTrick lastTrick;

    private MockDealBuilder() {
        deal = mock(ContreeDeal.class);
        lastTrick = mock(ContreeTrick.class);
    }

    static MockDealBuilder builder() {
        return new MockDealBuilder();
    }

    public MockDealBuilder withIsCapot(boolean isCapot) {
        when(deal.isCapot()).thenReturn(isCapot); return this;
    }

    public MockDealBuilder withIsOver(boolean isOver) {
        when(deal.isOver()).thenReturn(isOver); return this;
    }

    public MockDealBuilder withScore(Map<Team, Integer> scoreByTeam) {
        when(deal.getTeamScore(ContreeTeam.TEAM1)).thenReturn(scoreByTeam.get(ContreeTeam.TEAM1));
        when(deal.getTeamScore(ContreeTeam.TEAM2)).thenReturn(scoreByTeam.get(ContreeTeam.TEAM2));
        return this;
    }

    public MockDealBuilder withIsDouble(boolean isDouble) {
        when(deal.isDoubleBidExists()).thenReturn(isDouble); return this;
    }

    public MockDealBuilder withIsCapotMadeByAttackTeam(boolean isCapotMadeByAttackTeam) {
        when(deal.isCapotMadeByAttackTeam()).thenReturn(isCapotMadeByAttackTeam);return this;
    }

    public MockDealBuilder withIsRedouble(boolean isRedouble) {
        when(deal.isRedoubleBidExists()).thenReturn(isRedouble); return this;
    }

    public MockDealBuilder withIsAnnouncedCapot(boolean isAnnouncedCapot) {
        when(deal.isAnnouncedCapot()).thenReturn(isAnnouncedCapot); return this;
    }

    public MockDealBuilder withHasOnlyPassBids(boolean hasOnlyPassBids) {
        when(deal.hasOnlyPassBids()).thenReturn(hasOnlyPassBids); return this;
    }

    public MockDealBuilder withDealContractBid(ContreeBid dealContractBid) {
        when(deal.getContractBid()).thenReturn(Optional.ofNullable(dealContractBid)); return this;
    }

    public MockDealBuilder withLastTrickWinnerTeam(ContreeTeam team) {
        when(lastTrick.getWinnerTeam()).thenReturn(Optional.of(team)); return this;
    }

    public MockDealBuilder withAttackTeam(ContreeTeam team) {
        when(deal.getAttackTeam()).thenReturn(Optional.of(team)); return this;
    }

    public MockDealBuilder withDefenseTeam(ContreeTeam team) {
        when(deal.getDefenseTeam()).thenReturn(Optional.of(team)); return this;
    }

    public MockDealBuilder withCardsByTeam(Map<Team, Set<ContreeCard>> cardsByTeam) {
        when(deal.wonCardsByTeam()).thenReturn(cardsByTeam); return this;
    }

    public MockDealBuilder withTrumpSuit(CardSuit trumpSuit) {
        when(deal.getTrumpSuit()).thenReturn(trumpSuit); return this;
    }

    public MockDealBuilder withGameEventSender(ContreeGameEventSender gameEventSender) {
        when(deal.getEventSender()).thenReturn(gameEventSender); return this;
    }

    public MockDealBuilder withMockedGameEventSender() {
        ContreeGameEventSender eventSender = mock(ContreeGameEventSender.class);
        when(deal.getEventSender()).thenReturn(eventSender); return this;
    }

    public ContreeDeal build() {
        when(deal.lastTrick()).thenReturn(Optional.of(lastTrick));
        return deal;
    }
}