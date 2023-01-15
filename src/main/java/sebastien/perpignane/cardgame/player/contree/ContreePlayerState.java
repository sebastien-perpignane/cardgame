package sebastien.perpignane.cardgame.player.contree;

public class ContreePlayerState {

    private final String name;

    private final ContreePlayerStatus status;

    public ContreePlayerState(String name, ContreePlayerStatus status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public ContreePlayerStatus getStatus() {
        return status;
    }
}


