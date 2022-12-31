package sebastien.perpignane.cardgame.player.contree.local.thread;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.game.contree.ContreeGame;
import sebastien.perpignane.cardgame.player.AbstractThreadLocalPlayer;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;
import sebastien.perpignane.cardgame.player.contree.MessageType;
import sebastien.perpignane.cardgame.player.contree.PlayerMessage;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public abstract class AbstractLocalThreadContreePlayer extends AbstractThreadLocalPlayer<PlayerMessage, ContreeGame, ContreeTeam> implements ContreePlayer {

    private ContreeTeam team;

    private Collection<ClassicalCard> hand;

    private ContreeGame contreeGame;

    @Override
    public void receiveHand(Collection<ClassicalCard> cards) {
        this.hand = cards;
    }

    @Override
    public void removeCardFromHand(ClassicalCard card) {
        hand.remove(card);
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
        throw new UnsupportedOperationException("receiveNewCards does not make sense for Contree players");
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
    public void setTeam(ContreeTeam team) {
        this.team = team;
    }

    @Override
    public void onPlayerTurnToBid(Set<ContreeBidValue> allowedBidValues) {
        receiveNewMessage( new PlayerMessage( MessageType.BID, Collections.emptyList(), allowedBidValues ) );
    }

    @Override
    public void onPlayerTurn() {

    }

    @Override
    public void onPlayerTurn(Set<ClassicalCard> allowedCards) {
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
        receiveNewMessage( new PlayerMessage( MessageType.GAME_OVER) );
    }

    @Override
    public void onGameEjection() {
        receiveNewMessage(new PlayerMessage(MessageType.EJECTED));
    }

    @Override
    protected boolean handleMessage(PlayerMessage playerMessage) {

        boolean mustExit = false;

        switch (playerMessage.messageType()) {
            case PLAY -> managePlayMessage(playerMessage);
            case BID -> manageBidMessage(playerMessage);
            case GAME_OVER, EJECTED ->  mustExit = true;
            case GAME_STARTED -> {
                // Managed by the onGameStarted method
            }
            default -> throw new IllegalArgumentException( String.format( "Unknown message type: %s", playerMessage.messageType() ) );
        }
        return mustExit;
    }

    abstract void managePlayMessage(PlayerMessage playMessage);

    abstract void manageBidMessage(PlayerMessage bidMessage);

    protected ContreeGame getGame() {
        return contreeGame;
    }

}
