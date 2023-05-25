package sebastien.perpignane.cardgame.player.war.local.thread;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.war.WarGame;
import sebastien.perpignane.cardgame.player.AbstractThreadLocalPlayer;
import sebastien.perpignane.cardgame.player.PlayerState;
import sebastien.perpignane.cardgame.player.Team;
import sebastien.perpignane.cardgame.player.war.MessageType;
import sebastien.perpignane.cardgame.player.war.WarPlayer;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstracLocalThreadWarPlayer extends AbstractThreadLocalPlayer<MessageType, WarGame, Team> implements WarPlayer {

    private WarGame warGame;
    private PlayerState state;

    protected Deque<ClassicalCard> hand;

    private final List<ClassicalCard> cardStock = new CopyOnWriteArrayList<>();

    protected AbstracLocalThreadWarPlayer() {
        state = PlayerState.WAITING;
    }

    // FIXME -> send precise events to players, not a global "the game is updated"
    public void onUpdatedGame() {
        if (warGame.getCurrentPlayer() == this && state != PlayerState.PLAYING) {
            state = PlayerState.PLAYING;
            onPlayerTurn();
        }
        else {
            state = PlayerState.WAITING;
        }
    }

    public void onPlayerTurn() {
        receiveNewMessage(MessageType.PLAY);
    }

    @Override
    public void onGameOver() {
        receiveNewMessage(MessageType.END_OF_GAME);
    }

    @Override
    public void onGameEjection() {
        receiveNewMessage(MessageType.EJECTION);
    }

    @Override
    public void setGame(WarGame game) {
        this.warGame = game;
    }

    protected void play(ClassicalCard card) {
        warGame.play(this, card);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstracLocalThreadWarPlayer)) return false;
        return Objects.equals(warGame, ((AbstracLocalThreadWarPlayer)o).warGame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(warGame);
    }

    protected boolean gameIsOver() {
        return warGame.isOver();
    }

    protected boolean isCurrentPlayer() {
        return warGame.getCurrentPlayer() == this;
    }

    @Override
    protected boolean handleMessage(MessageType playerMessage) {

        boolean mustExit = false;

        switch (playerMessage) {
            case PLAY -> managePlayMessage(playerMessage);
            case END_OF_GAME, EJECTION -> mustExit = true;
            default -> throw new IllegalArgumentException("Unknown");
        }

        return mustExit;
    }

    protected void manageEmptyHandIfRelevant() {
        if (hand.isEmpty() && !cardStock.isEmpty()) {
            Collections.shuffle(cardStock);
            hand.addAll(cardStock);
            cardStock.clear();
        }
    }

    @Override
    public void removeCardFromHand(ClassicalCard card) {
        hand.remove(card);
    }

    abstract void managePlayMessage(MessageType playMessage);

    @Override
    public void onGameStarted() {
        startPlayerThread();
    }

    @Override
    public Collection<ClassicalCard> getHand() {
        return hand;
    }

    @Override
    public Optional<Team> getTeam() {
        return Optional.empty();
    }

    @Override
    public void setTeam(Team team) {

    }

    @Override
    public void playCard() {
        warGame.play(this, hand.getLast());
    }

    @Override
    public boolean hasNoMoreCard() {
        return (hand == null || hand.isEmpty()) && cardStock.isEmpty();
    }

    @Override
    public int nbAvailableCards() {
        return cardStock.size() + (hand == null ? 0 : hand.size());
    }

    @Override
    public void receiveHand(Collection<ClassicalCard> hand) {
        this.hand = new LinkedList<>(hand);
    }

    @Override
    public void receiveNewCards(Collection<ClassicalCard> cards) {
        cardStock.addAll(cards);
    }
}
