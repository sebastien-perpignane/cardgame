package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.game.GameState;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

        assertTrue(calledFlag[0]);

    }

    @Test
    public void testSendPlayedCardEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(gameObserver).onCardPlayed(any(), any());

        gameEventSender.sendPlayedCardEvent(player1, ClassicalCard.SEVEN_CLUB);

        assertTrue(calledFlag[0]);

    }

    @Test
    public void testSendStartOfDealEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onDealStarted(any());

        gameEventSender.sendStartOfDealEvent("TEST");

        assertTrue(calledFlag[0]);

    }

    @Test
    public void testSendEndOfDealEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onDealOver(any());

        gameEventSender.sendEndOfDealEvent("TEST");

        assertTrue(calledFlag[0]);

    }

    @Test
    void testSendPlacedBidEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onPlacedBid(any(), any(), any(), any());

        gameEventSender.sendPlacedBidEvent("TEST", new ContreeBid(player1, ContreeBidValue.NONE, null));

        assertTrue(calledFlag[0]);

    }

    @Test
    public void testSendBidStepStartedEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onBidStepStarted(any());

        gameEventSender.sendBidStepStartedEvent("TEST");

        assertTrue(calledFlag[0]);

    }

    @Test
    public void testSendBidStepEndedEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onBidStepEnded(any());

        gameEventSender.sendBidStepEndedEvent("TEST");

        assertTrue(calledFlag[0]);

    }

    @Test
    public void testSendPlayStepStartedEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onPlayStepStarted(any(), any());

        gameEventSender.sendPlayStepStartedEvent("TEST", CardSuit.DIAMONDS);

        assertTrue(calledFlag[0]);

    }

    @Test
    public void testSendPlayStepEndedEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(dealObserver).onPlayStepEnded(any());

        gameEventSender.sendPlayStepEndedEvent("TEST");

        assertTrue(calledFlag[0]);

    }

    @Test
    public void testSendTrumpedTrickEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(trickObserver).onTrumpedTrick(any());

        gameEventSender.sendTrumpedTrickEvent("TEST");

        assertTrue(calledFlag[0]);

    }

    @Test
    public void testSendNewTrickEvent() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(trickObserver).onNewTrick(any(), any());

        gameEventSender.sendNewTrickEvent("TEST", CardSuit.HEARTS);

        assertTrue(calledFlag[0]);

    }

    @DisplayName("A registered game observer received game state update events")
    @Test
    public void testUpdatedStateIsReceivedByRegisteredGameObserver() {

        doAnswer(invocationOnMock -> {
            calledFlag[0] = true;
            return null;
        }).when(gameObserver).onStateUpdated(any(), any());

        gameEventSender.registerAsGameObserver(gameObserver);

        gameEventSender.sendStateEvent(GameState.WAITING_FOR_PLAYERS, GameState.OVER);

        assertTrue(calledFlag[0]);

    }

}
