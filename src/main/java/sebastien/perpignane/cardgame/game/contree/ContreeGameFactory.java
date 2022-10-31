package sebastien.perpignane.cardgame.game.contree;

public class ContreeGameFactory {

    public static ContreeGame createGame() {

        ContreeGameEventSender eventSender = new ContreeGameEventSender();
        ContreeGamePlayers players = new ContreeGamePlayersImpl();
        ContreeGameScore gameScore = new ContreeGameScore(1000);
        PlayableCardsFilter playableCardsFilter = new PlayableCardsFilter();
        DealScoreCalculator scoreCalculator = new DealScoreCalculator();
        ContreeDeals deals = new ContreeDeals(gameScore, scoreCalculator, playableCardsFilter, eventSender);
        return new ContreeGame(players, deals, eventSender);

    }
}
