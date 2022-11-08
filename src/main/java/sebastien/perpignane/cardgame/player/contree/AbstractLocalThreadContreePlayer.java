package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.game.contree.ContreeGame;
import sebastien.perpignane.cardgame.player.AbstractThreadLocalPlayer;
import sebastien.perpignane.cardgame.player.Team;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractLocalThreadContreePlayer extends AbstractThreadLocalPlayer<PlayerMessage, ContreeGame> implements ContreePlayer {

    private ContreeTeam team;

    private Collection<ClassicalCard> hand;

    private ContreeGame contreeGame;

    protected enum MessageType {
        PLAY,
        BID,
        END_OF_GAME
    }

    public void receiveHand(Collection<ClassicalCard> cards) {
        this.hand = cards;
    }

    @Override
    public void onUpdatedGame() {

    }

    @Override
    public void setGame(ContreeGame game) {
        this.contreeGame = game;
    }

    @Override
    public boolean hasNoMoreCard() {
        return hand.isEmpty();
    }

    public void receiveNewCards(Collection<ClassicalCard> cards) {

    }

    @Override
    public int nbAvailableCards() {
        return hand == null ? 0 : hand.size();
    }

    @Override
    public Collection<ClassicalCard> getHand() {
        return hand;
    }

    @Override
    public void setTeam(Team team) {
        if (team instanceof ContreeTeam ct) {
            this.team = ct;
        }
        else {
            throw new IllegalArgumentException("ContreeTeam expected");
        }
    }

    @Override
    public void onPlayerTurnToBid(Set<ContreeBidValue> allowedBidValues) {
        receiveNewMessage( new PlayerMessage( MessageType.BID, Collections.emptyList(), allowedBidValues ) );
    }

    @Override
    public ClassicalCard play() {
        return null;
    }

    @Override
    public void onPlayerTurn() {

    }

    @Override
    public void onPlayerTurn(Collection<ClassicalCard> allowedCards) {
        receiveNewMessage(new PlayerMessage(MessageType.PLAY, allowedCards, Collections.emptyList()));
    }

    @Override
    public Optional<ContreeTeam> getTeam() {
        return Optional.ofNullable(team);
    }

    @Override
    public boolean sameTeam(ContreePlayer otherPlayer) {
        return getTeam().orElseThrow() == otherPlayer.getTeam().orElseThrow();
    }

    @Override
    public void onGameStarted() {
        startPlayerThread();
    }

    @Override
    public void onGameOver() {
        receiveNewMessage( new PlayerMessage( MessageType.END_OF_GAME, null, null ) );
    }

    @Override
    protected boolean handleMessage(PlayerMessage playerMessage) {

        boolean mustExit = false;

        switch (playerMessage.messageType()) {
            case PLAY -> managePlayMessage(playerMessage);
            case BID -> manageBidMessage(playerMessage);
            case END_OF_GAME ->  mustExit = true;
        }
        return mustExit;
    }

    abstract void managePlayMessage(PlayerMessage playMessage);

    abstract void manageBidMessage(PlayerMessage bidMessage);

    protected ContreeGame getGame() {
        return contreeGame;
    }

}
