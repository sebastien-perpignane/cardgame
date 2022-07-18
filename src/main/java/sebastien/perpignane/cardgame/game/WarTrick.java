package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.card.Card;
import sebastien.perpignane.cardgame.card.CardValue;
import sebastien.perpignane.cardgame.player.Player;

import java.util.*;
import java.util.stream.Collectors;

public class WarTrick implements Trick {

    private boolean endOfTrick = false;

    private boolean prematureEndOfTrick = false;

    private int cardLimit = 1;

    private final List<Player> players;

    private final Map<Player, Integer> nbPlayedCardsByPlayer = new HashMap<>();

    private final Map<Player, List<PlayedCard>> playedCardsByPlayer = new HashMap<>();

    private Player winner = null;

    private final String trickId;

    private final List<WarTrickObserver> observers;

    public WarTrick(String trickId, List<Player> players, List<WarTrickObserver> observers) {
        if (players.size() != 2) {
            throw new IllegalArgumentException("Only 2 players are allowed");
        }
        this.trickId = trickId;
        this.players = players;
        this.observers = observers;
        for (Player p: players) {
            nbPlayedCardsByPlayer.put(p, 0);
            playedCardsByPlayer.put(p, new ArrayList<>());
        }
    }

    @Override
    public void playerPlay(PlayedCard pc) {

        if (endOfTrick) {
            throw new IllegalStateException("This trick is over");
        }

        addPlayedCard(pc);
        incrementPlayedCards(pc);

        if (isWarCondition()) {
            sendWarEvent();
            cardLimit += 2;
        }

        computeEndOfTrick();
        computeWinnerIfRelevant();

    }

    private void addPlayedCard(PlayedCard pc) {
        playedCardsByPlayer.get(pc.player()).add(pc);
    }

    private void incrementPlayedCards(PlayedCard pc) {
        int ncPlayedCards = nbPlayedCardsByPlayer.get(pc.player());
        nbPlayedCardsByPlayer.put(pc.player(), ++ncPlayedCards);
    }

    private void computeEndOfTrick() {
        if (allPlayerReachedCardLimit() || onePlayerHasNoMoreCardsAndCardsToPlay() ) {
            endOfTrick = true;
        }
        this.prematureEndOfTrick = onePlayerHasNoMoreCardsAndCardsToPlay();
    }

    private void computeWinnerIfRelevant() {
        if (!endOfTrick) {
            return;
        }
        Card bestCard = null;
        Player bestCardPlayer = null;

        for (var player : players) {

            if (player.hasNoMoreCard() && prematureEndOfTrick) {
                continue;
            }

            var cards = playedCardsByPlayer.get(player);
            var lastPlayedCard = cards.get(cards.size() - 1);
            if (bestCard == null) {
                bestCard = lastPlayedCard.card();
                bestCardPlayer = player;
                continue;
            }

            if (lastPlayedCard.card().getValue().ordinal() > bestCard.getValue().ordinal()) {
                bestCard = lastPlayedCard.card();
                bestCardPlayer = player;
            }

        }

        this.winner = bestCardPlayer;
    }

    private boolean allPlayerReachedCardLimit() {
        for (var player : players) {
            if (nbPlayedCardsByPlayer.get(player) != cardLimit) {
                return false;
            }
        }
        return true;
    }

    private boolean isWarCondition() {
        if (!allPlayersPlayedToCardLimit()) {
            return false;
        }
        return distinctLastPlayedCardValues().size() == 1;
    }

    private Set<CardValue> distinctLastPlayedCardValues() {
        return playedCardsByPlayer.values().stream().map( playedCards -> {
            var lastCardIndex = playedCards.size() - 1;
            return playedCards.get(lastCardIndex).card().getValue();
        }).collect(Collectors.toSet());
    }

    private boolean allPlayersPlayedToCardLimit() {
        for (var player : players) {
            if (nbPlayedCardsByPlayer.get(player) != cardLimit) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEndOfTrick() {
        return endOfTrick;
    }

    @Override
    public boolean isPrematureEndOfTrick() {
        return prematureEndOfTrick;
    }

    @Override
    public Player getWinner() {
        return winner;
    }

    @Override
    public List<Card> getAllCards() {
        return playedCardsByPlayer.values().stream().flatMap(Collection::stream).map(PlayedCard::card).toList();
    }

    private void sendWarEvent() {
        observers.forEach(o -> o.onWar(collectLastPlayedCards()));
    }

    List<PlayedCard> collectLastPlayedCards() {
        return playedCardsByPlayer.values().stream()
                .filter(cards -> !cards.isEmpty())
                .map(cards -> {
                    var lastCardIdx = cards.size() - 1;
                    return cards.get(lastCardIdx);
                }).toList();
    }

    private boolean endOfTurn() {
        Integer nbPlayedCards = null;
        for(int nb : nbPlayedCardsByPlayer.values()) {
            if (nbPlayedCards == null) {
                nbPlayedCards = nb;
            }
            if (nbPlayedCards != nb) {
                return false;
            }
        }
        return true;
    }

    private boolean onePlayerHasNoMoreCardsAndCardsToPlay() {
        for (var player : players) {
            var nbPlayedCards = nbPlayedCardsByPlayer.get(player);
            if (player.hasNoMoreCard() && nbPlayedCards < cardLimit) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "WarTrick{" +
                "trickId='" + trickId + '\'' +
                '}';
    }

}
