package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardDealer;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;
import sebastien.perpignane.cardgame.player.util.PlayerSlot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

class ContreeDealsTest extends TestCasesManagingPlayers {

    private ContreeDealPlayers dealPlayers;

    private ContreeGameScore gameScore;

    private ContreeDeals deals;

    @BeforeAll
    static void globalSetUp() {
        initPlayers();
    }

    @BeforeEach
    void setUp() {

        ContreeGameEventSender eventSender = mock(ContreeGameEventSender.class);

        ContreeBidPlayers bidPlayers = mock(ContreeBidPlayers.class);
        when(bidPlayers.getCurrentBidderSlot()).thenAnswer(AdditionalAnswers.returnsElementsOf(playerSlots));

        dealPlayers = mock(ContreeDealPlayers.class);
        when(dealPlayers.getNumberOfPlayers()).thenReturn(ContreePlayers.NB_PLAYERS);
        when(dealPlayers.buildBidPlayers()).thenReturn(bidPlayers);

        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);
        List<PlayerSlot<ContreePlayer>> multipliedPlayerSlots = loopingPlayerSlots(8);
        when(trickPlayers.getCurrentPlayerSlot()).thenAnswer(AdditionalAnswers.returnsElementsOf(multipliedPlayerSlots));
        when(dealPlayers.buildTrickPlayers()).thenReturn(trickPlayers);

        BiddableValuesFilter biddableValuesFilter = mock(BiddableValuesFilter.class);

        // All cards are playable
        PlayableCardsFilter filter = mock(PlayableCardsFilter.class);
        when(filter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        gameScore = mock(ContreeGameScore.class);
        DealScoreCalculator dealScoreCalculator = mock(DealScoreCalculator.class);
        when(dealScoreCalculator.computeDealScores(any())).thenReturn(new DealScoreResult(Collections.emptyMap(), Collections.emptyMap(), Collections.emptyMap(), true));

        CardDealer cardDealer = mock(CardDealer.class);

        BiddableValuesFilter.BidFilterResult bidFilterResult = mock(BiddableValuesFilter.BidFilterResult.class);
        when(bidFilterResult.biddableValues()).thenReturn(Arrays.stream(ContreeBidValue.values()).collect(Collectors.toSet()));
        when(bidFilterResult.exclusionCauseByBidValue()).thenReturn(Collections.emptyMap());

        when(biddableValuesFilter.biddableValues(any(), any())).thenReturn(bidFilterResult);

        deals = new ContreeDeals(gameScore, dealScoreCalculator, biddableValuesFilter, filter, cardDealer, eventSender);

    }

    @DisplayName("When deals are started, there is one ongoing deal")
    @Test
    void getCurrentDeal() {

        deals.startDeals("TEST", dealPlayers);

        assertThat(deals.getNbDeals()).isEqualTo(1);
        assertThat(deals.nbOverDeals()).isZero();
        assertThat(deals.nbOngoingDeals()).isEqualTo(1);

    }

    @DisplayName("When the first deal is over, 2 deals exist, one is over and one is in progress")
    @Test
    void testNbDeals() {

        deals.startDeals("TEST", dealPlayers);

        deals.placeBid(player1, ContreeBidValue.PASS, null);
        deals.placeBid(player2, ContreeBidValue.PASS, null);
        deals.placeBid(player3, ContreeBidValue.PASS, null);
        deals.placeBid(player4, ContreeBidValue.PASS, null);

        assertThat(deals.getNbDeals()).isEqualTo(2);
        assertThat(deals.nbOverDeals()).isEqualTo(1);
        assertThat(deals.nbOngoingDeals()).isEqualTo(1);

    }

    @DisplayName("When maximum score is reached, cards cannot be played anymore")
    @Test
    void testCannotPlayWhenMaxScoreReached() {

        when( gameScore.isMaximumScoreReached() ).thenReturn(true);
        when( gameScore.getWinner() ).thenReturn(Optional.of(ContreeTeam.TEAM1));

        assertThat( deals.isMaximumScoreReached() ).isTrue();

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
            () -> deals.playCard(player1, ClassicalCard.JACK_DIAMOND)
        );

        assertThat( deals.isMaximumScoreReached() ).isTrue();
        assertThat( deals.getWinner() ).isPresent();

    }

    @DisplayName("When a deal is over, the score of the deal is added to the score of the game")
    @Test
    void testScoreIsUpdatedAtEndOfDeal() {

        boolean[] calledFlag = {false};
        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(gameScore).addDealScore(any());

        deals.startDeals("TEST", dealPlayers);

        playFullDeal();

        assertThat(calledFlag[0]).isTrue();
        assertThat(deals.getWinner()).isEmpty();

    }

    private void playFullDeal() {

        deals.placeBid(player1, ContreeBidValue.EIGHTY, CardSuit.HEARTS);
        deals.placeBid(player2, ContreeBidValue.PASS, null);
        deals.placeBid(player3, ContreeBidValue.PASS, null);
        deals.placeBid(player4, ContreeBidValue.PASS, null);

        int i = 0;

        while (i < 8) {

            deals.playCard(player1, ClassicalCard.JACK_HEART);
            deals.playCard(player2, ClassicalCard.ACE_SPADE);
            deals.playCard(player3, ClassicalCard.ACE_HEART);
            deals.playCard(player4, ClassicalCard.ACE_CLUB);

            i++;
        }
    }

}
