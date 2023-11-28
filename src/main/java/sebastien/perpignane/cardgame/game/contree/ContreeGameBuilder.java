package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardDealer;

public class ContreeGameBuilder {

    private ContreeGameBuilder() {
    }

    public static ContreeGame createGame(ContreeGameConfig gameConfig) {
        ContreeGameEventSender eventSender = new ContreeGameEventSender();
        ContreeGamePlayers players = new ContreeGamePlayersImpl();
        ContreeGameScore gameScore = new ContreeGameScore(gameConfig.getMaxScore());
        PlayableCardsFilter playableCardsFilter = new PlayableCardsFilter();
        BiddableValuesFilter biddableValuesFilter = new BiddableValuesFilter();
        DealScoreCalculator scoreCalculator = new DealScoreCalculator();
        CardDealer cardDealer = new CardDealer(gameConfig.getDistributionConfiguration());
        ContreeDeals deals = new ContreeDeals(gameScore, scoreCalculator, biddableValuesFilter, playableCardsFilter, cardDealer, eventSender);
        return new ContreeGame(players, deals, eventSender);
    }

}
