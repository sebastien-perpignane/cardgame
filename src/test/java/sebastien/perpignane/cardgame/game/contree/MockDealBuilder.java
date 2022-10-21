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

public class MockDealBuilder {
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

    public MockDealBuilder withIsDouble(boolean isDouble) {
        when(deal.isDoubleBidExists()).thenReturn(isDouble); return this;
    }

    public MockDealBuilder withIsRedouble(boolean isRedouble) {
        when(deal.isRedoubleBidExists()).thenReturn(isRedouble); return this;
    }

    public MockDealBuilder withIsAnnouncedCapot(boolean isAnnouncedCapot) {
        when(deal.isAnnouncedCapot()).thenReturn(isAnnouncedCapot); return this;
    }

    public MockDealBuilder withTeamDoingCapot(ContreeTeam teamDoingCapot) {
        when(deal.teamDoingCapot()).thenReturn(Optional.ofNullable(teamDoingCapot)); return this;
    }

    public MockDealBuilder withDealContractBid(ContreeBid dealContractBid) {
        when(deal.findDealContractBid()).thenReturn(Optional.ofNullable(dealContractBid)); return this;
    }

    public MockDealBuilder withWinnerTeam(ContreeTeam team) {
        when(lastTrick.getWinnerTeam()).thenReturn(team); return this;
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