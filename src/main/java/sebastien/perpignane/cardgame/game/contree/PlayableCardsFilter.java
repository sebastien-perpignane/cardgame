package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayableCardsFilter {

    Set<ClassicalCard> playableCards(ContreeTrick trick, ContreePlayer player) {

        Objects.requireNonNull(player);

        Set<ClassicalCard> allHand = new HashSet<>(player.getHand());

        if (trick.getPlayedCards().isEmpty()) {
            return allHand;
        }

        var firstPlayedCard = trick.getPlayedCards().get(0).card();

        boolean trumpCardPlayed = trick.getPlayedCards().stream().anyMatch(pc -> pc.card().isTrump());

        if (trick.isTrumpTrick() || trumpCardPlayed) {
            return computeAllowedCardsForTrickWithTrumpCards(trick, player);
        }

        var sameSuitCards = player.getHand().stream().filter(c -> c.getSuit() == firstPlayedCard.getSuit()).collect(Collectors.toSet());
        if (sameSuitCards.isEmpty()) {
            return computeAllowedCardsWhenPlayerLacksSuit(trick, player);
        }
        else {
            return sameSuitCards;
        }

    }

    private Set<ClassicalCard> computeAllowedCardsForTrickWithTrumpCards(ContreeTrick trick, ContreePlayer player) {

        Set<ClassicalCard> allHand = new HashSet<>(player.getHand());

        var optionalHighestTrump = findHighestPlayedTrumpCard(trick);
        if (optionalHighestTrump.isEmpty()) {
            throw new IllegalStateException("This method is supposed to be called when at least one trump card was played");
        }

        var highestTrump = optionalHighestTrump.get();

        // If the trick was not started by a trump card but trumps were played,
        // the player does not have to play a trump if his teammate is winning the trick.
        if (!trick.isTrumpTrick() && trick.winningPlayer().sameTeam(player)) {
            return allHand;
        }

        var playerTrumps = ContreeCard.of(trick.getTrumpSuit(), new HashSet<>(player.getHand())).stream()
                .filter(ContreeCard::isTrump).toList();

        var higherPlayerTrumps = playerTrumps.stream().filter(c -> c.getGameValue() > highestTrump.card().getGameValue()).toList();

        if (higherPlayerTrumps.isEmpty()) {
            return playerTrumps.isEmpty() ? allHand : playerTrumps.stream().map(ContreeCard::getCard).collect(Collectors.toSet());
        } else {
            return higherPlayerTrumps.stream().map(ContreeCard::getCard).collect(Collectors.toSet());
        }
    }

    private Set<ClassicalCard> playerTrumpCards(ContreeTrick trick, ContreePlayer player) {
        return ContreeCard.of(trick.getTrumpSuit(), new HashSet<>(player.getHand())).stream()
                .filter(ContreeCard::isTrump)
                .map(ContreeCard::getCard).collect(Collectors.toSet());
    }

    private Set<ClassicalCard> computeAllowedCardsWhenPlayerLacksSuit(ContreeTrick trick, ContreePlayer player) {

        Set<ClassicalCard> allHand = new HashSet<>(player.getHand());

        var winningPlayer = trick.winningPlayer();
        if (winningPlayer != null && winningPlayer.getTeam().orElseThrow() == player.getTeam().orElseThrow()) {
            return allHand;
        }

        var trumpCards = playerTrumpCards(trick, player);
        if (!trumpCards.isEmpty()) {
            return trumpCards;
        }
        else {
            return allHand;
        }
    }

    private Optional<ContreePlayedCard> findHighestPlayedTrumpCard(ContreeTrick trick) {
        return trick.getPlayedCards().stream()
                .filter(pc -> pc.card().isTrump()).min((a, b) -> Integer.compare(b.card().getGameValue(), a.card().getGameValue()));
    }

}
