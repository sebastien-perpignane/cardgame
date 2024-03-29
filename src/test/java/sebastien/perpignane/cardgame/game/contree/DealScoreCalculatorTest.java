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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*
 Responsibilities are :
    * compute card score *
    * compute full deal score, that includes bonuses depending on capot announce, dix de der, double, redouble *
    * throw exception if the deal on which score is calculated is not consistent
      (no contract, no attack team, no defense team, bad total card score etc.) *
 */
class DealScoreCalculatorTest extends TestCasesManagingPlayers {

    private static Map<CardSuit, CardSuitTypicalTricks>  typicalTricksBySuit;

    private DealScoreCalculator dealScoreCalculator;

    record CardSuitTypicalTricks(
            CardSuit cardSuit,
            List<ClassicalCard> standardStrongTrick_28points, // gives 28
            List<ClassicalCard> standardWeakTrick_2points, // gives 2
            List<ClassicalCard> trumpStrongTrick_55points, // gives 55
            List<ClassicalCard> trumpWeakTrick_7points // gives 7
    ) {}

    @BeforeAll
    static void globalSetUp() {
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
    void setUp() {
        dealScoreCalculator = new DealScoreCalculator();
    }

    @DisplayName("Score cannot be calculated on a deal with no contract (no valued bids)")
    @Test
    void testExceptionIfNoContract() {

        ContreeDeal deal = MockDealBuilder.builder().build();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> dealScoreCalculator.computeDealScores(deal));

    }

