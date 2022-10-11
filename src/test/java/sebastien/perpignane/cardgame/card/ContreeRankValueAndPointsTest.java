package sebastien.perpignane.cardgame.card;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.contree.ContreeRankValueAndPoints;

import java.util.Arrays;
import java.util.Comparator;

public class ContreeRankValueAndPointsTest {

    @DisplayName("Sorting non trump cards by game value in descending order")
    @Test
    public void testStandardDescOrderingByValue() {
        var standardCards  = Arrays.stream(ContreeRankValueAndPoints.values())
                .sorted(
                    Comparator.comparingInt(
                        ContreeRankValueAndPoints::getStandardGameValue
                    ).reversed()
                ).toList();

        int idx = 0;

        assertEquals(ContreeRankValueAndPoints.ACE, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.TEN, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.KING, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.QUEEN, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.JACK, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.NINE, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.EIGHT, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.SEVEN, standardCards.get(idx));
    }

    @DisplayName("Sorting trump cards by game value in descending order")
    @Test
    public void testTrumpDescOrderingByValue() {
        var trumpCards  = Arrays.stream(ContreeRankValueAndPoints.values()).
                sorted(
                    Comparator.comparingInt(
                        ContreeRankValueAndPoints::getTrumpGameValue
                    ).reversed()
                ).toList();

        int idx = 0;

        assertEquals(ContreeRankValueAndPoints.JACK, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.NINE, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.ACE, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.TEN, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.KING, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.QUEEN, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.EIGHT, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.SEVEN, trumpCards.get(idx));
    }

    @DisplayName("Sorting non trump cards by game points in descending order")
    @Test
    public void testStandardDescOrderingByPoint() {
        var standardCards  = Arrays.stream(ContreeRankValueAndPoints.values())
                .sorted(
                        Comparator.comparingInt(
                            ContreeRankValueAndPoints::getStandardGamePoints
                        ).reversed()
                ).toList();

        int idx = 0;

        assertEquals(ContreeRankValueAndPoints.ACE, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.TEN, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.KING, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.QUEEN, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.JACK, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.NINE, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.EIGHT, standardCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.SEVEN, standardCards.get(idx));
    }

    @DisplayName("Sorting trump cards by game points in descending order")
    @Test
    public void testTrumpDescOrderingByPoint() {
        var trumpCards  = Arrays.stream(ContreeRankValueAndPoints.values())
                .sorted(
                    Comparator.comparingInt(
                        ContreeRankValueAndPoints::getTrumpGamePoints
                    ).reversed()
                ).toList();

        int idx = 0;

        assertEquals(ContreeRankValueAndPoints.JACK, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.NINE, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.ACE, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.TEN, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.KING, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.QUEEN, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.EIGHT, trumpCards.get(idx++));
        assertEquals(ContreeRankValueAndPoints.SEVEN, trumpCards.get(idx));
    }

}
