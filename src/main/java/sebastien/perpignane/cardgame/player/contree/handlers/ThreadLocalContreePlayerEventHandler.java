package sebastien.perpignane.cardgame.player.contree.handlers;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreePlayerEventHandler;
import sebastien.perpignane.cardgame.player.contree.MessageType;
import sebastien.perpignane.cardgame.player.contree.PlayerMessage;

import java.util.Set;

public abstract class ThreadLocalContreePlayerEventHandler extends AbstractThreadPlayerEventHandler<ContreePlayer, PlayerMessage> implements ContreePlayerEventHandler {

    private ContreePlayer player;

    protected ContreePlayer getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(ContreePlayer player) {
        this.player = player;
    }

    public ThreadLocalContreePlayerEventHandler() {
    }

    @Override
    public void onGameOver() {
        receiveNewMessage(new PlayerMessage(MessageType.GAME_OVER));
    }

    @Override
    public void onGameStarted() {
        startPlayerEventHandlerThread();
    }

    @Override
    public void onPlayerTurnToBid(Set<ContreeBidValue> allowedBidValues) {
        receiveNewMessage(new PlayerMessage(MessageType.BID, null, allowedBidValues));
    }

    @Override
    public void onPlayerTurn(Set<ClassicalCard> allowedCards) {
        receiveNewMessage(new PlayerMessage(MessageType.PLAY, allowedCards, null));
    }

    @Override
    public void onEjection() {
        receiveNewMessage(new PlayerMessage(MessageType.EJECTED));
    }

    @Override
    protected boolean handleMessage(PlayerMessage playerMessage) {
        boolean mustExit = false;

        switch (playerMessage.messageType()) {
            case PLAY -> managePlayMessage(playerMessage);
            case BID -> manageBidMessage(playerMessage);
            case GAME_OVER, EJECTED ->  mustExit = true;
            case GAME_STARTED -> {
                // Directly managed by the onGameStarted method, to start the thread
            }
            default -> throw new IllegalArgumentException( String.format( "Unknown message type: %s", playerMessage.messageType() ) );
        }
        return mustExit;
    }

    abstract void managePlayMessage(PlayerMessage playMessage);

    abstract void manageBidMessage(PlayerMessage bidMessage);

    @Override
    protected String getName() {
        return getPlayer().getName();
    }
}