    @DisplayName("Score cannot be calculated on a deal with no contract (no valued bids)")
    @Test
    void testExceptionIfNoAttackTeam() {

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

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> dealScoreCalculator.computeDealScores(deal));

    }

    private Map<Team, Set<ContreeCard>> buildCardsByTeamWithTeam1_89AndTeam2_63_trump_is_heart() {
        Set<ClassicalCard> team1Cards = new HashSet<>();
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.HEARTS).trumpStrongTrick_55points);
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.HEARTS).trumpWeakTrick_7points); // 62
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.SPADES).standardStrongTrick_28points); // 90


        Set<ClassicalCard> team2Cards = new HashSet<>();

        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.CLUBS).standardStrongTrick_28points);
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.SPADES).standardWeakTrick_2points); // 30
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.CLUBS).standardWeakTrick_2points); // 32
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.DIAMONDS).standardStrongTrick_28points); // 60
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.DIAMONDS).standardWeakTrick_2points); // 62

        return Map.of(
                ContreeTeam.TEAM1, ContreeCard.of(CardSuit.HEARTS, team1Cards),
                ContreeTeam.TEAM2, ContreeCard.of(CardSuit.HEARTS, team2Cards)
        );
    }

    @DisplayName("Compute score when attack team reached the contract, without double or redouble or capot")
    @Test
    void testStandardContractIsReached() {
        var contractPlayer = player1;
        var opponentPlayer = player2;

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

        var scores = dealScoreCalculator.computeDealScores(deal);

        var contractPlayerTeam = contractPlayer.getTeam().orElseThrow();
        var opponentTeam = opponentPlayer.getTeam().orElseThrow();


        assertThat( scores.rawScoreByTeam() )
                .containsEntry(contractPlayerTeam, 90)
                .containsEntry(opponentTeam, 72);

        assertThat(scores.finalRoundedScoreByTeam())
                .containsEntry(contractPlayerTeam, 90)
                .containsEntry(opponentTeam, 70); // 62 + dix de der, rounded


    }

    @DisplayName("Compute score when attack team reached the contract, with double but no redouble or capot")
    @Test
    void testContractIsReached_doubled() {
        var contractPlayer = player1;
        var opponentPlayer = player2;

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

        var dealScores = dealScoreCalculator.computeDealScores(deal);

        var contractPlayerTeam = contractPlayer.getTeam().orElseThrow();
        var opponentTeam = opponentPlayer.getTeam().orElseThrow();

        assertThat(dealScores.finalRoundedScoreByTeam())
                .containsEntry(contractPlayerTeam, 320)
                .containsEntry(opponentTeam, 0);
    }

    @DisplayName("Compute score when attack team reached the contract with double and redouble, but no capot announced")
    @Test
    void testContractIsReached_redoubled() {
        var contractPlayer = player1;
        var opponentPlayer = player2;

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

        var dealScores = dealScoreCalculator.computeDealScores(deal);

        var contractPlayerTeam = contractPlayer.getTeam().orElseThrow();
        var opponentTeam = opponentPlayer.getTeam().orElseThrow();

        // 88 + dix de der, rounded
        assertThat( dealScores.finalRoundedScoreByTeam() )
                .containsEntry(contractPlayerTeam, 640)
                .containsEntry(opponentTeam, 0);
    }

    @DisplayName("Compute score when attack team failed to reach the contract")
    @Test
    void testStandardContractIsNotReached() {

        var contractPlayer = player1;
        var opponentPlayer = player2;

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

        var dealScores = dealScoreCalculator.computeDealScores(deal);

        var contractPlayerTeam = contractPlayer.getTeam().orElseThrow();
        var opponentTeam = opponentPlayer.getTeam().orElseThrow();

        assertThat(dealScores.finalRoundedScoreByTeam() )
                .containsEntry(contractPlayerTeam, 0)
                .containsEntry(opponentTeam, 160);
    }

    @DisplayName("Compute score when attack team announces capot and reach the contract, without double nor redouble")
    @Test
    void testIsAnnouncedCapotReached() {

        var contractPlayer = player1;
        var firstOpponent = player2;

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(true)
                .withIsCapot(true)
                .withIsCapotMadeByAttackTeam(true)
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
        assertThat(firstOpponent.getTeam()).isNotEqualTo(contractPlayer.getTeam());

        var dealScores = dealScoreCalculator.computeDealScores(deal);

        var contractPlayerTeam = contractPlayer.getTeam().orElseThrow();
        var opponentPlayerTeam = firstOpponent.getTeam().orElseThrow();

        assertThat(dealScores.finalRoundedScoreByTeam() )
                .containsEntry(contractPlayerTeam, 500)
                .containsEntry(opponentPlayerTeam, 0);
    }

    @DisplayName("A capot made by attack team that was not announced gives 250 points to attack team")
    @Test
    void testNotAnnouncedCapotMadeByAttackTeam() {

        var contractPlayer = player1;
        var firstOpponent = player2;

        Map<Team, Set<ContreeCard>> cardsByTeam = Map.of(
                ContreeTeam.TEAM1, ContreeCard.of( CardSuit.HEARTS, CardSet.GAME_32.getGameCards() ),
                ContreeTeam.TEAM2, ContreeCard.of(CardSuit.HEARTS, Collections.emptySet())
        );

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(false)
                .withIsCapot(true)
                .withIsDouble(false)
                .withIsRedouble(false)
                .withDealContractBid(
                        new ContreeBid(contractPlayer, ContreeBidValue.HUNDRED_SIXTY, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withLastTrickWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(cardsByTeam)
                .build();

        // To make sure that buildPlayers() always return different teams for player 1 and player 2
        assertThat(firstOpponent.getTeam()).isNotEqualTo(contractPlayer.getTeam());

        var dealScore = dealScoreCalculator.computeDealScores(deal);

        var contractPlayerTeam = contractPlayer.getTeam().orElseThrow();
        var opponentPlayerTeam = firstOpponent.getTeam().orElseThrow();

        assertThat( dealScore.finalRoundedScoreByTeam() )
                .containsEntry(contractPlayerTeam, 250)
                .containsEntry(opponentPlayerTeam, 0);

    }

    @DisplayName("A capot made by defense team gives 250 points to defense team")
    @Test
    void testNotAnnouncedCapotMadeByDefenseTeam() {

        var contractPlayer = player1;
        var firstOpponent = player2;

        Map<Team, Set<ContreeCard>> cardsByTeam = Map.of(
                ContreeTeam.TEAM1, ContreeCard.of( CardSuit.HEARTS, Collections.emptySet() ),
                ContreeTeam.TEAM2, ContreeCard.of( CardSuit.HEARTS, CardSet.GAME_32.getGameCards() )
        );

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(false)
                .withIsCapot(true)
                .withIsDouble(false)
                .withIsRedouble(false)
                .withDealContractBid(
                        new ContreeBid(contractPlayer, ContreeBidValue.EIGHTY, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withLastTrickWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(cardsByTeam)
                .build();

        var dealScore = dealScoreCalculator.computeDealScores(deal);

        var contractPlayerTeam = contractPlayer.getTeam().orElseThrow();
        var opponentPlayerTeam = firstOpponent.getTeam().orElseThrow();

        assertThat( dealScore.finalRoundedScoreByTeam() )
                .containsEntry(contractPlayerTeam, 0)
                .containsEntry(opponentPlayerTeam, 250);

    }

    @DisplayName("Compute score when attack team announces capot and does not reach the contract, without double nor redouble")
    @Test
    void testAnnouncedCapotNotReached() {

        var contractPlayer = player1;
        var firstOpponent = player2;

        var cardsByTeam = buildCardsByTeamWithTeam1_89AndTeam2_63_trump_is_heart();

        ContreeDeal deal = MockDealBuilder.builder()
                .withIsAnnouncedCapot(true)
                .withIsCapot(false)
                .withIsCapotMadeByAttackTeam(false)
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
        assertThat(firstOpponent.getTeam()).isNotEqualTo(contractPlayer.getTeam());

        var dealScore = dealScoreCalculator.computeDealScores(deal);

        var contractPlayerTeam = contractPlayer.getTeam().orElseThrow();
        var opponentPlayerTeam = firstOpponent.getTeam().orElseThrow();

        assertThat(dealScore.finalRoundedScoreByTeam() )
                .containsEntry(contractPlayerTeam, 0)
                .containsEntry(opponentPlayerTeam, 500);
    }

    @DisplayName("When adding the points from both team, the sum must always be 152")
    @Test
    void testExceptionIfCardScoreSumIsNot162() {

        var contractPlayer = player1;

        CardSuit trumpSuit = CardSuit.HEARTS;

        Set<ClassicalCard> team1Cards = new HashSet<>();
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.HEARTS).trumpStrongTrick_55points);
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.HEARTS).trumpWeakTrick_7points); // 62
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.SPADES).standardStrongTrick_28points); // 89
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.SPADES).standardWeakTrick_2points); // 92

        Set<ClassicalCard> team2Cards = new HashSet<>();
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.DIAMONDS).standardStrongTrick_28points); // 119
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.DIAMONDS).standardWeakTrick_2points); // 122
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.CLUBS).standardStrongTrick_28points); // 149
        // Missing trick :  team2Cards.addAll(typicalTricksBySuit.get(CardSuit.CLUBS).standardWeakTrick_2points);


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

        assertThat( re.getMessage() ).contains("Cheat");

    }

    @DisplayName("Test all intermediate scores when attack team reaches his contract, without double nor redouble")
    @Test
    void testAllScoreTypesWhenContractIsReached() {

        var contractPlayer = player1;

        CardSuit trumpSuit = CardSuit.HEARTS;

        Set<ClassicalCard> team1Cards = new HashSet<>();
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.HEARTS).trumpStrongTrick_55points);
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.HEARTS).trumpWeakTrick_7points); // 62
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.SPADES).standardStrongTrick_28points); // 89
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.SPADES).standardWeakTrick_2points); // 92
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.DIAMONDS).standardWeakTrick_2points); // 94
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.CLUBS).standardWeakTrick_2points); // 96

        Set<ClassicalCard> team2Cards = new HashSet<>();
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.DIAMONDS).standardStrongTrick_28points);
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.CLUBS).standardStrongTrick_28points); // 56



        Map<Team, Set<ContreeCard>> cardsByTeam = Map.of(
                ContreeTeam.TEAM1, ContreeCard.of( trumpSuit, team1Cards ),
                ContreeTeam.TEAM2, ContreeCard.of( trumpSuit, team2Cards )
        );

        ContreeDeal deal = MockDealBuilder.builder()
                .withDealContractBid(
                        new ContreeBid(contractPlayer, ContreeBidValue.HUNDRED, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withLastTrickWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(cardsByTeam)
                .build();

        var dealScore = dealScoreCalculator.computeDealScores(deal);

        // Dix de der
        assertThat( dealScore.rawScoreByTeam() )
                .containsEntry(ContreeTeam.TEAM1, 96 + 10) // Dix de der
                .containsEntry(ContreeTeam.TEAM2, 56);

        assertThat( dealScore.finalNotRoundedScoreByTeam() )
                .containsEntry(ContreeTeam.TEAM1, 96 + 10) // Dix de der
                .containsEntry(ContreeTeam.TEAM2, 56);

        assertThat( dealScore.finalRoundedScoreByTeam() )
                .containsEntry(ContreeTeam.TEAM1, 110)
                .containsEntry(ContreeTeam.TEAM2, 60);

    }

    @DisplayName("Test all intermediate scores when attack team does not reach his contract, even if the contract seems reached once score is rounded")
    @Test
    void testAllScoreTypesWhenContractIsNotReached() {

        var contractPlayer = player1;

        CardSuit trumpSuit = CardSuit.HEARTS;

        Set<ClassicalCard> team1Cards = new HashSet<>();
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.HEARTS).trumpStrongTrick_55points);
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.HEARTS).trumpWeakTrick_7points); // 62
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.SPADES).standardStrongTrick_28points); // 89
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.SPADES).standardWeakTrick_2points); // 92
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.DIAMONDS).standardWeakTrick_2points); // 94
        team1Cards.addAll(typicalTricksBySuit.get(CardSuit.CLUBS).standardWeakTrick_2points); // 96

        Set<ClassicalCard> team2Cards = new HashSet<>();
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.DIAMONDS).standardStrongTrick_28points);
        team2Cards.addAll(typicalTricksBySuit.get(CardSuit.CLUBS).standardStrongTrick_28points); // 56



        Map<Team, Set<ContreeCard>> cardsByTeam = Map.of(
                ContreeTeam.TEAM1, ContreeCard.of( trumpSuit, team1Cards ),
                ContreeTeam.TEAM2, ContreeCard.of( trumpSuit, team2Cards )
        );

        ContreeDeal deal = MockDealBuilder.builder()
                .withDealContractBid(
                        new ContreeBid(contractPlayer, ContreeBidValue.HUNDRED_TEN, CardSuit.HEARTS)
                )
                .withAttackTeam(ContreeTeam.TEAM1)
                .withDefenseTeam(ContreeTeam.TEAM2)
                .withLastTrickWinnerTeam(ContreeTeam.TEAM1)
                .withCardsByTeam(cardsByTeam)
                .build();

        var result = dealScoreCalculator.computeDealScores(deal);

        // Dix de der
        assertThat(result.rawScoreByTeam())
                .containsEntry(ContreeTeam.TEAM1, 96 + 10)
                .containsEntry(ContreeTeam.TEAM2, 56);

        assertThat(result.finalNotRoundedScoreByTeam())
                .containsEntry(ContreeTeam.TEAM1, 0)
                .containsEntry(ContreeTeam.TEAM2, 160);


        assertThat(result.finalRoundedScoreByTeam())
                .containsEntry(ContreeTeam.TEAM1, 0)
                .containsEntry(ContreeTeam.TEAM2, 160);

    }

    @DisplayName("When only PASS bids are placed on a deal, expected scores are 0")
    @Test
    void testComputeScoreOnDealWithOnlyPassBids() {

        ContreeDeal onlyPassBidsDeal = MockDealBuilder.builder()
                .withDealContractBid(new ContreeBid(player1))
                .withHasOnlyPassBids(true)
                .build();


        var result = dealScoreCalculator.computeDealScores(onlyPassBidsDeal);

        assertThat(result.finalRoundedScoreByTeam().values().stream().allMatch(i -> i == 0)).isTrue();

    }

    @DisplayName("When no bids are placed on a deal, trying to compute scores throws exception")
    @Test
    void testComputeScoreOnDealWithNoBidsThrowsException() {
        ContreeDeal noBidDeal = MockDealBuilder.builder()
                .withDealContractBid(null)
                .build();

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> dealScoreCalculator.computeDealScores(noBidDeal));
    }

    private Collection<ContreeCard> buildContreeCards(CardSuit trump, Set<ClassicalCard> cards) {
        return ContreeCard.of(trump, cards);
    }

    @DisplayName("Count points for standard and trump cards")
    @Test
    void testCountStandardAndTrumpCards() {

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

        assertThat(dealPoints).isEqualTo(21);

    }

    @DisplayName("Count points for trump cards only")
    @Test
    void testCountTrumpCards() {

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

        assertThat(dealPoints).isEqualTo(45);

    }

    @DisplayName("A full card set contains 152 points (162 when we count the 10 bonus points for the last trick)")
    @Test
    void testCountPointsForAllCards() {

        Collection<ContreeCard> dealCards = buildContreeCards(
                CardSuit.CLUBS,
                CardSet.GAME_32.getGameCards()
        );

        var dealPoints = dealScoreCalculator.computeCardPoints(dealCards);

        assertThat(dealPoints).isEqualTo(152);

    }


}
