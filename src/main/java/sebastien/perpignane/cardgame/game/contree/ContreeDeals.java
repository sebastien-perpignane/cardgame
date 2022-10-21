package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.ArrayList;
import java.util.List;

public class ContreeDeals {

    private final ContreeGame game;

    private final ContreeDealPlayers dealPlayers;

    private final List<ContreeDeal> deals = new ArrayList<>();

    private ContreeDeal currentDeal;

    private final ContreeGameEventSender gameEventSender;

    public ContreeDeals(ContreeGame game, ContreeDealPlayers contreeDealPlayers, ContreeGameEventSender gameEventSender) {
        this.game = game;
        this.dealPlayers = contreeDealPlayers;
        this.gameEventSender = gameEventSender;
    }

    public void createAndStartNewDeal() {

        if (game.isOver()) {
            return;
        }

        currentDeal = new ContreeDeal(dealId(), dealPlayers, gameEventSender);
        deals.add(currentDeal);

        dealPlayers.setCurrentDeal(currentDeal);

        currentDeal.startDeal();
    }

    public void placeBid(ContreePlayer player, ContreeBidValue bidValue, CardSuit cardSuit) {
        currentDeal.placeBid(new ContreeBid(player, bidValue, cardSuit));
        if (currentDeal.isOver()) {
            createAndStartNewDeal();
        }
    }

    public void playCard(ContreePlayer player, ClassicalCard card) {
        currentDeal.playerPlays(player, card);
    }

    public ContreeDeal getCurrentDeal() {
        return currentDeal;
    }

    public boolean isCurrentDealOver() {
        return currentDeal.isOver();
    }

    private String dealId() {
        return game.getGameId() + "-" + (deals.size() + 1);
    }


}
