package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.game.PlayedCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

final class ContreePlayedCard extends PlayedCard<ContreePlayer, ContreeCard> {

    public ContreePlayedCard(ContreePlayer player, ContreeCard card) {
        super(player, card);
        if (player.getTeam().isEmpty()) {
            throw new IllegalArgumentException("player must be part of a team");
        }
    }

}
