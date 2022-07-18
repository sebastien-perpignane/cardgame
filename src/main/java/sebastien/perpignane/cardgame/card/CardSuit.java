package sebastien.perpignane.cardgame.card;

public enum CardSuit {

    /**
     * Carreau
     */
    DIAMONDS("\u2666"),
    /**
     * Trèfle
     */
    CLUBS("\u2663"),
    /**
     * Coeur
     */
    HEARTS("\u2665"),
    /**
     * Pique
     */
    SPADES("\u2660"),
    NONE("");

    CardSuit(String label) {
        this.label = label;
    }

    private final String label;

    @Override
    public String toString() {
        return label;
    }
}
