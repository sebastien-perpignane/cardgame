package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/*
 Responsibilities are :
    * compute card score *
    * compute full deal score, that includes bonuses depending on capot announce, dix de der, double, redouble *
    * throw exception if the deal on which score is calculated is not consistent
      (no contract, no attack team, no defense team, bad total card score etc.) *
 */
public class DealScoreCalculatorTest extends TestCasesManagingPlayers {

    private static Map<CardSuit, CardSuitTypicalTricks>  typicalTricksBySuit;

    private DealScoreCalculator dealScoreCalculator;

    record CardSuitTypicalTricks(CardSuit cardSuit,
             List<ClassicalCard> standardStrongTrick_27points, // gives 27
             List<ClassicalCard> standardWeakTrick_3points, // gives 3
             List<ClassicalCard> trumpStrongTrick_55points, // gives 55
             List<ClassicalCard> trumpWeakTrick_7points // gives 7
    ) {}

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();

        typicalTricksBySuit = new HashMap<>();

        typicalTricksBySuit.put(
            CardSuit.HEARTS,
            new CardSuitTypicalTricks(
                    CardSuit.HEARTS,
                    List.of(
                        ClassicalCard.ACE_HEART,
                        ClassicalCard.TEN_HEART,
                        ClassicalCard.KING_HEART,
                        ClassicalCard.QUEEN_HEART
                    ),
                    List.of(
                        ClassicalCard.JACK_HEART,
                        ClassicalCard.NINE_HEART,
                        ClassicalCard.EIGHT_HEART,
                        ClassicalCard.SEVEN_HEART
                    ),
                    List.of(
                        ClassicalCard.JACK_HEART,
                        ClassicalCard.NINE_HEART,
                        ClassicalCard.ACE_HEART,
                        ClassicalCard.TEN_HEART
                            
                    ),
                    List.of(
                        ClassicalCard.KING_HEART,
                        ClassicalCard.QUEEN_HEART,
                        ClassicalCard.EIGHT_HEART,
                        ClassicalCard.SEVEN_HEART
                    )
            )
        );
        
        typicalTricksBySuit.put(
            CardSuit.DIAMONDS,
            new CardSuitTypicalTricks(
                CardSuit.DIAMONDS,
                List.of(
                    ClassicalCard.ACE_DIAMOND,
                    ClassicalCard.TEN_DIAMOND,
                    ClassicalCard.KING_DIAMOND,
                    ClassicalCard.QUEEN_DIAMOND
                ),
                List.of(
                    ClassicalCard.JACK_DIAMOND,
                    ClassicalCard.NINE_DIAMOND,
                    ClassicalCard.EIGHT_DIAMOND,
                    ClassicalCard.SEVEN_DIAMOND
                ),
                List.of(
                    ClassicalCard.JACK_DIAMOND,
                    ClassicalCard.NINE_DIAMOND,
                    ClassicalCard.ACE_DIAMOND,
                    ClassicalCard.TEN_DIAMOND

                ),
                List.of(
                    ClassicalCard.KING_DIAMOND,
                    ClassicalCard.QUEEN_DIAMOND,
                    ClassicalCard.EIGHT_DIAMOND,
                    ClassicalCard.SEVEN_DIAMOND
                )
            )
        );

        typicalTricksBySuit.put(
                CardSuit.SPADES,
                new CardSuitTypicalTricks(
                        CardSuit.SPADES,
                        List.of(
                                ClassicalCard.ACE_SPADE,
                                ClassicalCard.TEN_SPADE,
                                ClassicalCard.KING_SPADE,
                                ClassicalCard.QUEEN_SPADE
                        ),
                        List.of(
                                ClassicalCard.JACK_SPADE,
                                ClassicalCard.NINE_SPADE,
                                ClassicalCard.EIGHT_SPADE,
                                ClassicalCard.SEVEN_SPADE
                        ),
                        List.of(
                                ClassicalCard.JACK_SPADE,
                                ClassicalCard.NINE_SPADE,
                                ClassicalCard.ACE_SPADE,
                                ClassicalCard.TEN_SPADE

                        ),
                        List.of(
                                ClassicalCard.KING_SPADE,
                                ClassicalCard.QUEEN_SPADE,
                                ClassicalCard.EIGHT_SPADE,
                                ClassicalCard.SEVEN_SPADE
                        )
                )
        );

