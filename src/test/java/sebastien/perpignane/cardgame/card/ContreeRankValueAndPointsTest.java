package sebastien.perpignane.cardgame.card;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.contree.ContreeRankValueAndPoints;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ContreeRankValueAndPointsTest {

    @DisplayName("Sorting non trump cards by game value in descending order")
    @Test
    public void testStandardDescOrderingByValue() {
        List<ContreeRankValueAndPoints> standardCards  = Arrays.stream(ContreeRankValueAndPoints.values())
                .sorted(
                    Comparator.comparingInt(
                        ContreeRankValueAndPoints::getStandardGameValue
                    ).reversed()
                ).toList();

        int idx = 0;

        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.ACE);
        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.TEN);
        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.KING);
        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.QUEEN);
        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.JACK);
        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.NINE);
        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.EIGHT);
        assertThat(standardCards.get(idx)).isSameAs(ContreeRankValueAndPoints.SEVEN);
    }

    @DisplayName("Sorting trump cards by game value in descending order")
    @Test
    public void testTrumpDescOrderingByValue() {
        List<ContreeRankValueAndPoints> trumpCards  = Arrays.stream(ContreeRankValueAndPoints.values()).
                sorted(
                    Comparator.comparingInt(
                        ContreeRankValueAndPoints::getTrumpGameValue
                    ).reversed()
                ).toList();

        int idx = 0;

        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.JACK);
        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.NINE);
        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.ACE);
        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.TEN);
        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.KING);
        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.QUEEN);
        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.EIGHT);
        assertThat(trumpCards.get(idx)).isSameAs(ContreeRankValueAndPoints.SEVEN);
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

        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.ACE);
        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.TEN);
        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.KING);
        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.QUEEN);
        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.JACK);
        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.NINE);
        assertThat(standardCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.EIGHT);
        assertThat(standardCards.get(idx)).isSameAs(ContreeRankValueAndPoints.SEVEN);
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

        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.JACK);
        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.NINE);
        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.ACE);
        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.TEN);
        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.KING);
        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.QUEEN);
        assertThat(trumpCards.get(idx++)).isSameAs(ContreeRankValueAndPoints.EIGHT);
        assertThat(trumpCards.get(idx)).isSameAs(ContreeRankValueAndPoints.SEVEN);
    }

}
