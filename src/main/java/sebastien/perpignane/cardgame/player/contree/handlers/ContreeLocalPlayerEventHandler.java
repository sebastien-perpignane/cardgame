package sebastien.perpignane.cardgame.player.contree.handlers;

import sebastien.perpignane.cardgame.card.CardRank;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBid;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.player.contree.ContreePlayerStatus;
import sebastien.perpignane.cardgame.player.contree.PlayerMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.*;

class MaxRetryException extends Exception {
    public MaxRetryException(String message) {
        super(message);
    }
}

class LeaverException extends Exception {}

public class ContreeLocalPlayerEventHandler extends ThreadLocalContreePlayerEventHandler {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    static final String LEAVE_MSG = "leave";

    private static final Map<String, ClassicalCard> CARD_BY_LABEL;

    private static final Map<String, ContreeBidValue> BID_VALUE_BY_LABEL;

    private static final Map<String, CardSuit> CARD_SUIT_BY_LABEL;

    private static final String ALL_BID_VALUES_AS_STRING = streamToJoinedStr(
            Arrays.stream(ContreeBidValue.values()).sorted()
    );

    private static final String CARD_SUIT_VALUES_AS_STRING =
            streamToJoinedStr(
                    Arrays.stream(CardSuit.values())
                            .filter(Predicate.not(cs -> cs == CardSuit.NONE))
                            .sorted()
            );

    static {
        CARD_BY_LABEL =
                CardSet.GAME_32.getGameCards().stream().collect(Collectors.toMap(
                        ClassicalCard::toString,
                        c -> c
                ));

        BID_VALUE_BY_LABEL = Arrays.stream(ContreeBidValue.values()).collect(Collectors.toMap(
                ContreeBidValue::toString,
                bv -> bv
        ));

        CARD_SUIT_BY_LABEL =  ContreeBid.allowedCardSuitsForValuedBids().stream()
                .collect(
                    Collectors.toMap(
                        CardSuit::toString,
                        cs -> cs
                    )
                );

    }

    private final String name;
    private final Scanner scanner;

    @Override
    public String getName() {
        return name;
    }

    public ContreeLocalPlayerEventHandler(Scanner scanner, String name) {
        this.name = name;
        this.scanner = scanner;
    }

    @Override
    public void onReceivedHand(Collection<ClassicalCard> hand) {
        // Do nothing, hand is displayed every time it is needed by the manage* methods
    }

    @Override
    public boolean isBot() {
        return false;
    }

    @Override
    void managePlayMessage(PlayerMessage playMessage) {

        out.printf("Your turn to play, %s.%n", getName());
        out.printf("Here is your hand : %s%n", playerHandAsSortedJoinedStr());

        ClassicalCard playedCard;
        if (playMessage.onlyOneAllowedCard()) {
            playedCard = playMessage.allowedCards().iterator().next();
            out.printf("Only one allowed card: %s. This card is played automatically. Please press enter.%n", playedCard);
            scanner.nextLine();
            getPlayer().playCard(playedCard);
        }
        else {

            try {
                playedCard = managePlayCardUserInput(playMessage.allowedCards());
                getPlayer().playCard(playedCard);
            }
            catch (MaxRetryException e) {
                out.printf("%s. Is it a technical issue ? Please create an issue at https://github.com/sebastien-perpignane/cardgame/%n", e.getMessage());
                System.exit(1);
            }
            catch (LeaverException e) {
                out.println("Leaving the game.");
                getPlayer().leaveGame();
                exit(0);
            }
            catch(Exception e) {
                err.printf("Unexpected error occurred when placing your bid: %s. Please create an issue at https://github.com/sebastien-perpignane/cardgame/%n", e.getMessage());
                e.printStackTrace(err);
                exit(1);
            }

        }

    }

    private ClassicalCard managePlayCardUserInput(Collection<ClassicalCard> allowedCards) throws MaxRetryException, LeaverException {

        final String allowedCardsAsString = collectionToJoinedStr(
                ClassicalCard.sort(allowedCards)
        );

        ClassicalCard playedCard = null;

        int nbTries = 0;
        while (playedCard == null && nbTries < 3) {
            out.printf("Select a card among the allowed ones : %s:%n", allowedCardsAsString);
            String selectedCard = scanner.nextLine();

            if (LEAVE_MSG.equalsIgnoreCase(selectedCard)) {
                throw new LeaverException();
            }

            playedCard = CARD_BY_LABEL.get(selectedCard);

            if (notAllowedCard(playedCard, allowedCards)) {
                out.printf("%s is not an allowed card. Please play one of these cards : %s", playedCard, allowedCardsAsString);
                playedCard = null;
            }

            nbTries++;

        }

        if (playedCard == null) {
            throw new MaxRetryException("Maximum number of retries reached when selecting a card to play");
        }

        return playedCard;

    }

    private boolean notAllowedCard(ClassicalCard playedCard, Collection<ClassicalCard> allowedCards) {
        return playedCard != null && !allowedCards.contains(playedCard);
    }

