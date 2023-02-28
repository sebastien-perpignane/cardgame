package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;
import sebastien.perpignane.cardgame.player.util.PlayerSlot;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

/*
 Responsibilities of ContreeTricks are :
    * manage tricks lifecycle (start the first one, start a new one when the previous is over or stop when all tricks are played)
    * provide information about the tricks to allow deal score calculation
        * teamWhoDidCapot *
        * wonCardsByTeam *
        * lastTrick *
        * isCapot *
        * nbOverTricks *
        * nbOngoingTricks *
 */
public class ContreeTricksTest extends TestCasesManagingPlayers {

    private ContreeTrickPlayers trickPlayers;

    private ContreeDeal deal;

    private ContreeTricks tricksWithHeartAsTrumpSuit;

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    /**
     * Contract is always 80 HEART
     * Mocked PlayableCardsFilter allows any card to simplify tests
     */
    @BeforeEach
    public void setUp() {

        trickPlayers = mock(ContreeTrickPlayers.class);

        ContreeGameEventSender gameEventSender = mock(ContreeGameEventSender.class);

        deal = MockDealBuilder.builder()
                .withDealContractBid(
                        new ContreeBid(players.get(0), ContreeBidValue.EIGHTY, CardSuit.HEARTS)
                )
                .withTrumpSuit(CardSuit.HEARTS)
                .withGameEventSender(gameEventSender)
                .build();

        // All cards are allowed, it simplifies tests
        PlayableCardsFilter playableCardsFilter = mock(PlayableCardsFilter.class);
        when(playableCardsFilter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        tricksWithHeartAsTrumpSuit = new ContreeTricks(playableCardsFilter, gameEventSender);

        configureTrickPlayersForNumberOfTricks(1);


    }

    private void configureTrickPlayersForNumberOfTricks(int nbTricks) {
        List<PlayerSlot<ContreePlayer>> multipliedPlayerSlots = loopingPlayerSlots(nbTricks);
        when(trickPlayers.getCurrentPlayerSlot()).thenAnswer(AdditionalAnswers.returnsElementsOf(multipliedPlayerSlots));
    }

    @DisplayName("Test state of not started tricks")
    @Test
    public void testNotStartedTricks() {

        assertThat(tricksWithHeartAsTrumpSuit.lastTrick()).isEmpty();
        assertThat(tricksWithHeartAsTrumpSuit.nbOverTricks()).isEqualTo(0);
        assertThat(tricksWithHeartAsTrumpSuit.nbOngoingTricks()).isEqualTo(0);
        assertThat(tricksWithHeartAsTrumpSuit.tricksAreOver()).isFalse();

    }

    @DisplayName("When tricks are started, there is one ongoing trick and none over")
    @Test
    public void testStartTricks() {

        tricksWithHeartAsTrumpSuit.startTricks(deal, trickPlayers);

        assertThat(tricksWithHeartAsTrumpSuit.nbOverTricks()).isEqualTo(0);
        assertThat(tricksWithHeartAsTrumpSuit.nbOngoingTricks()).isEqualTo(1);

        tricksWithHeartAsTrumpSuit.playerPlays(player1, ClassicalCard.JACK_CLUB);
        tricksWithHeartAsTrumpSuit.playerPlays(player2, ClassicalCard.JACK_CLUB);

    }

    @DisplayName("When 4 cards are played on the current trick, the current trick is over and a new one is started")
    @Test
    public void testWhen4CardsArePlayed() {

        configureTrickPlayersForNumberOfTricks(2);

        tricksWithHeartAsTrumpSuit.startTricks(deal, trickPlayers);

        // mocked PlayableCardsFilter is very permissive ^^
        tricksWithHeartAsTrumpSuit.playerPlays(player1, ClassicalCard.JACK_HEART);
        tricksWithHeartAsTrumpSuit.playerPlays(player2, ClassicalCard.JACK_CLUB);
        tricksWithHeartAsTrumpSuit.playerPlays(player3, ClassicalCard.JACK_CLUB);
        tricksWithHeartAsTrumpSuit.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertThat(tricksWithHeartAsTrumpSuit.nbOverTricks()).isEqualTo(1);
        assertThat(tricksWithHeartAsTrumpSuit.nbOngoingTricks()).isEqualTo(1);

        tricksWithHeartAsTrumpSuit.playerPlays(player1, ClassicalCard.JACK_HEART);
        tricksWithHeartAsTrumpSuit.playerPlays(player2, ClassicalCard.JACK_CLUB);
        tricksWithHeartAsTrumpSuit.playerPlays(player3, ClassicalCard.JACK_CLUB);
        tricksWithHeartAsTrumpSuit.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertThat(tricksWithHeartAsTrumpSuit.nbOverTricks()).isEqualTo(2);
        assertThat(tricksWithHeartAsTrumpSuit.nbOngoingTricks()).isEqualTo(1);

        assertThat(tricksWithHeartAsTrumpSuit.lastTrick()).isPresent();
        assertThat(tricksWithHeartAsTrumpSuit.lastTrick().get().isOver()).isFalse();

    }

    @DisplayName("All tricks are won by team 1, team 1 makes capot")
    @Test
    public void testTeamWhoDidCapotWhenCapotHappens() {
        configureTrickPlayersForNumberOfTricks(8);

        tricksWithHeartAsTrumpSuit.startTricks(deal, trickPlayers);

        int i = 0;
        while( i < ContreeTricks.NB_TRICKS_PER_DEAL ) {
            // mocked PlayableCardsFilter is very permissive ^^
            tricksWithHeartAsTrumpSuit.playerPlays(player1, ClassicalCard.JACK_HEART);
            tricksWithHeartAsTrumpSuit.playerPlays(player2, ClassicalCard.JACK_CLUB);
            tricksWithHeartAsTrumpSuit.playerPlays(player3, ClassicalCard.JACK_CLUB);
            tricksWithHeartAsTrumpSuit.playerPlays(player4, ClassicalCard.JACK_CLUB);
            i++;
        }

        assertThat(tricksWithHeartAsTrumpSuit.tricksAreOver()).isTrue();
        assertThat(tricksWithHeartAsTrumpSuit.isCapot()).isTrue();
        assertThat(tricksWithHeartAsTrumpSuit.teamWhoDidCapot()).isPresent();
        assertThat(tricksWithHeartAsTrumpSuit.teamWhoDidCapot().get()).isSameAs(ContreeTeam.TEAM1);

        assertThat(tricksWithHeartAsTrumpSuit.nbOverTricks()).isEqualTo(8);
        assertThat(tricksWithHeartAsTrumpSuit.nbOngoingTricks()).isEqualTo(0);

    }

    @DisplayName("Not all tricks are won by the same team, no team doing capot")
    @Test
    public void testTeamWhoDidCapotWhenNoCapot() {

        configureTrickPlayersForNumberOfTricks(8);

        tricksWithHeartAsTrumpSuit.startTricks(deal, trickPlayers);

        // First 7 tricks are won by team 1
        int i = 0;
        while( i < ContreeTricks.NB_TRICKS_PER_DEAL - 1 ) {
            // mocked PlayableCardsFilter is very permissive ^^
            tricksWithHeartAsTrumpSuit.playerPlays(player1, ClassicalCard.JACK_HEART);
            tricksWithHeartAsTrumpSuit.playerPlays(player2, ClassicalCard.JACK_CLUB);
            tricksWithHeartAsTrumpSuit.playerPlays(player3, ClassicalCard.JACK_CLUB);
            tricksWithHeartAsTrumpSuit.playerPlays(player4, ClassicalCard.JACK_CLUB);
            i++;
        }

        // Final trick is won by team 2
        tricksWithHeartAsTrumpSuit.playerPlays(player1, ClassicalCard.JACK_CLUB);
        tricksWithHeartAsTrumpSuit.playerPlays(player2, ClassicalCard.JACK_HEART);
        tricksWithHeartAsTrumpSuit.playerPlays(player3, ClassicalCard.JACK_CLUB);
        tricksWithHeartAsTrumpSuit.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertThat(tricksWithHeartAsTrumpSuit.tricksAreOver()).isTrue();
        assertThat(tricksWithHeartAsTrumpSuit.isCapot()).isFalse();
        assertThat(tricksWithHeartAsTrumpSuit.teamWhoDidCapot()).isEmpty();

        assertThat(tricksWithHeartAsTrumpSuit.nbOverTricks()).isEqualTo(8);
        assertThat(tricksWithHeartAsTrumpSuit.nbOngoingTricks()).isEqualTo(0);

        Map<Team, Set<ContreeCard>> wonCardsByTeam = tricksWithHeartAsTrumpSuit.wonCardsByTeam();

        var expectedWonCards = Set.of(ClassicalCard.JACK_HEART, ClassicalCard.JACK_CLUB);

        assertThat(wonCardsByTeam.get(ContreeTeam.TEAM1).stream().map(ContreeCard::getCard).collect(Collectors.toSet())).isEqualTo(expectedWonCards);
        assertThat(wonCardsByTeam.get(ContreeTeam.TEAM2).stream().map(ContreeCard::getCard).collect(Collectors.toSet())).isEqualTo(expectedWonCards);

    }

    @DisplayName("Tricks cannot be started multiple times")
    @Test
    public void testStartTricksMultipleTimesFails() {
        tricksWithHeartAsTrumpSuit.startTricks(deal, trickPlayers);

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> tricksWithHeartAsTrumpSuit.startTricks(deal, trickPlayers));

    }

