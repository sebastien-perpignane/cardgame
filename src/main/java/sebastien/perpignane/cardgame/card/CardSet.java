package sebastien.perpignane.cardgame.card;

import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

public enum CardSet {

    GAME_32 {
        @Override
        protected SortedSet<Card> selectCards() {
            return Arrays.stream(Card.values())
                    .filter(c -> c.getValue().ordinal() > CardValue.SIX.ordinal() && c.getValue() != CardValue.JOKER)
                    .collect(sortedSetCollector());
        }
    },
    GAME_52 {
        @Override
        protected SortedSet<Card> selectCards() {
            return Arrays.stream(Card.values())
                    .filter(c -> c.getValue() != CardValue.JOKER)
                    .collect(sortedSetCollector());
        }
    },
    GAME_54 {
        @Override
        protected SortedSet<Card> selectCards() {
            return new TreeSet<>(Arrays.asList(Card.values()));
        }
    };

    private final SortedSet<Card> gameCards;

    CardSet() {
        this.gameCards = selectCards();
    }

    abstract protected SortedSet<Card> selectCards();

    public SortedSet<Card> getGameCards() {
        return gameCards;
    }

    private static Collector<Card, ?, SortedSet<Card>> sortedSetCollector() {
        final Supplier<SortedSet<Card>> supplier = TreeSet::new;
        final BiConsumer<SortedSet<Card>, Card> accumulator = Set::add;
        final BinaryOperator<SortedSet<Card>> combiner = (a, b) -> {
            a.addAll(b);
            return a;
        };
        return Collector.of(
                supplier,
                accumulator,
                combiner,
                Collector.Characteristics.IDENTITY_FINISH,
                Collector.Characteristics.UNORDERED

        );
    }
}
