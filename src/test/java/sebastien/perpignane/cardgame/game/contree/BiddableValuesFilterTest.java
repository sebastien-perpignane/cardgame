package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static sebastien.perpignane.cardgame.game.contree.ContreeBidValue.*;

@DisplayName("Unit tests for the BiddableValuesFilter class")
class BiddableValuesFilterTest extends TestCasesManagingPlayers {

    private static Set<ContreeBidValue> allBidValuesExceptDoubleRedouble;

    private static ContreePlayer currentPlayer;

    private static ContreePlayer teamMate;

    private static ContreePlayer opponent;

    @BeforeAll
    static void globalSetUp() {
        initPlayers();

        currentPlayer = player1;
        teamMate = player3;
        opponent = player2;

        allBidValuesExceptDoubleRedouble =
                Arrays.stream( ContreeBidValue.values() )
                    .filter( Predicate.not( bidValue -> bidValue == DOUBLE || bidValue == REDOUBLE ) )
                    .collect( Collectors.toSet() );

    }

    private ContreeDealBids bids;

    private BiddableValuesFilter filter;

    private Set<ContreeBidValue> expectedAllowedBidValues;

    @BeforeEach
    void setUp() {

        bids = mock(ContreeDealBids.class);

        filter = new BiddableValuesFilter();

        expectedAllowedBidValues = null;

    }

    private void runTestWithCurrentPlayerAndCheckAssertions() {
        assertThat(expectedAllowedBidValues).describedAs("expectedAllowedBidValues cannot be null, each test must define its content").isNotNull();
        BiddableValuesFilter.BidFilterResult filterResult = filter.biddableValues(currentPlayer, bids);
        Map<ContreeBidValue, String> exclusionCauseByBidValue = filterResult.exclusionCauseByBidValue();
        Set<ContreeBidValue> allowedBidValues = filterResult.biddableValues();

        assertThat(allowedBidValues).isEqualTo(expectedAllowedBidValues);

        checkExclusionCausesAreComplete(allowedBidValues, exclusionCauseByBidValue);
    }

    private void checkExclusionCausesAreComplete(Set<ContreeBidValue> allowedBidValues, Map<ContreeBidValue, String> exclusionCauseByBidValue) {
        Arrays.stream(ContreeBidValue.values()).filter(Predicate.not(allowedBidValues::contains)).forEach( bv -> {
            String assertionErrorMessage = String.format("Exclusion cause not available for bid value %s", bv);
            assertThat(exclusionCauseByBidValue).as(assertionErrorMessage).containsKey(bv);
        });
    }

    @DisplayName("When no bid placed, any bid value except double and redouble is allowed")
    @Test
    void testNoBid() {

        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.noBidsExceptPass()).thenReturn(true);

        expectedAllowedBidValues = allBidValuesExceptDoubleRedouble;

