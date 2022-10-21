package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.buildPlayers;

public class DealScoreCalculatorTest {

    /*static class MockDealBuilder {

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
            when(deal.getCurrentDealAttackTeam()).thenReturn(Optional.of(team)); return this;
        }

        public MockDealBuilder withDefenseTeam(ContreeTeam team) {
            when(deal.getCurrentDealDefenseTeam()).thenReturn(Optional.of(team)); return this;
        }

        public MockDealBuilder withCardsByTeam(Map<Team, Set<ContreeCard>> cardsByTeam) {
            when(deal.wonCardsByTeam()).thenReturn(cardsByTeam); return this;
        }

        public ContreeDeal build() {
            when(deal.lastTrick()).thenReturn(Optional.of(lastTrick));
            return deal;
        }

    }*/

    @Test
    public void testExceptionIfNoContract() {

        ContreeDeal deal = MockDealBuilder.builder().build();

        DealScoreCalculator dealScoreCalculator = new DealScoreCalculator(deal);
        assertThrows(
                IllegalStateException.class,
                dealScoreCalculator::computeDealScores
        );

    }

    @Test
    public void testStandardContractIsReached() {
        var dealPlayers = buildPlayers();
        var contractPlayer = dealPlayers.get(0);

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(false)
                .withIsCapot(false)
                .withIsDouble(false)
                .withIsRedouble(false)
                .withTeamDoingCapot(null)
                .withDealContractBid(
                    new ContreeBid(contractPlayer, ContreeBidValue.EIGHTY, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(
                        Map.of(
                                ContreeTeam.TEAM1, ContreeCard.of(
                                        CardSuit.HEARTS, Set.of(ClassicalCard.JACK_HEART, ClassicalCard.NINE_HEART, ClassicalCard.ACE_HEART, ClassicalCard.ACE_SPADE, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_DIAMOND, ClassicalCard.TEN_SPADE)
                                ),
                                ContreeTeam.TEAM2, ContreeCard.of(
                                        CardSuit.HEARTS, Set.of(ClassicalCard.TEN_DIAMOND)
                                )
                        )
                )
                .build();

        DealScoreCalculator dealScoreCalculator = new DealScoreCalculator(deal);

        var scoreByTeam = dealScoreCalculator.computeDealScores();
        // 88 + dix de der, rounded
        assertEquals(100, scoreByTeam.get(contractPlayer.getTeam().orElseThrow()));
        assertEquals(10, scoreByTeam.get(ContreeTeam.TEAM2));
    }

    @Test
    public void testContractIsReached_doubled() {

        var dealPlayers = buildPlayers();
        var contractPlayer = dealPlayers.get(0);

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(false)
                .withIsCapot(false)
                .withIsDouble(true)
                .withIsRedouble(false)
                .withTeamDoingCapot(null)
                .withDealContractBid(
                    new ContreeBid(contractPlayer, ContreeBidValue.EIGHTY, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(
                        Map.of(
                                ContreeTeam.TEAM1, ContreeCard.of(
                                        CardSuit.HEARTS, Set.of(ClassicalCard.JACK_HEART, ClassicalCard.NINE_HEART, ClassicalCard.ACE_HEART, ClassicalCard.ACE_SPADE, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_DIAMOND, ClassicalCard.TEN_SPADE)
                                ),
                                ContreeTeam.TEAM2, ContreeCard.of(
                                        CardSuit.HEARTS, Set.of(ClassicalCard.TEN_DIAMOND)
                                )
                        )
                )
                .build();

        DealScoreCalculator dealScoreCalculator = new DealScoreCalculator(deal);

        var scoreByTeam = dealScoreCalculator.computeDealScores();

        assertEquals(320, scoreByTeam.get(contractPlayer.getTeam().orElseThrow()));
        assertEquals(0, scoreByTeam.get(ContreeTeam.TEAM2));
    }

    @Test
    public void testContractIsReached_redoubled() {

        var dealPlayers = buildPlayers();
        var contractPlayer = dealPlayers.get(0);

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(false)
                .withIsCapot(false)
                .withIsDouble(true)
                .withIsRedouble(true)
                .withTeamDoingCapot(null)
                .withDealContractBid(
                    new ContreeBid(contractPlayer, ContreeBidValue.EIGHTY, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(
                        Map.of(
                                ContreeTeam.TEAM1, ContreeCard.of(
                                        CardSuit.HEARTS, Set.of(ClassicalCard.JACK_HEART, ClassicalCard.NINE_HEART, ClassicalCard.ACE_HEART, ClassicalCard.ACE_SPADE, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_DIAMOND, ClassicalCard.TEN_SPADE)
                                ),
                                ContreeTeam.TEAM2, ContreeCard.of(
                                        CardSuit.HEARTS, Set.of(ClassicalCard.TEN_DIAMOND)
                                )
                        )
                )
                .build();

        DealScoreCalculator dealScoreCalculator = new DealScoreCalculator(deal);

        var scoreByTeam = dealScoreCalculator.computeDealScores();
        // 88 + dix de der, rounded
        assertEquals(640, scoreByTeam.get(contractPlayer.getTeam().orElseThrow()));
        assertEquals(0, scoreByTeam.get(ContreeTeam.TEAM2));
    }

    @Test
    public void testStandardContractIsNotReached() {

        var dealPlayers = buildPlayers();
        var contractPlayer = dealPlayers.get(0);

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(false)
                .withIsCapot(false)
                .withIsDouble(false)
                .withIsRedouble(false)
                .withTeamDoingCapot(null)
                .withDealContractBid(
                    new ContreeBid(contractPlayer, ContreeBidValue.HUNDRED_FORTY, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(
                        Map.of(
                                ContreeTeam.TEAM1, ContreeCard.of(
                                        CardSuit.HEARTS, Set.of(ClassicalCard.JACK_HEART, ClassicalCard.NINE_HEART, ClassicalCard.ACE_HEART, ClassicalCard.ACE_SPADE, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_DIAMOND, ClassicalCard.TEN_SPADE)
                                ),
                                ContreeTeam.TEAM2, ContreeCard.of(
                                        CardSuit.HEARTS, Set.of(ClassicalCard.TEN_DIAMOND)
                                )
                        )
                )
                .build();

        DealScoreCalculator dealScoreCalculator = new DealScoreCalculator(deal);

        var scoreByTeam = dealScoreCalculator.computeDealScores();

        assertEquals(0, scoreByTeam.get(contractPlayer.getTeam().orElseThrow()));
        assertEquals(160, scoreByTeam.get(ContreeTeam.TEAM2));
    }

    @Test
    public void testIsAccouncedCapotReached() {

        var dealPlayers = buildPlayers();

        var contractPlayer = dealPlayers.get(0);
        var firstOpponent = dealPlayers.get(1);

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(true)
                .withIsCapot(true)
                .withIsDouble(false)
                .withIsRedouble(false)
                .withTeamDoingCapot(ContreeTeam.TEAM1)
                .withDealContractBid(
                    new ContreeBid(contractPlayer, ContreeBidValue.CAPOT, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(
                        Map.of(
                                contractPlayer.getTeam().orElseThrow(), ContreeCard.of(
                                        CardSuit.HEARTS, new HashSet<>(CardSet.GAME_32.getGameCards())
                                ),
                                firstOpponent.getTeam().orElseThrow(), Collections.emptySet()
                        )
                )
                .build();



        // To make sure that buildPlayers() always return different teams for player 1 and player 2
        assertNotEquals(contractPlayer.getTeam(), firstOpponent.getTeam());

        DealScoreCalculator dealScoreCalculator = new DealScoreCalculator(deal);

        var scoreByTeam = dealScoreCalculator.computeDealScores();

        // 88 + dix de der, rounded
        assertEquals(500, scoreByTeam.get(contractPlayer.getTeam().orElseThrow()));
        assertEquals(0, scoreByTeam.get(ContreeTeam.TEAM2));
    }

    private Collection<ContreeCard> buildContreeCards(CardSuit trump, Set<ClassicalCard> cards) {
        return ContreeCard.of(trump, cards);
    }

    @DisplayName("Count points for standard and trump cards")
    @Test
    public void testCountStandardAndTrumpCards() {

        Collection<ContreeCard> dealCards = buildContreeCards(
                CardSuit.HEARTS,
                Set.of(
                        ClassicalCard.ACE_SPADE,
                        ClassicalCard.TEN_SPADE,
                        ClassicalCard.EIGHT_SPADE,
                        ClassicalCard.SEVEN_DIAMOND
                )
        );

        ContreeDeal deal = mock(ContreeDeal.class);

        DealScoreCalculator dealPointComputer = new DealScoreCalculator(deal);
        var dealPoints = dealPointComputer.computeCardPoints(dealCards);

        assertEquals(21, dealPoints);

    }

    @DisplayName("Count points for trump cards only")
    @Test
    public void testCountTrumpCards() {

        Collection<ContreeCard> dealCards = buildContreeCards(
                CardSuit.DIAMONDS,
                Set.of(
                        ClassicalCard.ACE_DIAMOND,
                        ClassicalCard.JACK_DIAMOND,
                        ClassicalCard.NINE_DIAMOND,
                        ClassicalCard.SEVEN_DIAMOND
                )
        );

        ContreeDeal deal = mock(ContreeDeal.class);

        DealScoreCalculator dealPointComputer = new DealScoreCalculator(deal);
        var dealPoints = dealPointComputer.computeCardPoints(dealCards);

        assertEquals(45, dealPoints);

    }

    @DisplayName("A full card set contains 152 points (162 when we count the 10 bonus points for the last trick)")
    @Test
    public void testCountPointsForAllCards() {

        Collection<ContreeCard> dealCards = buildContreeCards(
                CardSuit.CLUBS,
                CardSet.GAME_32.getGameCards()
        );

        ContreeDeal deal = mock(ContreeDeal.class);

        DealScoreCalculator dealPointComputer = new DealScoreCalculator(deal);
        var dealPoints = dealPointComputer.computeCardPoints(dealCards);

        assertEquals(152, dealPoints);

    }

}
