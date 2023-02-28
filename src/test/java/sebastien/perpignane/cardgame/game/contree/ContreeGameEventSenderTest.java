package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.game.GameStatus;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class ContreeGameEventSenderTest extends TestCasesManagingPlayers {

    private final boolean[] calledFlag = new boolean[1];

    private GameObserver gameObserver;

    private ContreeDealObserver dealObserver;

    private ContreeTrickObserver trickObserver;

    private ContreeGameEventSender gameEventSender;

    @BeforeEach
    public void setUp() {

        calledFlag[0] = false;

        gameObserver = mock(GameObserver.class);
        dealObserver = mock(ContreeDealObserver.class);
        trickObserver = mock(ContreeTrickObserver.class);

        gameEventSender = new ContreeGameEventSender();

        gameEventSender.registerAsObserver(gameObserver);
        gameEventSender.registerAsObserver(dealObserver);
        gameEventSender.registerAsObserver(trickObserver);

    }

    @Test
    public void testSendEndOfGameEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(gameObserver).onEndOfGame((ContreeGame) any());

        ContreeGame game = mock(ContreeGame.class);
        gameEventSender.sendEndOfGameEvent(game);

        boolean endOfGameWasCalled = calledFlag[0];
        assertThat(endOfGameWasCalled).isTrue();

    }

    @Test
    public void testSendPlayedCardEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(gameObserver).onCardPlayed(any(), any());

        gameEventSender.sendPlayedCardEvent(player1, ClassicalCard.SEVEN_CLUB);

        boolean onCardPlayedWasCalled = calledFlag[0];
        assertThat( onCardPlayedWasCalled ).isTrue();

    }

    @Test
    public void testSendStartOfDealEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onDealStarted(anyInt(), any());

        gameEventSender.sendStartOfDealEvent(-1, "TEST");

        boolean onDealStartedWasCalled = calledFlag[0];
        assertThat(onDealStartedWasCalled).isTrue();

    }

    @Test
    public void testSendEndOfDealEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onEndOfDeal(any(), any(), any(), anyBoolean());

        ContreeDealScore dealScore = mock(ContreeDealScore.class);

        gameEventSender.sendEndOfDealEvent("TEST", ContreeTeam.TEAM1, dealScore, true);

        boolean onEndOfDealWasCalled = calledFlag[0];
        assertThat( onEndOfDealWasCalled ).isTrue();

    }

    @Test
    void testSendPlacedBidEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onPlacedBid(any(), any(), any(), any());

        gameEventSender.sendPlacedBidEvent("TEST", new ContreeBid(player1, ContreeBidValue.PASS, null));

        boolean onPlacedBidWasCalled = calledFlag[0];
        assertThat( onPlacedBidWasCalled ).isTrue();

    }

    @Test
    public void testSendBidStepStartedEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onBidStepStarted(any());

        gameEventSender.sendBidStepStartedEvent("TEST");

        boolean onBidStepStartedWasCalled = calledFlag[0];
        assertThat( onBidStepStartedWasCalled ).isTrue();

    }

    @Test
    public void testSendBidStepEndedEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onBidStepEnded(any());

        gameEventSender.sendBidStepEndedEvent("TEST");

        boolean onBidStepEndedWasCalled = calledFlag[0];
        assertThat( onBidStepEndedWasCalled ).isTrue();

    }

    @Test
    public void testSendPlayStepStartedEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onPlayStepStarted(any(), any());

        gameEventSender.sendPlayStepStartedEvent("TEST", CardSuit.DIAMONDS);

        boolean onPlayStepStartedWasCalled = calledFlag[0];
        assertThat( onPlayStepStartedWasCalled ).isTrue();

    }

    @Test
    public void testSendPlayStepEndedEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onPlayStepEnded(any());

        gameEventSender.sendPlayStepEndedEvent("TEST");

        boolean onPlayStepEndedWasCalled = calledFlag[0];
        assertThat( onPlayStepEndedWasCalled ).isTrue();

    }

    @Test
    public void testSendTrumpedTrickEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(trickObserver).onTrumpedTrick(any());

        gameEventSender.sendTrumpedTrickEvent("TEST");

        boolean onTrumpTrickWasCalled = calledFlag[0];
        assertThat( onTrumpTrickWasCalled ).isTrue();

    }

    @Test
    public void testSendNewTrickEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(trickObserver).onNewTrick(any(), any());

        gameEventSender.sendNewTrickEvent("TEST", CardSuit.HEARTS);

        boolean onNewTrickWasCalled = calledFlag[0];
        assertThat( onNewTrickWasCalled ).isTrue();

    }

    @DisplayName("A registered game observer received game state update events")
    @Test
    public void testUpdatedStateIsReceivedByRegisteredGameObserver() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(gameObserver).onStateUpdated(any(), any());

        gameEventSender.registerAsGameObserver(gameObserver);

        gameEventSender.sendStateEvent(GameStatus.WAITING_FOR_PLAYERS, GameStatus.OVER);

        boolean onStateUpdatedWasCalled = calledFlag[0];
        assertThat( onStateUpdatedWasCalled ).isTrue();

    }

}