        runTestWithCurrentPlayerAndCheckAssertions();


    }

    @DisplayName("When only PASS bid were placed, any bid value except double and redouble is allowed")
    @Test
    void testOnlyPassBids() {
        when(bids.highestBid()).thenReturn(Optional.of(
                new ContreeBid(teamMate, ContreeBidValue.PASS)
        ));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.noDoubleNorRedoubleBid()).thenReturn(true);
        when(bids.hasOnlyPassBids()).thenReturn(true);

        expectedAllowedBidValues = allBidValuesExceptDoubleRedouble;

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a EIGHTY bid was placed by an opponent and no double bid exists, any bid except eighty and redouble are allowed")
    @Test
    void testEightyBidByOpponent_noDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.EIGHTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.noDoubleNorRedoubleBid()).thenReturn(true);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = new HashSet<>(allBidValuesExceptDoubleRedouble);
        expectedAllowedBidValues.remove(ContreeBidValue.EIGHTY);
        expectedAllowedBidValues.add(ContreeBidValue.DOUBLE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a EIGHTY bid was placed by an opponent and the team mate doubled, only PASS bid is allowed")
    @Test
    void testEightyBidByOpponent_alreadyDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.EIGHTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.PASS);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a HUNDRED_TEN bid was placed by an opponent and no double bid exists, any bid except eighty and redouble are allowed")
    @Test
    void testHundredTenBidByOpponent_noDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.HUNDRED_TEN, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.noDoubleNorRedoubleBid()).thenReturn(true);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = new HashSet<>(allBidValuesExceptDoubleRedouble);

        List.of(EIGHTY, NINETY, HUNDRED, HUNDRED_TEN).forEach(expectedAllowedBidValues::remove);
        expectedAllowedBidValues.add(DOUBLE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a HUNDRED_TEN bid was placed by an opponent and the teeam mate doubled, only PASS bid is allowed")
    @Test
    void testHundredTenBidByOpponent_alreadyDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, EIGHTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(PASS);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a EIGHTY bid was placed by the team mate and the opponents did not double, the player can pass or bid higher")
    @Test
    void testEightyBidByMate_noDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, EIGHTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.noDoubleNorRedoubleBid()).thenReturn(true);
        when(bids.hasOnlyPassBids()).thenReturn(false);


        expectedAllowedBidValues = new HashSet<>(allBidValuesExceptDoubleRedouble);
        expectedAllowedBidValues.remove(ContreeBidValue.EIGHTY);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a EIGHTY bid was placed by the team mate and the opponents doubled, the player can only bid PASS or REDOUBLE")
    @Test
    void testEightyBidByMate_withDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, EIGHTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(REDOUBLE, PASS);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a HUNDRED_TEN bid was placed by the team mate and the opponents did not double, the player can pass or bid higher")
    @Test
    void testHundredTenBidByMate_noDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, HUNDRED_TEN, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.noDoubleNorRedoubleBid()).thenReturn(true);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = new HashSet<>(allBidValuesExceptDoubleRedouble);

        List.of(EIGHTY, NINETY, HUNDRED, HUNDRED_TEN).forEach(expectedAllowedBidValues::remove);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a HUNDRED_TEN bid is placed by the teammate and the opponents double, the player can only bid PASS or REDOUBLE")
    @Test
    void testHundredTenBidByMate_withDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.EIGHTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.REDOUBLE, ContreeBidValue.PASS);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When an opponent bids CAPOT and the team mate does not double, the current player can double or not bid")
    @Test
    void testCapotByOpponent_noDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.CAPOT, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(true);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.DOUBLE, ContreeBidValue.PASS);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When an opponent bids CAPOT and the team mate double, the current player can only bid PASS")
    @Test
    void testCapotByOpponent_withDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.CAPOT, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(true);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.PASS);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When the teammate bids CAPOT and the opponents do not double, the current player can only bid PASS")
    @Test
    void testCapotByTeammate_noDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.CAPOT, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(true);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.PASS);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When the teammate bids CAPOT and the opponents do not double, the current player can redouble or bid PASS")
    @Test
    void testCapotByTeammate_withDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.CAPOT, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(true);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.PASS, ContreeBidValue.REDOUBLE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When an opponent bids 160 and the teammate does not double, the current player can bid PASS, double or bid CAPOT")
    @Test
    void testHundredSixtyByOpponent_noDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.HUNDRED_SIXTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.noDoubleNorRedoubleBid()).thenReturn(true);
        when(bids.isAnnouncedCapot()).thenReturn(false);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.DOUBLE, ContreeBidValue.PASS, ContreeBidValue.CAPOT);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When an opponent bids 160 and the teammate double, the current player can only bid PASS")
    @Test
    void testHundredSixtyByOpponent_withDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.HUNDRED_SIXTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(false);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.PASS);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When the teammate bids 160 and the opponent does not double, the current player can bid PASS or CAPOT")
    @Test
    void testHundredSixtyByTeammate_noDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.HUNDRED_SIXTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.noDoubleNorRedoubleBid()).thenReturn(true);
        when(bids.isAnnouncedCapot()).thenReturn(false);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.PASS, ContreeBidValue.CAPOT);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When the teammate bids 160 and the opponents double, the current player can bid PASS or redouble")
    @Test
    void testHundredSixtyByTeammate_withDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.HUNDRED_SIXTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(false);
        when(bids.hasOnlyPassBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.PASS, ContreeBidValue.REDOUBLE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

}