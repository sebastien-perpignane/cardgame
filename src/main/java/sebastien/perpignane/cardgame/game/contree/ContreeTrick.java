package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.game.Trick;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.*;
import java.util.stream.Collectors;

class ContreeTrick implements Trick {

    private final String trickId;

    private final List<PlayedCard> playedCards = new ArrayList<>();

    private ContreeCard firstPlayedCard = null;

    private Collection<ClassicalCard> currentPlayerPlayableCards;

    private final CardSuit trumpSuit;

    private ContreePlayer winner;

    private final ContreeGameEventSender eventSender;

    private final ContreeTrickPlayers trickPlayers;

    private final PlayableCardsFilter playableCardsFilter;

    private ContreePlayer currentPlayer;

    public ContreeTrick(String trickId, ContreeTrickPlayers trickPlayers, CardSuit trumpSuit,  ContreeGameEventSender eventSender) {
        this.trickId = trickId;
        this.eventSender = eventSender;
        this.trumpSuit = trumpSuit;
        this.trickPlayers = trickPlayers;
        this.playableCardsFilter = new PlayableCardsFilter();
    }

    public ContreeTrick(ContreeDeal deal, String trickId, ContreeTrickPlayers trickPlayers, PlayableCardsFilter playableCardsFilter) {
        this.trickId = trickId;
        this.eventSender = deal.getEventSender();
        this.trumpSuit = deal.getTrumpSuit();
        this.trickPlayers = trickPlayers;
        this.playableCardsFilter = playableCardsFilter;
    }

    public void startTrick() {
        trickPlayers.setCurrentTrick(this);
        currentPlayer = trickPlayers.getCurrentPlayer();
        currentPlayerPlayableCards = this.playableCardsFilter.playableCards(this, currentPlayer);
        trickPlayers.notifyCurrentPlayerTurn(currentPlayerPlayableCards);
    }

    private void updateCurrentPlayer() {
        trickPlayers.gotToNextPlayer();
        currentPlayer = trickPlayers.getCurrentPlayer();
        currentPlayerPlayableCards = this.playableCardsFilter.playableCards(this, currentPlayer);
        trickPlayers.notifyCurrentPlayerTurn(currentPlayerPlayableCards);
    }

    public boolean isTrumpTrick() {
        return this.firstPlayedCard.isTrump();
    }

    public void playerPlays(ContreePlayer player, ClassicalCard card) {

        throwExceptionIfInvalidPlayedCard(player, card);

        var playedCard = new PlayedCard(player, new ContreeCard(card, trumpSuit));

        if (firstPlayedCard == null) {
            firstPlayedCard = playedCard.card();
        }
        else {
            if (!isTrumpTrick() && playedCard.card().isTrump()) {
                eventSender.sendTrumpedTrickEvent(this.trickId);
            }
        }
        playedCards.add(playedCard);
        if (isOver()) {
            winner = winningPlayer();
        }
        else {
            updateCurrentPlayer();
        }
    }

    private void throwExceptionIfInvalidPlayedCard(ContreePlayer player, ClassicalCard card) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(card);

        if (isOver()) {
            throw new IllegalStateException(String.format("Cheater detected : trick is over, player %s cannot play%n", player));
        }

        if (player != currentPlayer) {
            throw new IllegalArgumentException( String.format("Cheater detected -> %s is not current player!. Current player is %s%n", player, currentPlayer) );
        }

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
    public ContreePlayer getWinner() {
        return winner;
    }

    public ContreeTeam getWinnerTeam() {
        return winner.getTeam().orElseThrow();
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

    List<PlayedCard> getPlayedCards() {
        return playedCards;
    }

    public String getTrickId() {
        return trickId;
    }

}
