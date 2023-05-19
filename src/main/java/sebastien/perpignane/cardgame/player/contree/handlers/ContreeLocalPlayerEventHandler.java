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

import static java.lang.System.out;

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

    private final static Map<String, ClassicalCard> cardByLabel;

    private final static Map<String, ContreeBidValue> bidValueByLabel;

    private final static Map<String, CardSuit> cardSuitByLabel;

    static {
        cardByLabel =
                CardSet.GAME_32.getGameCards().stream().collect(Collectors.toMap(
                        ClassicalCard::toString,
                        c -> c
                ));

        bidValueByLabel = Arrays.stream(ContreeBidValue.values()).collect(Collectors.toMap(
                ContreeBidValue::toString,
                bv -> bv
        ));

        cardSuitByLabel =  ContreeBid.allowedCardSuitsWhenCardSuiteRequired().stream()
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
        boolean leaver = false;
        boolean cardPlayed = false;
        int nbTries = 0;

        final String allowedCards = collectionToJoinedStr(
                ClassicalCard.sort(playMessage.allowedCards())
        );

        playLoop:
        while (!cardPlayed && nbTries < 3) {
            try {
                out.printf("Your turn to play, %s.%n", getName());
                out.printf("Here is your hand : %s%n", playerHandAsSortedJoinedStr());

                ClassicalCard playedCard = null;
                if (playMessage.allowedCards().size() == 1) {
                    playedCard = playMessage.allowedCards().iterator().next();
                    out.printf("Only one allowed card: %s. This card is played automatically. Please press enter.%n", playedCard);
                    scanner.nextLine();
                }
                while (playedCard == null) {
                    out.printf("Select a card among the allowed ones : %s:%n", allowedCards);
                    String selectedCard = scanner.nextLine();

                    if ("leave".equalsIgnoreCase(selectedCard)) {
                        getPlayer().leaveGame();
                        leaver = true;
                        break playLoop;
                    }

                    playedCard = cardByLabel.get(selectedCard);
                    if (playedCard != null && !playMessage.allowedCards().contains(playedCard)) {
                        nbTries++;
                        out.printf("%s is not an allowed card. Please play one of these cards : %s", playedCard, allowedCards);
                        playedCard = null;
                    }
                }
                getPlayer().playCard(playedCard);
                cardPlayed = true;
            }
            catch(Exception e) {
                System.err.printf("Error occurred when playing your card: %s. Please try again.%n", e.getMessage());
                nbTries++;
            }
        }
        if (!cardPlayed && !leaver) {
            System.err.println("It looks like you cannot play. Is it a technical issue ? Please create an issue at https://github.com/sebastien-perpignane/cardgame/");
            System.exit(1);
        }
    }

    @Override
    void manageBidMessage(PlayerMessage bidMessage) {

        boolean leaver = false;

        int nbTries = 0;
        boolean bidPlaced = false;

        final String allowedBiValues = streamToJoinedStr(bidMessage.allowedBidValues().stream().sorted());

        final String allBidValues = streamToJoinedStr(
                Arrays.stream(ContreeBidValue.values()).sorted()
        );

        final String cardSuitValues =
                streamToJoinedStr(
                        Arrays.stream(CardSuit.values())
                                .filter(Predicate.not(cs -> cs == CardSuit.NONE))
                                .sorted()
                );

        out.printf("Your turn to bid, %s.%n", getName());
        out.printf("Here is your hand: %s%n", playerHandAsSortedJoinedStr());

        bidLoop:
        while (!bidPlaced && nbTries < 3) {
            try {
                ContreeBidValue bidValue = null;
                CardSuit bidSuit = null;

                while (bidValue == null) {
                    out.printf("All bid values are: %s%n", allBidValues);
                    out.printf("Select a bid value. Allowed values are : %s%n : ", allowedBiValues);
                    String selectedBid = scanner.nextLine();
                    if ("leave".equalsIgnoreCase(selectedBid)) {
                        getPlayer().leaveGame();
                        leaver = true;
                        break bidLoop;
                    }

                    bidValue = bidValueByLabel.get(selectedBid);

                }

                if ( !bidValue.isCardSuitRequired() ) {
                    getPlayer().placeBid(bidValue, null);
                    return;
                }

                while (bidSuit == null) {
                    out.printf("Select a card suit. Allowed values are : %s%n : ", cardSuitValues);
                    String selectedSuit = scanner.nextLine();
                    if ("leave".equalsIgnoreCase(selectedSuit)) {
                        getPlayer().leaveGame();
                        leaver = true;
                        break bidLoop;
                    }
                    bidSuit = cardSuitByLabel.get(selectedSuit);
                }

                getPlayer().placeBid(bidValue, bidSuit);
                bidPlaced = true;
            }
            catch (Exception e) {
                System.err.printf("Error occurred when placing your bid: %s. Please try again.%n", e.getMessage());
                e.printStackTrace(System.err);
            }
            finally {
                nbTries++;
            }
        }

        if (!bidPlaced && !leaver) {
            System.err.println("It looks like you cannot bid. Is it a technical issue ? Please create an issue at https://github.com/sebastien-perpignane/cardgame/");
            System.exit(1);
        }

    }
    private String playerHandAsSortedJoinedStr() {
        return collectionToJoinedStr(
                ClassicalCard.sort( getPlayer().getHand() ).stream().map(c -> {
                    if (c.getRank() == CardRank.JACK || c.getRank() == CardRank.NINE) {
                        return String.format("%s%s%s", ANSI_RED, c, ANSI_RESET);
                    }
                    else if (c.getRank() == CardRank.ACE) {
                        return String.format("%s%s%s", ANSI_PURPLE, c, ANSI_RESET);
                    }
                    else {
                        return c.toString();
                    }
                }).collect(Collectors.toList())
        );
    }

    @Override
    public void onStatusUpdate(ContreePlayerStatus oldStatus, ContreePlayerStatus newStatus) {
        System.out.printf("You're now %s%n", newStatus);
    }

    @Override
    public String toString() {
        return String.format("* %s *", getName());
    }

    private String collectionToJoinedStr(Collection<?> collection) {
        return streamToJoinedStr(collection.stream());
    }

    private String streamToJoinedStr(Stream<?> stream) {
        return stream.map(Object::toString).collect(Collectors.joining(", "));
    }

}
