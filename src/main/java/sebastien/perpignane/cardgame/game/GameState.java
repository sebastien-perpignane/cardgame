package sebastien.perpignane.cardgame.game;

enum GameState {
    NOT_INITIALIZED(false),
    INITIALIZED(false),
    STARTING(false),
    STARTED(true),
    OVER(false);

    private final boolean playable;

    GameState(boolean playable) {
        this.playable = playable;
    }

    public boolean isPlayable() {
        return playable;
    }

}
