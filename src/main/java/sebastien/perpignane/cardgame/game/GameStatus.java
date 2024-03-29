package sebastien.perpignane.cardgame.game;

public enum GameStatus {
    NOT_INITIALIZED(false),
    INITIALIZED(false),
    STARTING(false),
    STARTED(true),
    WAITING_FOR_PLAYERS(false),
    OVER(false);

    private final boolean playable;

    GameStatus(boolean playable) {
        this.playable = playable;
    }

    public boolean isPlayable() {
        return playable;
    }

}
