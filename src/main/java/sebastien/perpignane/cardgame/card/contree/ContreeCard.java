package sebastien.perpignane.cardgame.card.contree;

import sebastien.perpignane.cardgame.card.CardRank;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.ValuableCard;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ContreeCard implements ValuableCard {

    private final ClassicalCard card;

    private final boolean trump;

    private final int value;

    private final int point;

    public ContreeCard(ClassicalCard card, CardSuit trumpSuit) {
        this(card, card.getSuit() == trumpSuit);
    }

    public ContreeCard(ClassicalCard card, boolean trump) {
        Objects.requireNonNull(card);
        this.card = card;
        this.trump = trump;
        var contreeRankValueAndPoints = ContreeRankValueAndPoints.getByCardValue(card.getRank());
        this.value = this.trump ? contreeRankValueAndPoints.getTrumpGameValue() : contreeRankValueAndPoints.getStandardGameValue();
        this.point = this.trump ? contreeRankValueAndPoints.getTrumpGamePoints() : contreeRankValueAndPoints.getStandardGamePoints();
    }

    public ClassicalCard getCard() {
        return card;
    }

    @Override
    public int getGamePoints() {
        return point;
    }

    @Override
    public int getGameValue() {
        return value;
    }

    public CardRank getRank() {
        return card.getRank();
    }

    public CardSuit getSuit() {
        return card.getSuit();
    }

    public boolean isTrump() {
        return trump;
    }

    public static Set<ContreeCard> of(final CardSuit trump, Set<ClassicalCard> cards) {
        return cards.stream().map(c -> new ContreeCard(c, trump)).collect(Collectors.toSet());
    }

    public static Set<ContreeCard> ofAllTrumps(Set<ClassicalCard> cards) {
        return cards.stream().map(c -> new ContreeCard(c, true)).collect(Collectors.toSet());
    }

    public static Set<ContreeCard> ofNoTrumps(Set<ClassicalCard> cards) {
        return cards.stream().map(c -> new ContreeCard(c, false)).collect(Collectors.toSet());
    }

}
