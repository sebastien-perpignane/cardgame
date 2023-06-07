package sebastien.perpignane.cardgame.card;

public enum CardSuit {

    /**
     * Carreau
     */
    DIAMONDS("♦"),
    /**
     * Trèfle
     */
    CLUBS("♣"),
    /**
     * Coeur
     */
    HEARTS("♥"),
    /**
     * Pique
     */
    SPADES("♠"),
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
