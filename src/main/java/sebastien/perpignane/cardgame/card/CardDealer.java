package sebastien.perpignane.cardgame.card;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CardDealer {

    private final List<Integer> distributeConfiguration;
    private final int nbDistributedCardsPerPlayer;

    public CardDealer(List<Integer> distributeConfiguration) {
        this.distributeConfiguration = distributeConfiguration;
        nbDistributedCardsPerPlayer = nbDistributedCardsPerPlayer();
    }

    public List<List<ClassicalCard>> dealCards(List<ClassicalCard> cards, int nbPlayers) {

        throwExceptionIfCardsCannotBeEquallyDistributed(cards.size(), nbPlayers);

        throwExceptionIfNbDistributedCardsDoesNotMatchNbCardsAndNbPlayers(cards.size(), nbPlayers);

        return buildPlayerHands(cards, nbPlayers);

    }

    private int nbDistributedCardsPerPlayer() {
        return distributeConfiguration.stream().mapToInt(Integer::intValue).sum();
    }

    private void throwExceptionIfCardsCannotBeEquallyDistributed(int nbCards, int nbPlayers) {
        if (nbCards % nbPlayers != 0) {
            throw new IllegalArgumentException(String.format("%d cards cannot be equally distributed to %d players", nbCards, nbPlayers));
        }
    }

    private void throwExceptionIfNbDistributedCardsDoesNotMatchNbCardsAndNbPlayers(int nbCards, int nbPlayers) {
        if (nbDistributedCardsPerPlayer * nbPlayers != nbCards) {
            String exceptionMessage = String.format("""
                    Inconsistent number of cards to be distributed to each player.
                    Total number of cards: %d
                    Number of players: %d
                    Number of cards per player: %d
                    Check distribution configuration: %s
                """.stripIndent(),
                nbCards,
                nbPlayers,
                nbDistributedCardsPerPlayer,
                distributeConfiguration
            );
            throw new IllegalArgumentException(exceptionMessage);
        }
    }

    private List<List<ClassicalCard>> buildPlayerHands(List<ClassicalCard> cards, int nbPlayers) {
        final List<List<ClassicalCard>> handByPlayer = initHandByPlayer(nbPlayers);

        int alreadyDealtCards = 0;
        for(Integer nbCards : distributeConfiguration) {
            for (int playerIdx = 0 ; playerIdx < nbPlayers ; playerIdx++ ) {
                int offset = alreadyDealtCards + (playerIdx * nbCards);
                handByPlayer.get(playerIdx).addAll(cards.subList(offset, offset + nbCards));
            }
            alreadyDealtCards += nbPlayers * nbCards;
        }

        return handByPlayer;
    }

    private List<List<ClassicalCard>> initHandByPlayer(int nbPlayers) {
        final List<List<ClassicalCard>> handByPlayer = new ArrayList<>(nbPlayers);
        IntStream.range(0, nbPlayers).forEach(playerIdx ->  handByPlayer.add(new ArrayList<>(nbDistributedCardsPerPlayer)));
        return handByPlayer;
    }

}
