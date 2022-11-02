package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.System.out;

public class ContreeLocalConsoleHumanPlayer extends AbstractLocalThreadContreePlayer {

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

        cardSuitByLabel =  Arrays.stream(CardSuit.values()).collect(Collectors.toMap(
                CardSuit::toString,
                cs -> cs
        ));

    }

    private final String name;

    public ContreeLocalConsoleHumanPlayer(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    void managePlayMessage(PlayerMessage playMessage) {

        boolean cardPlayed = false;

        Scanner scanner = new Scanner(System.in);

        final String allowedCards = collectionToJoinedStr(
                ClassicalCard.sort(playMessage.allowedCards())
        );

        while (!cardPlayed) {
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
                    playedCard = cardByLabel.get(selectedCard);
                    if (playedCard != null && !playMessage.allowedCards().contains(playedCard)) {
                        out.printf("%s is not an allowed card. Please play one of these cards : %s", playedCard, allowedCards);
                        playedCard = null;
                    }
                }
                getHand().remove(playedCard);
                getGame().playCard(this, playedCard);
                cardPlayed = true;
            }
            catch(Exception e) {
                System.err.printf("Error occurred when playing your card: %s. Please try again.%n", e.getMessage());
            }
        }

    }

    @Override
    void manageBidMessage(PlayerMessage bidMessage) {

        int nbTries = 0;
        boolean bidPlaced = false;

        Scanner scanner = new Scanner(System.in);

        final String bidValues = streamToJoinedStr(
            Arrays.stream(ContreeBidValue.values()).sorted()
        );

        final String cardSuitValues =
                streamToJoinedStr(
                    Arrays.stream(CardSuit.values())
                        .filter(Predicate.not(cs -> cs == CardSuit.NONE))
                        .sorted()
                );

        out.printf("Your turn to bid, %s.%n", getName());
        out.printf("Here is your hand : %s%n", playerHandAsSortedJoinedStr());

        while (!bidPlaced && nbTries < 3) {
            try {
                ContreeBidValue bidValue = null;
                CardSuit bidSuit = null;

                while (bidValue == null) {
                    out.printf("Select a bid value. Allowed values are : %s%n : ", bidValues);
                    String selectedBid = scanner.nextLine();

                    bidValue = bidValueByLabel.get(selectedBid);

                }

                if ( !bidValue.isCardSuitRequired() ) {
                    getGame().placeBid(this, bidValue, null);
                    return;
                }

                while (bidSuit == null) {
                    out.printf("Select a card suit. Allowed values are : %s%n : ", cardSuitValues);
                    String selectedSuit = scanner.nextLine();
                    bidSuit = cardSuitByLabel.get(selectedSuit);
                }

                getGame().placeBid(this, bidValue, bidSuit);
                bidPlaced = true;
            }
            catch (Exception e) {
                System.err.printf("Error occurred when placing your bid: %s. Please try again.%n", e.getMessage());
            }
            finally {
                nbTries++;
            }
        }

        if (!bidPlaced) {
            System.err.println("It looks like you cannot bid. Is it a technical issue ? Please create an issue at https://github.com/sebastien-perpignane/cardgame/");
            System.exit(1);
        }

    }

    private String playerHandAsSortedJoinedStr() {
        return collectionToJoinedStr(
                ClassicalCard.sort( getHand() )
        );
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
