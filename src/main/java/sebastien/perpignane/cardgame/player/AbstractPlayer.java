package sebastien.perpignane.cardgame.player;

import sebastien.perpignane.cardgame.game.Game;
import sebastien.perpignane.cardgame.game.PlayedCard;

import java.util.Objects;



public abstract class AbstractPlayer implements Player {

    private Game game;
    private PlayerState state;

    public AbstractPlayer() {
        state = PlayerState.WAITING;
    }

    // FIXME -> send precise events to players, not a global "the game is updated"
    public synchronized void onUpdatedGame() {
        if (game.getCurrentPlayer() == this && state != PlayerState.PLAYING) {
            state = PlayerState.PLAYING;
            onPlayerTurn();
        }
        else {
            state = PlayerState.WAITING;
        }
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    protected void play(PlayedCard pc) {
        game.play(pc);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractPlayer that)) return false;
        return Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(game);
    }

    boolean gameIsOver() {
        return game.isOver();
    }

    boolean isCurrentPlayer() {
        return game.getCurrentPlayer() == this;
    }

}
