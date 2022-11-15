package sebastien.perpignane.cardgame.player.contree.refactor;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.game.contree.ContreeGame;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class RefactoredContreePlayer implements ContreePlayer {

    private final PlayerEventHandler<ContreePlayer, ContreeGame> playerEventHandler;

    private Collection<ClassicalCard> hand;

    private ContreeGame game;

    private ContreeTeam team;

    public RefactoredContreePlayer(PlayerEventHandler<ContreePlayer, ContreeGame> playerEventHandler) {
        this.playerEventHandler = playerEventHandler;
        this.playerEventHandler.setPlayer(this);
    }

    @Override
    public void receiveHand(Collection<ClassicalCard> cards) {
        this.hand = cards;
    }

    @Override
    public void onUpdatedGame() {

    }

    @Override
    public void setGame(ContreeGame game) {
        this.game = game;
        playerEventHandler.setGame(game);
    }

    @Override
    public boolean hasNoMoreCard() {
        return hand == null || hand.isEmpty();
    }

    @Override
    public void receiveNewCards(Collection<ClassicalCard> cards) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void onGameStarted() {
        playerEventHandler.onGameStarted();
    }

    @Override
    public void onGameOver() {
        playerEventHandler.onGameOver();
    }

    @Override
    public void onPlayerTurn() {

    }

    @Override
    public int nbAvailableCards() {
        return hand == null ? 0 : hand.size();
    }

    @Override
    public Collection<ClassicalCard> getHand() {
        return hand;
    }

    @Override
    public void removeCardFromHand(ClassicalCard card) {
        hand.remove(card);
    }

    @Override
    public Optional<ContreeTeam> getTeam() {
        return Optional.ofNullable(team);
    }

    @Override
    public void setTeam(ContreeTeam team) {
        this.team = team;
    }

    @Override
    public void onPlayerTurnToBid(Set<ContreeBidValue> allowedBidValues) {
        playerEventHandler.onPlayerTurnToBid(allowedBidValues);
    }

    @Override
    public void onPlayerTurn(Set<ClassicalCard> allowedCards) {
        playerEventHandler.onPlayerTurn(allowedCards);
    }

    @Override
    public boolean sameTeam(ContreePlayer otherPlayer) {
        if (team == null || otherPlayer.getTeam().isEmpty()) {
            return false;
        }
        return team == otherPlayer.getTeam().get();
    }

    @Override
    public boolean isBot() {
        return playerEventHandler.isBot();
    }

    @Override
    public void playCard(ClassicalCard card) {
        game.playCard(this, card);
    }
}
