package sebastien.perpignane.cardgame.player.contree.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

class ContreeLocalPlayerEventHandlerTest {

    private static final String TEST_PLAYER_NAME = "test";

    private ContreeLocalPlayerEventHandler handler;

    private ContreePlayer mockPlayer;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(ContreePlayer.class);
    }

    @DisplayName("The placed bid matches the user input - case of a bid value requiring to provide a card suit")
    @Test
    void testOnBidTurn_playerBidsValueRequiringSuit() throws Exception {

        withTextFromSystemIn("EIGHTY", "HEARTS").execute(
                () -> {
                    initHandler();
                    handler.onGameStarted();
                    handler.onPlayerTurnToBid(Set.of(ContreeBidValue.EIGHTY, ContreeBidValue.NINETY, ContreeBidValue.HUNDRED));

                    await().atMost(2, TimeUnit.SECONDS).untilAsserted(
                            () -> verify(mockPlayer).placeBid(ContreeBidValue.EIGHTY, CardSuit.HEARTS)
                    );
                }
        );

    }

    @DisplayName("The placed bid matches the user input - case of a bid value not requiring a card suit")
    @Test
    void testOnBidTurn_playerBidsValueNotRequiringSuit() throws Exception {

        withTextFromSystemIn("PASS").execute(
                () -> {

                    initHandler();
                    handler.onGameStarted();
                    handler.onPlayerTurnToBid(Set.of(ContreeBidValue.PASS, ContreeBidValue.EIGHTY, ContreeBidValue.NINETY, ContreeBidValue.HUNDRED));

                    await().atMost(2, TimeUnit.SECONDS).untilAsserted(
                            () -> verify(mockPlayer).placeBid(ContreeBidValue.PASS, null)
                    );
                }
        );

    }

    @DisplayName("The placed bid matches the user input - the first bid value is not allowed, the second is allowed")
    @Test
    void testOnBidTurn_firstBidValueNotAllowed() throws Exception {

        withTextFromSystemIn("EIGHTY", "NINETY", "HEARTS").execute(
                () -> {

                    initHandler();
                    handler.onGameStarted();
                    var allowedBidValues = Set.of(ContreeBidValue.PASS, ContreeBidValue.NINETY, ContreeBidValue.HUNDRED);
                    handler.onPlayerTurnToBid(allowedBidValues);

                    await().atMost(2, TimeUnit.SECONDS).untilAsserted(
                            () -> verify(mockPlayer).placeBid(ContreeBidValue.NINETY, CardSuit.HEARTS)
                    );
                }
        );

    }

    @DisplayName("User enters 3 times a not allowed bid value - game is stopped with non zero exit code")
    @Test
    void testOnBidTurn_maxRetry() throws Exception {

        withTextFromSystemIn("EIGHTY", "EIGHTY", "EIGHTY").execute(
                () -> {

                    initHandler();
                    handler.onGameStarted();
                    int exitCode = catchSystemExit(
                            () -> {
                                handler.onPlayerTurnToBid(Set.of(ContreeBidValue.PASS, ContreeBidValue.NINETY, ContreeBidValue.HUNDRED));

                                await().atMost(2, TimeUnit.SECONDS).untilAsserted(
                                        () -> assertThat(handler.handlerThread.getState()).isSameAs(Thread.State.TERMINATED)
                                );
                            }
                    );

                    assertThat(exitCode).isNotZero();

                }
        );

    }

    @DisplayName("User leaves the game in bidding step - game is stopped with zero exit code")
    @Test
    void testOnBidTurn_leave() throws Exception {

        withTextFromSystemIn(ContreeLocalPlayerEventHandler.LEAVE_MSG).execute(
                () -> {

                    initHandler();
                    handler.onGameStarted();
                    int exitCode = catchSystemExit(
                            () -> {
                                handler.onPlayerTurnToBid(Set.of(ContreeBidValue.PASS, ContreeBidValue.NINETY, ContreeBidValue.HUNDRED));

                                await().atMost(2, TimeUnit.SECONDS).untilAsserted(
                                        () -> assertThat(handler.handlerThread.getState()).isSameAs(Thread.State.TERMINATED)
                                );
                            }
                    );

                    assertThat(exitCode).isZero();

                }
        );

    }

    @DisplayName("User input is an allowed card - the card is played")
    @Test
    void testOnPlayerTurn_cardIsAllowed() throws Exception {

        withTextFromSystemIn("JACK_HEART").execute(
                () -> {
                    initHandler();
                    handler.onGameStarted();
                    handler.onPlayerTurn(Set.of(ClassicalCard.ACE_SPADE, ClassicalCard.JACK_HEART, ClassicalCard.SEVEN_HEART));

                    await().atMost(2, TimeUnit.SECONDS).untilAsserted(
                            () -> verify(mockPlayer).playCard(ClassicalCard.JACK_HEART)
                    );
                }
        );

    }

    @DisplayName("First entered card is now allowed then user input is an allowed card - the second card is played")
    @Test
    void testOnPlayerTurn_firstCardNotAllowed() throws Exception {

        withTextFromSystemIn("JACK_SPADE", "JACK_HEART").execute(
                () -> {
                    initHandler();
                    handler.onGameStarted();
                    handler.onPlayerTurn(Set.of(ClassicalCard.ACE_SPADE, ClassicalCard.JACK_HEART));

                    await().atMost(2, TimeUnit.SECONDS).untilAsserted(
                            () -> verify(mockPlayer).playCard(ClassicalCard.JACK_HEART)
                    );
                }
        );

    }

    @DisplayName("User enters 3 times a not allowed card - the game is over with non zero exit code")
    @Test
    void testOnPlayerTurn_maxRetry() throws Exception {

        withTextFromSystemIn("JACK_SPADE", "JACK_SPADE", "JACK_SPADE").execute(
                () -> {
                    initHandler();
                    handler.onGameStarted();
                    int exitCode = catchSystemExit(
                            () -> {
                                handler.onPlayerTurn(Set.of(ClassicalCard.ACE_SPADE, ClassicalCard.JACK_HEART));

                                await().atMost(2, TimeUnit.SECONDS).untilAsserted(
                                        () -> assertThat(handler.handlerThread.getState()).isSameAs(Thread.State.TERMINATED)
                                );
                            }
                    );
                    assertThat(exitCode).isNotZero();

                }
        );

    }

    @DisplayName("User leaves - the game is over with zero exit code")
    @Test
    void testOnPlayerTurn_leave() throws Exception {

        withTextFromSystemIn(ContreeLocalPlayerEventHandler.LEAVE_MSG).execute(
                () -> {
                    initHandler();
                    handler.onGameStarted();
                    int exitCode = catchSystemExit(
                            () -> {
                                handler.onPlayerTurn(Set.of(ClassicalCard.ACE_SPADE, ClassicalCard.JACK_HEART));

                                await().atMost(2, TimeUnit.SECONDS).untilAsserted(
                                        () -> assertThat(handler.handlerThread.getState()).isSameAs(Thread.State.TERMINATED)
                                );
                            }
                    );
                    assertThat(exitCode).isZero();

                }
        );

    }

    @DisplayName("toString() is formatted as expected")
    @Test
    void testToString() {
        initHandler();
        assertThat(handler).hasToString("* " + TEST_PLAYER_NAME + " *");
    }

    private void initHandler() {
        handler = new ContreeLocalPlayerEventHandler(new Scanner(System.in), TEST_PLAYER_NAME);
        handler.addCardSuitsByNameInCardSuitByLabelMap(); //scanner.nextLine() does not work with suit labels  like "♥"
        handler.addCardsByEnumName(); //scanner.nextLine() does not work with suit labels  like "♥"

        when(mockPlayer.getHand()).thenReturn(
            Set.of(
                ClassicalCard.ACE_SPADE,
                ClassicalCard.JACK_SPADE,
                ClassicalCard.NINE_SPADE,
                ClassicalCard.SEVEN_HEART
            )
        );
        handler.setPlayer(mockPlayer);
    }

}