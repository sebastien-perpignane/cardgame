package sebastien.perpignane.cardgame.player.war;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.war.WarGame;
import sebastien.perpignane.cardgame.player.AbstractThreadLocalPlayer;
import sebastien.perpignane.cardgame.player.PlayerState;
import sebastien.perpignane.cardgame.player.Team;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;


public abstract class AbstractWarPlayer implements Player<WarGame> {

    private WarGame warGame;
    private PlayerState state;

    public AbstracLocalThreadWarPlayer() {
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
            case END_OF_GAME -> mustExit = true;
            default -> throw new IllegalArgumentException("Unknown");
        }

        return mustExit;
    }

    abstract void managePlayMessage(MessageType playMessage);

    @Override
    public void receiveHand(Collection<ClassicalCard> cards) {

    }

    @Override
    public boolean hasNoMoreCard() {
        return false;
    }

    @Override
    public void receiveNewCards(Collection<ClassicalCard> cards) {

    }

    @Override
    public void onGameStarted() {
        startPlayerThread();
    }

    @Override
    public int nbAvailableCards() {
        return 0;
    }

    @Override
    public Collection<ClassicalCard> getHand() {
        return null;
    }

    @Override
    public void removeCardFromHand(ClassicalCard card) {

    }

    @Override
    public Optional<Team> getTeam() {
        return Optional.empty();
    }

    @Override
    public void setTeam(Team team) {

    }
}