    @DisplayName("teamWhoDidCapot return Optional.empty if all tricks were not played yet")
    @Test
    public void testComputeTeamWhoDidCapotReturnsEmptyIfTricksAreNotOver() {

        configureTrickPlayersForNumberOfTricks(2);

        tricksWithHeartAsTrumpSuit.startTricks(deal, trickPlayers);

        // mocked PlayableCardsFilter is very permissive ^^
        tricksWithHeartAsTrumpSuit.playerPlays(player1, ClassicalCard.JACK_HEART);
        tricksWithHeartAsTrumpSuit.playerPlays(player2, ClassicalCard.JACK_CLUB);
        tricksWithHeartAsTrumpSuit.playerPlays(player3, ClassicalCard.JACK_CLUB);
        tricksWithHeartAsTrumpSuit.playerPlays(player4, ClassicalCard.JACK_CLUB);

        assertThat(tricksWithHeartAsTrumpSuit.teamWhoDidCapot()).isEmpty();

    }

    @DisplayName("When tricks are not started, there is no current player")
    @Test
    public void testCurrentPlayer_notStartedTrick() {
        assertThat(tricksWithHeartAsTrumpSuit.getCurrentPlayer()).isEmpty();
    }

    @Test
    public void testCurrentPlayer_notOver() {

        tricksWithHeartAsTrumpSuit.startTricks(deal, trickPlayers);

        assertThat(tricksWithHeartAsTrumpSuit.tricksAreOver()).isFalse();
        assertThat(tricksWithHeartAsTrumpSuit.getCurrentPlayer()).isPresent();
        assertThat(tricksWithHeartAsTrumpSuit.getCurrentPlayer().get()).isSameAs(player1);

    }

}
