package sebastien.perpignane.cardgame.player.contree.event.handler;

import com.github.javafaker.Faker;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.game.contree.ContreeGame;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class ContreePlayerEventHandlerImpl implements ContreePlayer {

    private final static Faker faker = new Faker();

    private final ContreePlayerEventHandler playerEventHandler;
    private final String name;

    private Collection<ClassicalCard> hand;

    private ContreeGame game;

    private ContreeTeam team;

    public ContreePlayerEventHandlerImpl(String name, ContreePlayerEventHandler playerEventHandler) {
        this.name = name;
        this.playerEventHandler = playerEventHandler;
        this.playerEventHandler.setPlayer(this);
    }

    public ContreePlayerEventHandlerImpl(ContreePlayerEventHandler playerEventHandler) {
        this(faker.name().firstName(), playerEventHandler);
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
    public boolean sameTeam(sebastien.perpignane.cardgame.player.contree.ContreePlayer otherPlayer) {
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
    public String toString() {
        String formatString = isBot() ? "%s-Bot (%s)" : "* %s (%s) *";
        return String.format(formatString, getName(), team);
    }
}
