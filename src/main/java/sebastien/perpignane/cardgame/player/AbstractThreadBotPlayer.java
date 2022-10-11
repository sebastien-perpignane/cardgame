package sebastien.perpignane.cardgame.player;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class AbstractThreadBotPlayer<T> implements Player, Runnable {

    private final BlockingQueue<T> gameMsgQueue = new ArrayBlockingQueue<>(54);

    @Override
    public boolean isBot() {
        return true;
    }

    @Override
    public void run() {
        while (true) {
            try {
                T playerMessage = gameMsgQueue.take();
                handleMessage(playerMessage);
            }
            catch (InterruptedException ie) {
                // FIXME review good practices to manage InterruptedException
                System.err.println("I'm interrupted");
                return;
            }
        }
    }

    public abstract String getName();

    protected void startBotPlayer() {
        var thread = new Thread(this, "PlayerThread-" + getName());
        thread.start();
    }

    protected abstract void handleMessage(T playerMessage);

    protected void receiveNewMessage(T message) {
        gameMsgQueue.add(message);
    }

}
