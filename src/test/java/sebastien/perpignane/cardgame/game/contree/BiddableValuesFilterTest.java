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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Unit tests for the BiddableValuesFilter class")
class BiddableValuesFilterTest extends TestCasesManagingPlayers {

    private static Set<ContreeBidValue> allValuesExceptDoubleRedouble;

    private static ContreePlayer currentPlayer;

    private static ContreePlayer teamMate;

    private static ContreePlayer opponent;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();

        currentPlayer = player1;
        teamMate = player3;
        opponent = player2;

        allValuesExceptDoubleRedouble =
                Arrays.stream( ContreeBidValue.values() )
                    .filter( Predicate.not( bv -> bv == ContreeBidValue.DOUBLE || bv == ContreeBidValue.REDOUBLE ) )
                    .collect( Collectors.toSet() );

    }

    private ContreeDealBids bids;

    private BiddableValuesFilter filter;

    private Set<ContreeBidValue> expectedAllowedBidValues;

    @BeforeEach
    public void setUp() {

        bids = mock(ContreeDealBids.class);

        filter = new BiddableValuesFilter();

    }

    private void runTestWithCurrentPlayerAndCheckAssertions() {
        BiddableValuesFilter.BidFilterResult filterResult = filter.biddableValues(currentPlayer, bids);
        Map<ContreeBidValue, String> exclusionCauseByBidValue = filterResult.exclusionCauseByBidValue();
        Set<ContreeBidValue> allowedBidValues = filterResult.biddableValues();

        assertEquals(
                expectedAllowedBidValues, allowedBidValues
        );
        checkExclusionCausesAreComplete(allowedBidValues, exclusionCauseByBidValue);
    }

    private void checkExclusionCausesAreComplete(Set<ContreeBidValue> allowedBidValues, Map<ContreeBidValue, String> exclusionCauseByBidValue) {
        Arrays.stream(ContreeBidValue.values()).filter(Predicate.not(allowedBidValues::contains)).forEach( bv -> {
            String assertionErrorMessage = String.format("Exclusion cause not available for bid value %s", bv);
            assertTrue( exclusionCauseByBidValue.containsKey(bv), assertionErrorMessage );
        });
    }

    @DisplayName("When no bid placed, any bid value except double and redouble is allowed")
    @Test
    public void testNoBid() {

        when(bids.highestBid()).thenReturn(Optional.empty());
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = allValuesExceptDoubleRedouble;

        runTestWithCurrentPlayerAndCheckAssertions();


    }

    @DisplayName("When only NONE bid were placed, any bid value except double and redouble is allowed")
    @Test
    public void testOnlyNoneBids() {
        when(bids.highestBid()).thenReturn(Optional.of(
                new ContreeBid(teamMate, ContreeBidValue.NONE)
        ));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(true);

        expectedAllowedBidValues = allValuesExceptDoubleRedouble;

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a EIGHTY bid was placed by an opponent and no double bid exists, any bid except eighty and redouble are allowed")
    @Test
    public void testEightyBidByOpponent_noDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.EIGHTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = new HashSet<>(allValuesExceptDoubleRedouble);
        expectedAllowedBidValues.remove(ContreeBidValue.EIGHTY);
        expectedAllowedBidValues.add(ContreeBidValue.DOUBLE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a EIGHTY bid was placed by an opponent and the team mate doubled, only NONE bid is allowed")
    @Test
    public void testEightyBidByOpponent_alreadyDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.EIGHTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.NONE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a HUNDRED_TEN bid was placed by an opponent and no double bid exists, any bid except eighty and redouble are allowed")
    @Test
    public void testHundredTenBidByOpponent_noDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.HUNDRED_TEN, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = new HashSet<>(allValuesExceptDoubleRedouble);
        expectedAllowedBidValues.remove(ContreeBidValue.EIGHTY);
        expectedAllowedBidValues.remove(ContreeBidValue.NINETY);
        expectedAllowedBidValues.remove(ContreeBidValue.HUNDRED);
        expectedAllowedBidValues.remove(ContreeBidValue.HUNDRED_TEN);
        expectedAllowedBidValues.add(ContreeBidValue.DOUBLE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a HUNDRED_TEN bid was placed by an opponent and the teeam mate doubled, only NONE bid is allowed")
    @Test
    public void testHundredTenBidByOpponent_alreadyDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.EIGHTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.NONE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a EIGHTY bid was placed by the team mate and the opponents did not double, the player can pass or bid higher")
    @Test
    public void testEightyBidByMate_noDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.EIGHTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = new HashSet<>(allValuesExceptDoubleRedouble);
        expectedAllowedBidValues.remove(ContreeBidValue.EIGHTY);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a EIGHTY bid was placed by the team mate and the opponents doubled, the player can only bid NONE or REDOUBLE")
    @Test
    public void testEightyBidByMate_withDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.EIGHTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.REDOUBLE, ContreeBidValue.NONE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a HUNDRED_TEN bid was placed by the team mate and the opponents did not double, the player can pass or bid higher")
    @Test
    public void testHundredTenBidByMate_noDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.HUNDRED_TEN, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = new HashSet<>(allValuesExceptDoubleRedouble);
        expectedAllowedBidValues.remove(ContreeBidValue.EIGHTY);
        expectedAllowedBidValues.remove(ContreeBidValue.NINETY);
        expectedAllowedBidValues.remove(ContreeBidValue.HUNDRED);
        expectedAllowedBidValues.remove(ContreeBidValue.HUNDRED_TEN);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When a HUNDRED_TEN bid is placed by the teammate and the opponents double, the player can only bid NONE or REDOUBLE")
    @Test
    public void testHundredTenBidByMate_withDouble() {

        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.EIGHTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.REDOUBLE, ContreeBidValue.NONE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When an opponent bids CAPOT and the team mate does not double, the current player can double or not bid")
    @Test
    public void testCapotByOpponent_noDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.CAPOT, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(true);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.DOUBLE, ContreeBidValue.NONE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When an opponent bids CAPOT and the team mate double, the current player can only bid NONE")
    @Test
    public void testCapotByOpponent_withDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.CAPOT, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(true);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.NONE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When the teammate bids CAPOT and the opponents do not double, the current player can only bid NONE")
    @Test
    public void testCapotByTeammate_noDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.CAPOT, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(true);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.NONE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When the teammate bids CAPOT and the opponents do not double, the current player can redouble or bid NONE")
    @Test
    public void testCapotByTeammate_withDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.CAPOT, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(true);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.NONE, ContreeBidValue.REDOUBLE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When an opponent bids 160 and the teammate does not double, the current player can bid NONE, double or bid CAPOT")
    @Test
    public void testHundredSixtyByOpponent_noDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.HUNDRED_SIXTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.DOUBLE, ContreeBidValue.NONE, ContreeBidValue.CAPOT);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When an opponent bids 160 and the teammate double, the current player can only bid NONE")
    @Test
    public void testHundredSixtyByOpponent_withDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(opponent, ContreeBidValue.HUNDRED_SIXTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.NONE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When the teammate bids 160 and the opponent does not double, the current player can bid NONE or CAPOT")
    @Test
    public void testHundredSixtyByTeammate_noDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.HUNDRED_SIXTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(false);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.NONE, ContreeBidValue.CAPOT);

        runTestWithCurrentPlayerAndCheckAssertions();

    }

    @DisplayName("When the teammate bids 160 and the opponents double, the current player can bid NONE or redouble")
    @Test
    public void testHundredSixtyByTeammate_withDouble() {
        when(bids.highestBid()).thenReturn(Optional.of(new ContreeBid(teamMate, ContreeBidValue.HUNDRED_SIXTY, CardSuit.HEARTS)));
        when(bids.isDoubleBidExists()).thenReturn(true);
        when(bids.isRedoubleBidExists()).thenReturn(false);
        when(bids.isAnnouncedCapot()).thenReturn(false);
        when(bids.hasOnlyNoneBids()).thenReturn(false);

        expectedAllowedBidValues = Set.of(ContreeBidValue.NONE, ContreeBidValue.REDOUBLE);

        runTestWithCurrentPlayerAndCheckAssertions();

    }


    
}