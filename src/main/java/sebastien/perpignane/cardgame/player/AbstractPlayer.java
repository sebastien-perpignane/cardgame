package sebastien.perpignane.cardgame.player;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGame;
import sebastien.perpignane.cardgame.game.war.WarGame;

import java.util.Objects;


/**
 * FIXME WarGame cannot be referenced in AbstractPlayer
 */
public abstract class AbstractPlayer implements Player {

    private WarGame warGame;
    private PlayerState state;

    public AbstractPlayer() {
        state = PlayerState.WAITING;
    }

    // FIXME -> send precise events to players, not a global "the game is updated"
    public synchronized void onUpdatedGame() {
        if (warGame.getCurrentPlayer() == this && state != PlayerState.PLAYING) {
            state = PlayerState.PLAYING;
            onPlayerTurn();
        }
        else {
            state = PlayerState.WAITING;
        }
    }

    @Override
    public void setGame(AbstractGame game) {
        this.warGame = (WarGame) game;
    }


    protected void play(ClassicalCard card) {
        warGame.play(this, card);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPlayer)) return false;
        return Objects.equals(warGame, ((AbstractPlayer)o).warGame);
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
