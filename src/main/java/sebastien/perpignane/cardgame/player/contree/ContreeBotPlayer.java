package sebastien.perpignane.cardgame.player.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.AbstractGame;
import sebastien.perpignane.cardgame.game.contree.ContreeBidValue;
import sebastien.perpignane.cardgame.game.contree.ContreeGame;
import sebastien.perpignane.cardgame.player.AbstractThreadBotPlayer;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.Team;

import java.util.*;
import java.util.stream.Collectors;


public class ContreeBotPlayer extends AbstractThreadBotPlayer<ContreeBotPlayer.PlayerMessage> implements ContreePlayer, Runnable {

    private final String name;

    private enum MessageType {
        PLAY,
        BID,
        END_OF_GAME
    }

    public static class PlayerMessage {

        private final MessageType messageType;

        private final Collection<ClassicalCard> allowedCards;

        public PlayerMessage(MessageType messageType, Collection<ClassicalCard> allowedCards) {
            this.messageType = messageType;
            this.allowedCards = allowedCards;
        }

        public Collection<ClassicalCard> getAllowedCards() {
            return allowedCards;
        }

        public MessageType getMessageType() {
            return messageType;
        }
    }

    private static final Deque<String> fakeNames = new LinkedList<>();

    private static void addFakeNames() {
        fakeNames.add("Yanis");
        fakeNames.add("Matis");
        fakeNames.add("Noé");
        fakeNames.add("Sébastien");
        fakeNames.add("Imène");
        fakeNames.add("Enzo");
    }

    static {
        addFakeNames();
    }

    private ContreeTeam team;

    private Collection<ClassicalCard> hand;

    private ContreeGame contreeGame;

    public ContreeBotPlayer() {
        //gameMsgQueue = new ArrayBlockingQueue<>(54);
        this.name = fakeNames.isEmpty() ? UUID.randomUUID().toString() : fakeNames.pop();
    }

    @Override
    public void receiveHand(Collection<ClassicalCard> cards) {
        this.hand = cards;
    }

    @Override
    public void onUpdatedGame() {

    }

    @Override
    public void setGame(AbstractGame game) {
        this.contreeGame = (ContreeGame) game;
    }

    @Override
    public boolean hasNoMoreCard() {
        return hand.isEmpty();
    }

    @Override
    public void receiveNewCards(Collection<ClassicalCard> cards) {

    }

    @Override
    public void onGameStarted() {
        startBotPlayer();
    }

    @Override
    public void onGameOver() {
        receiveNewMessage(new PlayerMessage(MessageType.END_OF_GAME, null));
    }

    @Override
    public ClassicalCard play() {
        return null;
    }

    @Override
    public void onPlayerTurn() {
        // do nothing
    }

    @Override
    public void onPlayerTurn(Collection<ClassicalCard> allowedCards) {
        receiveNewMessage(new PlayerMessage(MessageType.PLAY, allowedCards));
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
    public Optional<ContreeTeam> getTeam() {
        return Optional.ofNullable(team);
    }

    @Override
    public void setTeam(Team team) {

        if (team instanceof ContreeTeam ct) {
            this.team = ct;
        }
        else {
            throw new IllegalArgumentException("ContreeTeam expected");
        }
    }

    @Override
    public void onPlayerTurnToBid() {
        receiveNewMessage(new PlayerMessage(MessageType.BID, Collections.emptyList()));
    }

    @Override
    public boolean sameTeam(Player otherPlayer) {
        return getTeam().orElseThrow() == otherPlayer.getTeam().orElseThrow();
    }

    protected void placeBid() {
        placeBid(ContreeBidValue.NONE, null);
    }

    protected final void placeBid(ContreeBidValue bidValue, CardSuit cardSuit) {
        contreeGame.placeBid(this, bidValue, cardSuit);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    protected void handleMessage(PlayerMessage playerMessage) {
        switch (playerMessage.getMessageType()) {
            case PLAY -> {
                System.out.printf("Allowed cards : %s%n", playerMessage.getAllowedCards().stream().map(ClassicalCard::toString).collect(Collectors.joining(",")));
                var playedCard = playerMessage.getAllowedCards().iterator().next();
                hand.remove(playedCard);
                contreeGame.playCard(this, playedCard);
            }
            case BID -> {
                System.out.printf("%s reacting to BID event%n", this);
                placeBid();
            }
            case END_OF_GAME -> System.out.printf("%s reacting to END_OF_GAME event%n", this);
        }
    }

    @Override
    public String toString() {
        return "ContreeBotPlayer{name=" + name + "-BOT, " +
                "team=" + team +'}';
    }

}