    @Override
    void manageBidMessage(PlayerMessage bidMessage) {

        out.printf("Your turn to bid, %s.%n", getName());
        out.printf("Here is your hand: %s%n", playerHandAsSortedJoinedStr());

        try {
            ContreeBidValue bidValue = manageBidValueUserInput(bidMessage.allowedBidValues());

            CardSuit bidSuit = null;
            if (bidValue.isCardSuitRequired()) {
                bidSuit = manageBidSuitUserInput();
            }

            getPlayer().placeBid(bidValue, bidSuit);
        }
        catch(LeaverException le) {
            out.println("Leaving the game.");
            getPlayer().leaveGame();
            exit(0);
        }
        catch(MaxRetryException mre) {
            out.printf("%s. Is it a technical issue ? Please create an issue at https://github.com/sebastien-perpignane/cardgame/%n", mre.getMessage());
            System.exit(1);
        }
        catch (Exception e) {
            err.printf("Unexpected error occurred when placing your bid: %s. Please create an issue at https://github.com/sebastien-perpignane/cardgame/%n", e.getMessage());
            e.printStackTrace(err);
            System.exit(1);
        }

    }

    private ContreeBidValue manageBidValueUserInput(Collection<ContreeBidValue> allowedBidValues) throws MaxRetryException, LeaverException  {

        String allowedBidValuesDisplay = streamToJoinedStr(allowedBidValues.stream().sorted());

        ContreeBidValue bidValue = null;

        int nbTries = 0;
        while (bidValue == null && nbTries < 3) {
            out.printf("All bid values are: %s%n", ALL_BID_VALUES_AS_STRING);
            out.printf("Select a bid value. Allowed values are : %s%n : ", allowedBidValuesDisplay);

            String selectedBid = scanner.nextLine();
            if (LEAVE_MSG.equalsIgnoreCase(selectedBid)) {
                throw new LeaverException();
            }

            bidValue = BID_VALUE_BY_LABEL.get(selectedBid);
            if (!allowedBidValues.contains(bidValue)) {
                bidValue = null;
            }
            nbTries++;
        }

        if (bidValue == null) {
            throw new MaxRetryException("Maximum number of retries reached when selecting bid value");
        }

        return bidValue;

    }

    private CardSuit manageBidSuitUserInput() throws MaxRetryException, LeaverException {
        CardSuit bidSuit = null;
        int nbTries = 0;
        while (bidSuit == null && nbTries < 3) {
            out.printf("Select a card suit. Allowed values are : %s%n : ", CARD_SUIT_VALUES_AS_STRING);
            String selectedSuit = scanner.nextLine();
            if (LEAVE_MSG.equalsIgnoreCase(selectedSuit)) {
                throw new LeaverException();
            }
            bidSuit = CARD_SUIT_BY_LABEL.get(selectedSuit);
            nbTries++;
        }

        if (bidSuit == null) {
            throw new MaxRetryException("Maximum number of retries reached when selecting bid suit");
        }

        return bidSuit;

    }

    private String playerHandAsSortedJoinedStr() {
        return collectionToJoinedStr(
                ClassicalCard.sort( getPlayer().getHand() ).stream().map(c -> {
                    if (cardIsJackOrNine(c)) {
                        return String.format("%s%s%s", ANSI_RED, c, ANSI_RESET);
                    }
                    else if (cardIsAce(c)) {
                        return String.format("%s%s%s", ANSI_PURPLE, c, ANSI_RESET);
                    }
                    else {
                        return c.toString();
                    }
                }).toList()
        );
    }

    private boolean cardIsJackOrNine(ClassicalCard c) {
        return c.getRank() == CardRank.JACK || c.getRank() == CardRank.NINE;
    }

    private boolean cardIsAce(ClassicalCard c) {
        return c.getRank() == CardRank.ACE;
    }

    @Override
    public void onStatusUpdate(ContreePlayerStatus oldStatus, ContreePlayerStatus newStatus) {
        out.printf("You're now %s%n", newStatus);
    }

    @Override
    public String toString() {
        return String.format("* %s *", getName());
    }

    private static String collectionToJoinedStr(Collection<?> collection) {
        return streamToJoinedStr(collection.stream());
    }

    private static String streamToJoinedStr(Stream<?> stream) {
        return stream.map(Object::toString).collect(Collectors.joining(", "));
    }

    // For testing
    void addCardSuitsByNameInCardSuitByLabelMap() {
        CARD_SUIT_BY_LABEL.putAll(

                ContreeBid.allowedCardSuitsForValuedBids().stream()
                        .collect(
                                Collectors.toMap(
                                        CardSuit::name,
                                        cs -> cs
                                )
                        )
        );
    }

    // For testing
    void addCardsByEnumName() {
        CARD_BY_LABEL.putAll(
                CardSet.GAME_32.getGameCards().stream().collect(Collectors.toMap(
                        Enum::name,
                        c -> c
                ))
        );
    }

}
