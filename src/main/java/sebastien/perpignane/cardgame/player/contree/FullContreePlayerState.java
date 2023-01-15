package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;

import java.util.Collection;

public class FullContreePlayerState extends ContreePlayerState {

    private final String id;

    private final Collection<ClassicalCard> hand;

    public FullContreePlayerState(String name, ContreePlayerStatus status, String id, Collection<ClassicalCard> hand) {
        super(name, status);
        this.id = id;
        this.hand = hand;
    }

    public String getId() {
        return id;
    }

    public Collection<ClassicalCard> getHand() {
        return hand;
    }

}
