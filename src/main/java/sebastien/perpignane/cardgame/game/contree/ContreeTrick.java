package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.game.Trick;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.*;
import java.util.stream.Collectors;

class ContreeTrick implements Trick {

    private final String trickId;

    private final List<PlayedCard> playedCards = new ArrayList<>();

    private ContreeCard firstPlayedCard = null;

    private final Iterator<ContreePlayer> playerIterator;

    private ContreePlayer currentPlayer;

    private Collection<ClassicalCard> currentPlayerPlayableCards;

    private final CardSuit trumpSuit;

    private ContreePlayer winner;

    private final ContreeGameEventSender eventSender;

    public ContreeTrick(String trickId, List<ContreePlayer> players, CardSuit trumpSuit, ContreeGameEventSender eventSender) {
        this.trickId = trickId;
        this.eventSender = eventSender;
        this.trumpSuit = trumpSuit;
        playerIterator = players.iterator();
    }

    public ContreeTrick(String trickId, List<ContreePlayer> players, ContreePlayer previousWinner, CardSuit trumpSuit, ContreeGameEventSender eventSender) {
        this.trickId = trickId;
        this.eventSender = eventSender;
        // FIXME player list should be provided as argument
        var newPlayers = buildNextTrickPlayerListFromPreviousWinner(players, previousWinner);
        this.trumpSuit = trumpSuit;
        playerIterator = newPlayers.iterator();
    }

    public void startTrick() {
        updateCurrentPlayer();
    }

    private void updateCurrentPlayer() {
        currentPlayer = playerIterator.next();
        currentPlayerPlayableCards = playableCards(currentPlayer);
        currentPlayer.onPlayerTurn(currentPlayerPlayableCards);
    }

    List<ContreePlayer> buildNextTrickPlayerListFromPreviousWinner(List<ContreePlayer> players, ContreePlayer winner) {

        List<ContreePlayer> result = new ArrayList<>(ContreeGame.NB_PLAYERS);

        int winnerIndex = players.indexOf(winner);

        for (int i = winnerIndex ; ; ) {

            result.add(players.get(i));

            if (result.size() == players.size()) {
                break;
            }

            if (i == players.size() - 1) {
                i = 0;
            }
            else {
                i++;
            }
        }

        return result;

    }





    Collection<ClassicalCard> playableCards(Player player) {
        PlayableCardsFilter filter = new PlayableCardsFilter();
        return filter.playableCards(player);
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
        if (isEndOfTrick()) {
            winner = winningPlayer();
        }
        else {
            updateCurrentPlayer();
        }
    }

    private void throwExceptionIfInvalidPlayedCard(ContreePlayer player, ClassicalCard card) {
        Objects.requireNonNull(player);
        Objects.requireNonNull(card);

        if (isEndOfTrick()) {
            throw new IllegalStateException(String.format("Cheater detected : trick is over, player %s cannot play%n", player));
        }

        if (player != currentPlayer) {
            throw new IllegalArgumentException(String.format("Cheater detected -> %s is not current player!. Current player is %s%n", player, currentPlayer));
        }

        if (!currentPlayerPlayableCards.contains(card) ) {
            String allowedCardsStr = currentPlayerPlayableCards.stream().map(ClassicalCard::toString).collect(Collectors.joining(","));
            throw new IllegalArgumentException(String.format("Player %s : cheater detected -> %s is not an allowed card. Allowed cards are : %s", player, card, allowedCardsStr));
        }
    }

    @Override
    public boolean isEndOfTrick() {
        return playedCards.size() == 4;
    }

    @Override
    public ContreePlayer getWinner() {
        return winner;
    }

    public ContreeTeam getWinnerTeam() {
        return winner.getTeam().orElseThrow();
    }

    @Override
    public Set<ClassicalCard> getAllCards() {
        return playedCards.stream().map(pc -> pc.card().getCard()).collect(Collectors.toSet());
    }

    public CardSuit getTrumpSuit() {
        return trumpSuit;
    }

    private class PlayableCardsFilter {
        Collection<ClassicalCard> playableCards(Player player) {

            Objects.requireNonNull(player);

            Collection<ClassicalCard> allHand = new ArrayList<>(player.getHand());

            if (firstPlayedCard == null) {
                return allHand;
            }

            boolean trumpCardPlayed = playedCards.stream().anyMatch(pc -> pc.card().isTrump());

            if (isTrumpTrick() || trumpCardPlayed) {
                return computeAllowedCardsForTrickWithTrumpCards(player);
            }

            var sameSuitCardStream = player.getHand().stream().filter(c -> c.getSuit() == firstPlayedCard.getSuit());
            var sameSuitCard = sameSuitCardStream.toList();
            if (sameSuitCard.isEmpty()) {
                return computeAllowedCardsWhenPlayerLacksSuit(player);
            }
            else {
                return sameSuitCard;
            }

        }

        private Collection<ClassicalCard> computeAllowedCardsForTrickWithTrumpCards(Player player) {

            Collection<ClassicalCard> allHand = new ArrayList<>(player.getHand());

            var optionalHighestTrump = findHighestPlayedTrumpCard();
            if (optionalHighestTrump.isEmpty()) {
                throw new IllegalStateException("This method is supposed to be called when at least one trump card was played");
            }

            var highestTrump = optionalHighestTrump.get();

            // If the trick was not started by a trump card but trumps were played,
            // the player does not have to play a trump if his teammate is winning the trick.
            if (!isTrumpTrick() && winningPlayer().sameTeam(player)) {
                return allHand;
            }

            var playerTrumps = ContreeCard.of(trumpSuit, new HashSet<>(player.getHand())).stream()
                    .filter(ContreeCard::isTrump).toList();

            var higherPlayerTrumps = playerTrumps.stream().filter(c -> c.getGameValue() > highestTrump.card().getGameValue()).toList();

            if (higherPlayerTrumps.isEmpty()) {
                return playerTrumps.isEmpty() ? allHand : playerTrumps.stream().map(ContreeCard::getCard).toList();
            } else {
                return higherPlayerTrumps.stream().map(ContreeCard::getCard).toList();
            }
        }

        private Collection<ClassicalCard> playerTrumpCards(Player player) {
            return ContreeCard.of(trumpSuit, new HashSet<>(player.getHand())).stream()
                    .filter(ContreeCard::isTrump)
                    .map(ContreeCard::getCard).toList();
        }

        private Collection<ClassicalCard> computeAllowedCardsWhenPlayerLacksSuit(Player player) {

            Collection<ClassicalCard> allHand = new ArrayList<>(player.getHand());

            var winningPlayer = winningPlayer();
            if (winningPlayer != null && winningPlayer.getTeam().orElseThrow() == player.getTeam().orElseThrow()) {
                return allHand;
            }

            var trumpCards = playerTrumpCards(player);
            if (!trumpCards.isEmpty()) {
                return trumpCards;
            }
            else {
                return allHand;
            }
        }

        private Optional<PlayedCard> findHighestPlayedTrumpCard() {
            return playedCards.stream()
                    .filter(pc -> pc.card().isTrump()).min((a, b) -> Integer.compare(b.card().getGameValue(), a.card().getGameValue()));
        }
    }

    ContreePlayer winningPlayer() {
        if (playedCards.isEmpty()) {
            return null;
        }
        return playedCards.stream().max(Comparator.comparingInt(a -> a.card().getGameValue())).get().player();
    }

    List<PlayedCard> getPlayedCards() {
        return playedCards;
    }

    public String getTrickId() {
        return trickId;
    }

}
