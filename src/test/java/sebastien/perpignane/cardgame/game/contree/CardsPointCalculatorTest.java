package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CardsPointCalculatorTest {

    Collection<ContreeCard> buildData(CardSuit trump, Set<ClassicalCard> cards) {
        return ContreeCard.of(trump, cards);
    }

    @DisplayName("Count points for standard and trump cards")
    @Test
    public void testCountStandardAndTrumpCards() {
        Collection<ContreeCard> dealCards = buildData(
            CardSuit.HEARTS,
            Set.of(
                    ClassicalCard.ACE_SPADE,
                    ClassicalCard.TEN_SPADE,
                    ClassicalCard.EIGHT_SPADE,
                    ClassicalCard.SEVEN_DIAMOND
            )
        );

        CardsPointCalculator dealPointComputer = new CardsPointCalculator();
        var dealPoints = dealPointComputer.computePoints(dealCards);

        assertEquals(21, dealPoints);

    }

    @DisplayName("Count points for trump cards only")
    @Test
    public void testCountTrumpCards() {

        Collection<ContreeCard> dealCards = buildData(
            CardSuit.DIAMONDS,
            Set.of(
                    ClassicalCard.ACE_DIAMOND,
                    ClassicalCard.JACK_DIAMOND,
                    ClassicalCard.NINE_DIAMOND,
                    ClassicalCard.SEVEN_DIAMOND
            )
        );

        CardsPointCalculator dealPointComputer = new CardsPointCalculator();
        var dealPoints = dealPointComputer.computePoints(dealCards);

        assertEquals(45, dealPoints);

    }

    @DisplayName("A full card set contains 152 points (162 when we count the 10 bonus points for the last trick)")
    @Test
    public void testCountPointsForAllCards() {

        Collection<ContreeCard> dealCards = buildData(
                CardSuit.CLUBS,
                CardSet.GAME_32.getGameCards()
        );

        CardsPointCalculator dealPointComputer = new CardsPointCalculator();
        var dealPoints = dealPointComputer.computePoints(dealCards);

        assertEquals(152, dealPoints);

    }

}
