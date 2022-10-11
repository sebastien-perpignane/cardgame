package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

record PlayedCard(ContreePlayer player, ContreeCard card) {

    public PlayedCard {
        if (player == null) {
            throw new IllegalArgumentException("player cannot be null");
        }
        if (player.getTeam().isEmpty()) {
            throw new IllegalArgumentException("player must be part of a team");
        }
        if (card == null) {
            throw new IllegalArgumentException("card cannot be null");
        }
    }

}
