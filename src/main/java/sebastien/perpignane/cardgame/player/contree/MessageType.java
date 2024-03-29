package sebastien.perpignane.cardgame.player.contree;

public enum MessageType {

    PLAY(true, false),
    BID(false, true),
    EJECTED,
    GAME_STARTED,
    GAME_OVER,
    STATUS_UPDATE;

    private final boolean requiresAllowedCards;

    private final boolean requiresAllowedBidValues;

    MessageType() {
        this(false, false);
    }

    MessageType(boolean requiresAllowedCards, boolean requiresAllowedBidValues) {
        this.requiresAllowedCards = requiresAllowedCards;
        this.requiresAllowedBidValues = requiresAllowedBidValues;
    }

    public boolean isRequiresAllowedCards() {
        return requiresAllowedCards;
    }

    public boolean isRequiresAllowedBidValues() {
        return requiresAllowedBidValues;
    }
}
