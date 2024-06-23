package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.player.contree.handlers.ContreeBotPlayerEventHandler;

public class ContreePlayerFactory {
    
    private ContreePlayerFactory() {}
    
    public static ContreePlayer createBotPlayer(String name) {
        return new ContreePlayerImpl("Bot " + name, new ContreeBotPlayerEventHandler());
    }
}