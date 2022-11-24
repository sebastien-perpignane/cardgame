package sebastien.perpignane.cardgame.player.contree.event.handler.handlers;

import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.event.handler.PlayerEventHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class ThreadPlayerEventHandler<P extends Player<?, ?>, M> implements PlayerEventHandler<P>, Runnable {

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
                System.err.println("I'm interrupted");
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
        // FIXME find a way to name the thread properly
        var thread = new Thread(this, "PlayerThread-" + getName());
        thread.start();
    }
}
