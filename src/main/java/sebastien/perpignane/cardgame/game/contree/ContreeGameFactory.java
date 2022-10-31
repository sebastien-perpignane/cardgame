package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardDealer;

public class ContreeGameFactory {

    public static ContreeGame createGame(Integer maxScore) {

        ContreeGameConfig config = new ContreeGameConfig();
        ContreeGameEventSender eventSender = new ContreeGameEventSender();
        ContreeGamePlayers players = new ContreeGamePlayersImpl();
        ContreeGameScore gameScore = new ContreeGameScore(maxScore == null ? config.maxScore() : maxScore);
        PlayableCardsFilter playableCardsFilter = new PlayableCardsFilter();
        DealScoreCalculator scoreCalculator = new DealScoreCalculator();
        CardDealer cardDealer = new CardDealer(config.distributionConfiguration());
        ContreeDeals deals = new ContreeDeals(gameScore, scoreCalculator, playableCardsFilter, cardDealer, eventSender);
        return new ContreeGame(players, deals, eventSender);

    }

    public static ContreeGame createGame() {
        return createGame(null);
    }

}
