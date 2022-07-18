package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.player.Player;

import java.util.concurrent.BlockingQueue;

public class BlockingQueueGameObserver implements GameObserver {

    private final BlockingQueue<String> gameMessage;

    public BlockingQueueGameObserver(BlockingQueue<String> gameMessage) {
        this.gameMessage = gameMessage;
    }

    @Override
    public void onStateUpdated(GameState oldState, GameState newState) {

    }

    @Override
    public void onNextPlayer(Player p) {

    }

    @Override
    public void onWonTrick(Trick trick) {

    }

    @Override
    public void onEndOfGame(Game game) {
        gameMessage.add("END_OF_GAME");
    }

    @Override
    public void onCardPlayed(PlayedCard pc) {

    }
}
