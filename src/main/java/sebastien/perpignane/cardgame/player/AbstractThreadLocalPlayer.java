package sebastien.perpignane.cardgame.player;

import sebastien.perpignane.cardgame.game.AbstractGame;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class AbstractThreadLocalPlayer<M, G extends AbstractGame<?>> implements Runnable, Player<G> {

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

    public abstract String getName();

    protected void startPlayerThread() {
        var thread = new Thread(this, "PlayerThread-" + getName());
        thread.start();
    }

    /**
     *
     * @return true if the message processing must result in the end of the thread.
     *
     */
    protected abstract boolean handleMessage(M playerMessage);

    protected void receiveNewMessage(M message) {
        gameMsgQueue.add(message);
    }

}
