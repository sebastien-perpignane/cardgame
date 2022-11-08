package sebastien.perpignane.cardgame.game.war;

import sebastien.perpignane.cardgame.card.CardRank;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.game.Trick;
import sebastien.perpignane.cardgame.player.war.AbstractWarPlayer;

import java.util.*;
import java.util.stream.Collectors;

public class WarTrick implements Trick {

    private boolean endOfTrick = false;

    private boolean prematureEndOfTrick = false;

    private int cardLimit = 1;

    private final List<AbstractWarPlayer> players;

    private final Map<AbstractWarPlayer, Integer> nbPlayedCardsByPlayer = new HashMap<>();

    private final Map<AbstractWarPlayer, List<WarPlayedCard>> playedCardsByPlayer = new HashMap<>();

    private AbstractWarPlayer winner = null;

    private final String trickId;

    private final WarGameEventSender warGameEventSender;

    public WarTrick(String trickId, List<AbstractWarPlayer> players, WarGameEventSender warGameEventSender) {
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

    public void playerPlay(WarPlayedCard pc) {

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

    public void playerPlay(AbstractWarPlayer player, ClassicalCard card) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(card);
        playerPlay(new WarPlayedCard(player, card));
    }

    public boolean isWarInProgress() {
        return cardLimit > 1;
    }

    public int getTrickTurn() {
        return nbPlayedCardsByPlayer.values().stream().mapToInt(i -> i).min().orElse(0);
    }

    private void addPlayedCard(WarPlayedCard pc) {
        playedCardsByPlayer.get(pc.player()).add(pc);
    }

    private void incrementPlayedCards(WarPlayedCard pc) {
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
        AbstractWarPlayer bestCardPlayer = null;

        for (AbstractWarPlayer player : players) {

            if (player.hasNoMoreCard() && prematureEndOfTrick) {
                continue;
            }

            List<WarPlayedCard> cards = playedCardsByPlayer.get(player);
            WarPlayedCard lastPlayedCard = cards.get(cards.size() - 1);
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
    public boolean isOver() {
        return endOfTrick;
    }

    @Override
    public AbstractWarPlayer getWinner() {
        return winner;
    }

    public Set<ClassicalCard> getAllCards() {
        return playedCardsByPlayer.values().stream()
                .flatMap(Collection::stream)
                .map(WarPlayedCard::card)
                .collect(Collectors.toSet());
    }

    private List<WarPlayedCard> collectLastPlayedCards() {
        return playedCardsByPlayer.values().stream()
                .filter(cards -> !cards.isEmpty())
                .map(cards -> {
                    int lastCardIdx = cards.size() - 1;
                    return cards.get(lastCardIdx);
                }).collect(Collectors.toList());
    }

    private boolean onePlayerHasNoMoreCardsAndCardsToPlay() {
        for (AbstractWarPlayer player : players) {
            int nbPlayedCards = nbPlayedCardsByPlayer.get(player);
            if (player.hasNoMoreCard() && nbPlayedCards < cardLimit) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<WarPlayedCard> getPlayedCards() {
        return playedCardsByPlayer.values().stream().flatMap(Collection::stream).toList();
    }

    @Override
    public String toString() {
        return "WarTrick{" +
                "trickId='" + trickId + '\'' +
                '}';
    }

}
