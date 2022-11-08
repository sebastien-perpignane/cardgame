package sebastien.perpignane.cardgame.player.war;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.war.WarGame;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.PlayerState;

import java.util.Objects;


public abstract class AbstractWarPlayer implements Player<WarGame> {

    private WarGame warGame;
    private PlayerState state;

    public AbstractWarPlayer() {
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
        if (!(o instanceof AbstractWarPlayer)) return false;
        return Objects.equals(warGame, ((AbstractWarPlayer)o).warGame);
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

}
