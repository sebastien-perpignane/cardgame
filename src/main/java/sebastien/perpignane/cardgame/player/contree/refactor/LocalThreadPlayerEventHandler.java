package sebastien.perpignane.cardgame.player.contree.refactor;

import sebastien.perpignane.cardgame.game.AbstractGame;
import sebastien.perpignane.cardgame.player.Player;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class LocalThreadPlayerEventHandler<P extends Player<G, ?>, G extends AbstractGame<P>, M> implements PlayerEventHandler<P, G>, Runnable {

    private final BlockingQueue<M> gameMsgQueue = new ArrayBlockingQueue<>(54);

    private G game;

    @Override
    public void run() {
        while (true) {
            try {
                M playerMessage = gameMsgQueue.take();
                if (handleMessage(playerMessage)) {
                    return;
                }
            }
            catch (InterruptedException ie) {
                // TODO review good practices to manage InterruptedException
                System.err.println("I'm interrupted");
                return;
            }
        }
    }

    protected abstract boolean handleMessage(M playerMessage);

    protected void receiveNewMessage(M message) {
        gameMsgQueue.add(message);
    }

    protected void startPlayerEventHandlerThread() {
        // FIXME find a way to name the thread properly
        var thread = new Thread(this, "PlayerThread-prout");
        thread.start();
    }

    protected G getGame() {
        return game;
    }

    @Override
    public void setGame(G game) {
        this.game = game;
    }
}
