package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class PlayableCardsFilter {

    Set<ClassicalCard> playableCards(ContreeTrick trick, ContreePlayer player) {

        Objects.requireNonNull(player);

        Set<ClassicalCard> allHand = new HashSet<>(player.getHand());

        if (trick.getPlayedCards().isEmpty()) {
            return allHand;
        }

        var firstPlayedCard = trick.getPlayedCards().get(0).card();

        var sameSuitCards = player.getHand().stream().filter(c -> c.getSuit() == firstPlayedCard.getSuit()).collect(Collectors.toSet());
        boolean hasWantedSuit = !sameSuitCards.isEmpty();

        boolean trumpCardPlayed = trick.getPlayedCards().stream().anyMatch(pc -> pc.card().isTrump());

        if (trick.isTrumpTrick() || (trumpCardPlayed && !hasWantedSuit)) {
            return computeAllowedCardsForTrickWithTrumpCards(trick, player);
        }

        if (hasWantedSuit) {
            return sameSuitCards;
        }
        else {
            return computeAllowedCardsWhenPlayerLacksSuit(trick, player);
        }

    }

    private Set<ClassicalCard> computeAllowedCardsForTrickWithTrumpCards(ContreeTrick trick, ContreePlayer player) {

        Set<ClassicalCard> allHand = new HashSet<>(player.getHand());

        // If the trick was not started by a trump card but trumps were played,
        // the player does not have to play a trump if his teammate is winning the trick.
        if (!trick.isTrumpTrick() && trick.winningPlayer().sameTeam(player)) {
            return allHand;
        }

        var playerTrumps = trick.getPlayerHand(player)
                            .stream()
                            .filter(ContreeCard::isTrump).toList();

        var highestTrump = findHighestPlayedTrumpCard(trick);
        var higherPlayerTrumps = playerTrumps.stream().filter(c -> c.getGameValue() > highestTrump.card().getGameValue()).toList();

        if (higherPlayerTrumps.isEmpty()) {
            return playerTrumps.isEmpty() ? allHand : playerTrumps.stream().map(ContreeCard::getCard).collect(Collectors.toSet());
        } else {
            return higherPlayerTrumps.stream().map(ContreeCard::getCard).collect(Collectors.toSet());
        }
    }

    private Set<ClassicalCard> playerTrumpCards(ContreeTrick trick, ContreePlayer player) {
        return trick.getPlayerHand(player).stream()
                .filter(ContreeCard::isTrump)
                .map(ContreeCard::getCard).collect(Collectors.toSet());
    }

    private Set<ClassicalCard> computeAllowedCardsWhenPlayerLacksSuit(ContreeTrick trick, ContreePlayer player) {

        Set<ClassicalCard> allHand = new HashSet<>(player.getHand());

        var winningPlayer = trick.winningPlayer();
        if (winningPlayer != null && winningPlayer.sameTeam(player)) {
            return allHand;
        }

        var trumpCards = playerTrumpCards(trick, player);

        return trumpCards.isEmpty() ? allHand : trumpCards;

    }

    private ContreePlayedCard findHighestPlayedTrumpCard(ContreeTrick trick) {
        return trick.getPlayedCards().stream()
                .filter(pc -> pc.card().isTrump())
                .min((a, b) -> Integer.compare(b.card().getGameValue(), a.card().getGameValue()))
                .orElseThrow(() -> new IllegalStateException("Don't call me if no trump card played"));
    }

}
