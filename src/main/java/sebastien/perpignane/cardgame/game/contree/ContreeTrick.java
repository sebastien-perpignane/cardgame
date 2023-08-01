package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.game.Trick;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;
import sebastien.perpignane.cardgame.player.util.PlayerSlot;

import java.util.*;
import java.util.stream.Collectors;

class ContreeTrick implements Trick<ContreePlayer, ContreePlayedCard, ContreeTeam> {

    private final String trickId;

    private final List<ContreePlayedCard> playedCards = new ArrayList<>();

    private ContreeCard firstPlayedCard = null;

    private final CardSuit trumpSuit;

    private ContreePlayer winner;

    private final ContreeGameEventSender eventSender;

    private final ContreeTrickPlayers trickPlayers;

    private final PlayableCardsFilter playableCardsFilter;

    private PlayerSlot<ContreePlayer> currentPlayerSlot = new PlayerSlot<>();

    public ContreeTrick(ContreeDeal deal, String trickId, ContreeTrickPlayers trickPlayers, PlayableCardsFilter playableCardsFilter) {
        this.trickId = trickId;
        this.eventSender = deal.getEventSender();
        this.trumpSuit = deal.getTrumpSuit();
        this.trickPlayers = trickPlayers;
        this.playableCardsFilter = playableCardsFilter;
    }

    void startTrick() {
        trickPlayers.setCurrentTrick(this);
        configureCurrentPlayer();
    }

    private void nextPlayer() {
        trickPlayers.gotToNextPlayer();
        configureCurrentPlayer();
    }

    private void configureCurrentPlayer() {
        currentPlayerSlot = trickPlayers.getCurrentPlayerSlot();
        trickPlayers.notifyCurrentPlayerTurn(
                playableCardsFilter.playableCards(this, currentPlayerSlot.getPlayer().orElseThrow())
        );
    }

    boolean isTrumpTrick() {
        return this.firstPlayedCard.isTrump();
    }

    void playerPlays(ContreePlayer player, ClassicalCard card) {

        throwExceptionIfInvalidPlayedCard(player, card);

        ContreePlayedCard playedCard = new ContreePlayedCard(player, new ContreeCard(card, trumpSuit));

        if (firstPlayedCard == null) {
            firstPlayedCard = playedCard.card();
        }
        else {
            if (!isTrumpTrick() && playedCard.card().isTrump()) {
                eventSender.sendTrumpedTrickEvent(this.trickId);
            }
        }
        playedCards.add(playedCard);
        player.removeCardFromHand(card);
        if (isOver()) {
            winner = winningPlayer();
            currentPlayerSlot = new PlayerSlot<>(-1, null);
        }
        else {
            nextPlayer();
        }
    }

    private void throwExceptionIfInvalidPlayedCard(ContreePlayer player, ClassicalCard card) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(card);

        if (isOver()) {
            throw new IllegalStateException(String.format("Cheater detected : trick is over, player %s cannot play%n", player));
        }

        if (player != currentPlayerSlot.getPlayer().orElseThrow()) {
            throw new IllegalArgumentException( String.format("Cheater detected -> %s is not current player!. Current player is %s%n", player, currentPlayerSlot.getPlayer().orElseThrow()) );
        }

        var currentPlayerPlayableCards = playableCardsFilter.playableCards(this, player);

        if (!currentPlayerPlayableCards.contains(card) ) {
            String allowedCardsStr = currentPlayerPlayableCards.stream().map( ClassicalCard::toString ).collect( Collectors.joining(",") );
            throw new IllegalArgumentException( String.format("Player %s : cheater detected -> %s is not an allowed card. Allowed cards are : %s", player, card, allowedCardsStr) );
        }
    }

    @Override
    public boolean isOver() {
        return playedCards.size() == 4;
    }

    @Override
    public Optional<ContreePlayer> getWinner() {
        return Optional.ofNullable(winner);
    }

    @Override
    public Optional<ContreeTeam> getWinnerTeam() {
        return winner.getTeam();
    }

    public Set<ClassicalCard> getAllCards() {
        return playedCards.stream().map(pc -> pc.card().getCard()).collect(Collectors.toSet());
    }

    public CardSuit getTrumpSuit() {
        return trumpSuit;
    }

    ContreePlayer winningPlayer() {
        if (playedCards.isEmpty()) {
            return null;
        }
        CardSuit wantedSuit = firstPlayedCard.getSuit();
        return playedCards.stream()
                .filter(pc -> pc.card().getSuit() == wantedSuit || pc.card().getSuit() == trumpSuit)
                .max(Comparator.comparingInt(a -> a.card().getGameValue()))
                .orElseThrow(() -> new IllegalStateException("At least one card must be found as playedCards is not empty"))
                .player();
    }

    @Override
    public List<ContreePlayedCard> getPlayedCards() {
        return playedCards;
    }

    public String getTrickId() {
        return trickId;
    }

    public Optional<ContreePlayer> getCurrentPlayer() {
        return currentPlayerSlot.getPlayer();
    }

    public Collection<ContreeCard> getPlayerHand(ContreePlayer player) {
        return ContreeCard.of( trumpSuit, new HashSet<>(player.getHand()) );
    }

    @Override
    public String toString() {
        return "ContreeTrick{" +
                "trickId='" + trickId + '\'' +
                '}';
    }

}
