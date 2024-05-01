package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.game.contree.ContreeGame;

import java.util.*;

public class ContreePlayerImpl implements ContreePlayer {

    private final String id;

    private ContreePlayerStatus status;

    private final ContreePlayerEventHandler playerEventHandler;

    private final String name;

    private Collection<ClassicalCard> hand = new ArrayList<>();

    private ContreeGame game;

    private ContreeTeam team;

    public ContreePlayerImpl(String name, ContreePlayerEventHandler playerEventHandler) {
        id = UUID.randomUUID().toString();
        this.name = name;
        this.playerEventHandler = playerEventHandler;
        this.playerEventHandler.setPlayer(this);
        this.status = ContreePlayerStatus.WAITING;
    }

    @Override
    public void receiveHand(Collection<ClassicalCard> cards) {
        this.hand = cards;
        playerEventHandler.onReceivedHand(hand);
    }

    @Override
    public void onUpdatedGame() {
        // finer grain events are used
    }

    @Override
    public void setGame(ContreeGame game) {
        this.game = game;
    }

    @Override
    public boolean hasNoMoreCard() {
        return hand == null || hand.isEmpty();
    }

    @Override
    public void receiveNewCards(Collection<ClassicalCard> cards) {
        throw new UnsupportedOperationException("ContreePlayer can receive new full hand but not new cards");
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
    public void onGameEjection() {
        playerEventHandler.onEjection();
    }

    @Override
    public int nbAvailableCards() {
        return hand == null ? 0 : hand.size();
    }

    @Override
    public Collection<ClassicalCard> getHand() {
        return Collections.unmodifiableCollection(hand);
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
    public boolean sameTeam(sebastien.perpignane.cardgame.player.contree.ContreePlayer otherPlayer) {
        if (team == null) {
            return false;
        }
        return otherPlayer.getTeam()
                .map(otherPlayerTeam -> otherPlayerTeam == team)
                .orElse(false);
    }

    @Override
    public boolean isBot() {
        return playerEventHandler.isBot();
    }

    @Override
    public void playCard(ClassicalCard card) {
        game.playCard(this, card);
    }

    @Override
    public void placeBid(ContreeBidValue bidValue, CardSuit cardSuit) {
        game.placeBid(this, bidValue, cardSuit);
    }

    @Override
    public void leaveGame() {
        game.leaveGame(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setWaiting() {
        updateStatus(ContreePlayerStatus.WAITING);
    }

    @Override
    public void setBidding() {
        updateStatus(ContreePlayerStatus.BIDDING);
    }

    @Override
    public void setPlaying() {
        updateStatus(ContreePlayerStatus.PLAYING);
    }

    private void updateStatus(ContreePlayerStatus status) {
        var oldStatus = this.status;
        this.status = status;
        if (oldStatus != this.status) {
            playerEventHandler.onStatusUpdate(oldStatus, status);
        }
    }

    @Override
    public boolean isWaiting() {
        return status == ContreePlayerStatus.WAITING;
    }

    @Override
    public boolean isBidding() {
        return status == ContreePlayerStatus.BIDDING;
    }

    @Override
    public boolean isPlaying() {
        return status == ContreePlayerStatus.PLAYING;
    }

    @Override
    public String toString() {
        String formatString = isBot() ? "%s-Bot (%s)" : "* %s (%s) *";
        return String.format(formatString, getName(), team);
    }

    @Override
    public ContreePlayerState toState() {
        return new ContreePlayerState(getName(), status);
    }

    @Override
    public FullContreePlayerState toFullState() {
        return new FullContreePlayerState(getName(), status, id, hand);
    }

}