        typicalTricksBySuit.put(
                CardSuit.CLUBS,
                new CardSuitTypicalTricks(
                        CardSuit.CLUBS,
                        List.of(
                                ClassicalCard.ACE_CLUB,
                                ClassicalCard.TEN_CLUB,
                                ClassicalCard.KING_CLUB,
                                ClassicalCard.QUEEN_CLUB
                        ),
                        List.of(
                                ClassicalCard.JACK_CLUB,
                                ClassicalCard.NINE_CLUB,
                                ClassicalCard.EIGHT_CLUB,
                                ClassicalCard.SEVEN_CLUB
                        ),
                        List.of(
                                ClassicalCard.JACK_CLUB,
                                ClassicalCard.NINE_CLUB,
                                ClassicalCard.ACE_CLUB,
                                ClassicalCard.TEN_CLUB

                        ),
                        List.of(
                                ClassicalCard.KING_CLUB,
                                ClassicalCard.QUEEN_CLUB,
                                ClassicalCard.EIGHT_CLUB,
                                ClassicalCard.SEVEN_CLUB
                        )
                )
        );

    }

    @BeforeEach
    public void setUp() {
        dealScoreCalculator = new DealScoreCalculator();
    }

    @DisplayName("Score cannot be calculated on a deal with no contract (no valued bids)")
    @Test
    public void testExceptionIfNoContract() {

        ContreeDeal deal = MockDealBuilder.builder().build();
        assertThrows(
                RuntimeException.class,
                () -> dealScoreCalculator.computeDealScores(deal)
        );

    }

    @DisplayName("Score cannot be calculated on a deal with no contract (no valued bids)")
    @Test
    public void testExceptionIfNoAttackTeam() {

        Map<Team, Set<ContreeCard>> cardsByTeam = buildCardsByTeamWithTeam1_89AndTeam2_63_trump_is_heart();

        ContreePlayer contractPlayer = player1;

        ContreeDeal deal = MockDealBuilder.builder()
                .withDealContractBid(
                        new ContreeBid(contractPlayer, ContreeBidValue.CAPOT, CardSuit.HEARTS)
                )
                //.withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withLastTrickWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(cardsByTeam)
                .build();

        assertThrows(
                RuntimeException.class,
                () -> dealScoreCalculator.computeDealScores(deal)
        );

    }

    private Map<Team, Set<ContreeCard>> buildCardsByTeamWithTeam1_89AndTeam2_63_trump_is_heart() {
        Set<ClassicalCard> team1Cards = new HashSet<>();
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.HEARTS).trumpStrongTrick_55points);
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.HEARTS).trumpWeakTrick_7points); // 62
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.SPADES).standardStrongTrick_27points); // 89


        Set<ClassicalCard> team2Cards = new HashSet<>();

        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.CLUBS).standardStrongTrick_27points);
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.SPADES).standardWeakTrick_3points); // 30
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.CLUBS).standardWeakTrick_3points); // 33
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.DIAMONDS).standardStrongTrick_27points); // 60
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.DIAMONDS).standardWeakTrick_3points); // 63

        return Map.of(
                ContreeTeam.TEAM1, ContreeCard.of(CardSuit.HEARTS, team1Cards),
                ContreeTeam.TEAM2, ContreeCard.of(CardSuit.HEARTS, team2Cards)
        );
    }

    @DisplayName("Compute score when attack team reached the contract, without double or redouble or capot")
    @Test
    public void testStandardContractIsReached() {
        var contractPlayer = player1;

        var cardsByTeam = buildCardsByTeamWithTeam1_89AndTeam2_63_trump_is_heart();

        ContreeDeal deal = MockDealBuilder.builder()
                .withDealContractBid(
                    new ContreeBid(contractPlayer, ContreeBidValue.EIGHTY, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withLastTrickWinnerTeam(ContreeTeam.TEAM2)
                .withCardsByTeam(cardsByTeam)
                .build();

        var scoreByTeam = dealScoreCalculator.computeDealScores(deal);
        // 89
        assertEquals(90, scoreByTeam.get(contractPlayer.getTeam().orElseThrow()));
        // 63 + dix de der, rounded
        assertEquals(70, scoreByTeam.get(ContreeTeam.TEAM2));
    }

    @DisplayName("Compute score when attack team reached the contract, with double but no redouble or capot")
    @Test
    public void testContractIsReached_doubled() {
        var contractPlayer = player1;

        var cardsByTeam = buildCardsByTeamWithTeam1_89AndTeam2_63_trump_is_heart();

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(false)
                .withIsCapot(false)
                .withIsDouble(true)
                .withIsRedouble(false)
                .withDealContractBid(
                    new ContreeBid(contractPlayer, ContreeBidValue.EIGHTY, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withLastTrickWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(cardsByTeam)
                .build();

        var scoreByTeam = dealScoreCalculator.computeDealScores(deal);

        assertEquals(320, scoreByTeam.get(contractPlayer.getTeam().orElseThrow()));
        assertEquals(0, scoreByTeam.get(ContreeTeam.TEAM2));
    }

    @DisplayName("Compute score when attack team reached the contract with double and redouble, but no capot announced")
    @Test
    public void testContractIsReached_redoubled() {
        var contractPlayer = player1;

        var cardsByTeam = buildCardsByTeamWithTeam1_89AndTeam2_63_trump_is_heart();

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(false)
                .withIsCapot(false)
                .withIsDouble(true)
                .withIsRedouble(true)
                .withDealContractBid(
                    new ContreeBid(contractPlayer, ContreeBidValue.EIGHTY, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withLastTrickWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(cardsByTeam)
                .build();

        var scoreByTeam = dealScoreCalculator.computeDealScores(deal);
        // 88 + dix de der, rounded
        assertEquals(640, scoreByTeam.get(contractPlayer.getTeam().orElseThrow()));
        assertEquals(0, scoreByTeam.get(ContreeTeam.TEAM2));
    }

    @DisplayName("Compute score when attack team failed to reach the contract")
    @Test
    public void testStandardContractIsNotReached() {

        var contractPlayer = player1;

        Map<Team, Set<ContreeCard>> cardsByTeam = buildCardsByTeamWithTeam1_89AndTeam2_63_trump_is_heart();

        CardSuit dealTrumpSuit = CardSuit.HEARTS;

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(false)
                .withIsCapot(false)
                .withIsDouble(false)
                .withIsRedouble(false)
                .withDealContractBid(
                    new ContreeBid(contractPlayer, ContreeBidValue.HUNDRED_FORTY, dealTrumpSuit)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withLastTrickWinnerTeam(ContreeTeam.TEAM2)
                .withCardsByTeam(cardsByTeam)
                .build();

        var scoreByTeam = dealScoreCalculator.computeDealScores(deal);

        assertEquals(0, scoreByTeam.get(contractPlayer.getTeam().orElseThrow()));
        assertEquals(160, scoreByTeam.get(ContreeTeam.TEAM2));
    }

    @DisplayName("Compute score when attack team announces capot and reach the contract, without double nor redouble")
    @Test
    public void testIsAnnouncedCapotReached() {

        var contractPlayer = player1;
        var firstOpponent = player2;

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(true)
                .withIsCapot(true)
                .withIsDouble(false)
                .withIsRedouble(false)
                .withDealContractBid(
                    new ContreeBid(contractPlayer, ContreeBidValue.CAPOT, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withLastTrickWinnerTeam(ContreeTeam.TEAM1)
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

        var scoreByTeam = dealScoreCalculator.computeDealScores(deal);

        assertEquals(500, scoreByTeam.get(contractPlayer.getTeam().orElseThrow()));
        assertEquals(0, scoreByTeam.get(ContreeTeam.TEAM2));
    }

    @DisplayName("Compute score when attack team announces capot and reach the contract, without double nor redouble")
    @Test
    public void testAnnouncedCapotNotReached() {

        var contractPlayer = player1;
        var firstOpponent = player2;

        var cardsByTeam = buildCardsByTeamWithTeam1_89AndTeam2_63_trump_is_heart();

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(true)
                .withIsCapot(true)
                .withIsDouble(false)
                .withIsRedouble(false)
                .withDealContractBid(
                        new ContreeBid(contractPlayer, ContreeBidValue.CAPOT, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withLastTrickWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(cardsByTeam)
                .build();



        // To make sure that buildPlayers() always return different teams for player 1 and player 2
        assertNotEquals(contractPlayer.getTeam(), firstOpponent.getTeam());

        var scoreByTeam = dealScoreCalculator.computeDealScores(deal);

        assertEquals(0, scoreByTeam.get(contractPlayer.getTeam().orElseThrow()));
        assertEquals(500, scoreByTeam.get(firstOpponent.getTeam().orElseThrow()));
    }

    @DisplayName("Compute score when attack team announces capot and reach the contract, without double nor redouble")
    @Test
    public void testExceptionIfCardScoreSumIsNot162() {

        var contractPlayer = player1;

        CardSuit trumpSuit = CardSuit.HEARTS;

        Set<ClassicalCard> team1Cards = new HashSet<>();
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.HEARTS).trumpStrongTrick_55points);
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.HEARTS).trumpWeakTrick_7points); // 62
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.SPADES).standardStrongTrick_27points); // 89
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.SPADES).standardWeakTrick_3points); // 92

        Set<ClassicalCard> team2Cards = new HashSet<>();
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.DIAMONDS).standardStrongTrick_27points); // 119
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.DIAMONDS).standardWeakTrick_3points); // 122
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.CLUBS).standardStrongTrick_27points); // 149
        // Missing trick :  team2Cards.addAll(typicalTricksBySuit.get(CardSuit.CLUBS).standardWeakTrick_3points);


        Map<Team, Set<ContreeCard>> cardsByTeam = Map.of(
                ContreeTeam.TEAM1, ContreeCard.of( trumpSuit, team1Cards ),
                ContreeTeam.TEAM2, ContreeCard.of( trumpSuit, team2Cards )
        );

        ContreeDeal deal = MockDealBuilder.builder()
                .withDealContractBid(
                        new ContreeBid(contractPlayer, ContreeBidValue.CAPOT, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withLastTrickWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(cardsByTeam)
                .build();

        var re = assertThrows(
            RuntimeException.class,
            () -> dealScoreCalculator.computeDealScores(deal)
        );

        assertTrue(re.getMessage().contains("Cheat"));

    }

    @DisplayName("When only NONE bids are placed on a bid, expected scores are 0")
    @Test
    public void testComputeScoreOnDealWithOnlyNoneBids() {

        ContreeDeal onlyNoneBidsDeal = MockDealBuilder.builder()
                .withDealContractBid(new ContreeBid(player1))
                .withHasOnlyNoneBids(true)
                .build();


        var scores = dealScoreCalculator.computeDealScores(onlyNoneBidsDeal);

        assertTrue(scores.values().stream().allMatch(i -> i == 0));

    }

    @DisplayName("When no bids are placed on a deal, trying to compute scores throws exception")
    @Test
    public void testComputeScoreOnDealWithNoBidsThrowsException() {
        ContreeDeal onlyNoneBidsDeal = MockDealBuilder.builder()
                .withDealContractBid(null)
                .build();

        assertThrows(
                RuntimeException.class,
                () -> dealScoreCalculator.computeDealScores(onlyNoneBidsDeal)
        );
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

        var dealPoints = dealScoreCalculator.computeCardPoints(dealCards);

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

        var dealPoints = dealScoreCalculator.computeCardPoints(dealCards);

        assertEquals(45, dealPoints);

    }

    @DisplayName("A full card set contains 152 points (162 when we count the 10 bonus points for the last trick)")
    @Test
    public void testCountPointsForAllCards() {

        Collection<ContreeCard> dealCards = buildContreeCards(
                CardSuit.CLUBS,
                CardSet.GAME_32.getGameCards()
        );

        var dealPoints = dealScoreCalculator.computeCardPoints(dealCards);

        assertEquals(152, dealPoints);

    }


}
