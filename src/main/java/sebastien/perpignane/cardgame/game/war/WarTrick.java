package sebastien.perpignane.cardgame.game.war;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.CardRank;
import sebastien.perpignane.cardgame.game.PlayedCard;
import sebastien.perpignane.cardgame.game.Trick;
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

    private final WarGameEventSender warGameEventSender;

    public WarTrick(String trickId, List<Player> players, WarGameEventSender warGameEventSender) {
        if (players.size() != 2) {
            throw new IllegalArgumentException("Only 2 players are allowed");
        }
        this.trickId = trickId;
        this.players = players;
        players.forEach(p -> {
            nbPlayedCardsByPlayer.put(p, 0);
            playedCardsByPlayer.put(p, new ArrayList<>());
        });
        this.warGameEventSender = warGameEventSender;
    }

    //@Override
    public void playerPlay(PlayedCard pc) {

        if (endOfTrick) {
            throw new IllegalStateException("This trick is over");
        }

        addPlayedCard(pc);
        incrementPlayedCards(pc);

        if (isWarCondition()) {
            warGameEventSender.sendWarEvent(collectLastPlayedCards());
            cardLimit += 2;
        }

        computeEndOfTrick();
        computeWinnerIfRelevant();

    }

    //@Override
    public void playerPlay(Player player, ClassicalCard card) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(card);
        playerPlay(new PlayedCard(player, card));
    }

    public boolean isWarInProgress() {
        return cardLimit > 1;
    }

    public int getTrickTurn() {
        return nbPlayedCardsByPlayer.values().stream().mapToInt(i -> i).min().orElse(0);
    }

    private void addPlayedCard(PlayedCard pc) {
        playedCardsByPlayer.get(pc.player()).add(pc);
    }

    private void incrementPlayedCards(PlayedCard pc) {
        int ncPlayedCards = nbPlayedCardsByPlayer.get(pc.player());
        nbPlayedCardsByPlayer.put(pc.player(), ++ncPlayedCards);
    }

    private void computeEndOfTrick() {
        this.prematureEndOfTrick = onePlayerHasNoMoreCardsAndCardsToPlay();
        if (allPlayerReachedCardLimit() || this.prematureEndOfTrick ) {
            endOfTrick = true;
        }
    }

    private void computeWinnerIfRelevant() {
        if (!endOfTrick) {
            return;
        }
        ClassicalCard bestCard = null;
        Player bestCardPlayer = null;

        for (Player player : players) {

            if (player.hasNoMoreCard() && prematureEndOfTrick) {
                continue;
            }

            List<PlayedCard> cards = playedCardsByPlayer.get(player);
            PlayedCard lastPlayedCard = cards.get(cards.size() - 1);
            if (bestCard == null) {
                bestCard = lastPlayedCard.card();
                bestCardPlayer = player;
                continue;
            }

            if (lastPlayedCard.card().getRank().ordinal() > bestCard.getRank().ordinal()) {
                bestCard = lastPlayedCard.card();
                bestCardPlayer = player;
            }

        }

        this.winner = bestCardPlayer;
    }

    private boolean allPlayerReachedCardLimit() {
        return players.stream().allMatch(player -> nbPlayedCardsByPlayer.get(player) == cardLimit);
    }

    private boolean isWarCondition() {
        if (!allPlayerReachedCardLimit()) {
            return false;
        }
        return distinctLastPlayedCardValues().size() == 1;
    }

    private Set<CardRank> distinctLastPlayedCardValues() {
        return playedCardsByPlayer.values().stream().map( playedCards -> {
            int lastCardIndex = playedCards.size() - 1;
            return playedCards.get(lastCardIndex).card().getRank();
        }).collect(Collectors.toSet());
    }

    @Override
    public boolean isEndOfTrick() {
        return endOfTrick;
    }

    @Override
    public Player getWinner() {
        return winner;
    }

    @Override
    public Set<ClassicalCard> getAllCards() {
        return playedCardsByPlayer.values().stream()
                .flatMap(Collection::stream)
                .map(PlayedCard::card)
                .collect(Collectors.toSet());
    }

    private List<PlayedCard> collectLastPlayedCards() {
        return playedCardsByPlayer.values().stream()
                .filter(cards -> !cards.isEmpty())
                .map(cards -> {
                    int lastCardIdx = cards.size() - 1;
                    return cards.get(lastCardIdx);
                }).collect(Collectors.toList());
    }

    private boolean onePlayerHasNoMoreCardsAndCardsToPlay() {
        for (Player player : players) {
            int nbPlayedCards = nbPlayedCardsByPlayer.get(player);
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
