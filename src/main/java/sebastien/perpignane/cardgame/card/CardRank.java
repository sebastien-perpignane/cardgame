package sebastien.perpignane.cardgame.card;

public enum CardRank {
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    JACK("J"),
    QUEEN("Q"),
    KING("K"),
    ACE("A"),
    JOKER("JK");

    CardRank(String label) {
        this.label = label;
    }

    private final String label;

    @Override
    public String toString() {
        return label;
    }
}
