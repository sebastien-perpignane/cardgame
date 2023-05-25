package sebastien.perpignane.cardgame.player.contree.handlers;

import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.contree.PlayerEventHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.lang.System.err;

public abstract class AbstractThreadPlayerEventHandler<P extends Player<?, ?>, M> implements PlayerEventHandler<P>, Runnable {

    private final BlockingQueue<M> gameMsgQueue = new ArrayBlockingQueue<>(54);

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
                err.println("I'm interrupted");
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    protected abstract boolean handleMessage(M playerMessage);

    protected abstract String getName();

    protected void receiveNewMessage(M message) {
        gameMsgQueue.add(message);
    }

    protected void startPlayerEventHandlerThread() {
        var thread = new Thread(this, "PlayerThread-" + getName());
        thread.start();
    }
}
