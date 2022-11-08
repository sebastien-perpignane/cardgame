package sebastien.perpignane.cardgame.card;

import java.util.Arrays;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public enum CardSet {

    GAME_32 {
        @Override
        protected SortedSet<ClassicalCard> selectCards() {
            return Arrays.stream(ClassicalCard.values())
                    .filter(c -> c.getRank().ordinal() > CardRank.SIX.ordinal() && c.getRank() != CardRank.JOKER)
                    .collect(Collectors.toCollection(TreeSet::new));
        }
    },
    GAME_52 {
        @Override
        protected SortedSet<ClassicalCard> selectCards() {
            return Arrays.stream(ClassicalCard.values())
                    .filter(c -> c.getRank() != CardRank.JOKER)
                    .collect(Collectors.toCollection(TreeSet::new));
        }
    },
    GAME_54 {
        @Override
        protected SortedSet<ClassicalCard> selectCards() {
            return new TreeSet<>(Arrays.asList(ClassicalCard.values()));
        }
    };

    private final SortedSet<ClassicalCard> gameCards;

    CardSet() {
        this.gameCards = selectCards();
    }

    abstract protected SortedSet<ClassicalCard> selectCards();

    public SortedSet<ClassicalCard> getGameCards() {
        return gameCards;
    }

    public Collection<ClassicalCard> allOf(CardSuit cardSuit) {
        return gameCards.stream().filter(c -> c.getSuit() == cardSuit).toList();
    }

}
