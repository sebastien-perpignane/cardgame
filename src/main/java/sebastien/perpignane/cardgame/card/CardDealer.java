package sebastien.perpignane.cardgame.card;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CardDealer {

    private final List<ClassicalCard> cards;

    private final int nbPlayers;

    private final int nbDistributedCardsPerPlayer;

    private final List<Integer> nbCardGroups;

    public CardDealer(List<ClassicalCard> cards, int nbPlayers, List<Integer> distribConfiguration) {

        nbDistributedCardsPerPlayer = distribConfiguration.stream().mapToInt(Integer::intValue).sum();

        if (cards.size() % nbPlayers != 0) {
            throw new IllegalArgumentException(String.format("%d cards cannot be equally distributed to %d players", nbDistributedCardsPerPlayer, nbPlayers));
        }

        if (nbDistributedCardsPerPlayer * nbPlayers != cards.size()) {
            throw new IllegalArgumentException("All cards will not be distributed to players");
        }

        this.cards = cards;
        this.nbPlayers = nbPlayers;
        this.nbCardGroups = distribConfiguration;
    }

    public List<List<ClassicalCard>> dealCards() {

        final List<List<ClassicalCard>> result = new ArrayList<>(nbPlayers);
        IntStream.range(0, nbPlayers).forEach(playerIdx ->  result.add(new ArrayList<>(nbDistributedCardsPerPlayer)));

        int alreadyDealtCards = 0;
        for(Integer nbCards : nbCardGroups) {
            for (int playerIdx = 0 ; playerIdx < nbPlayers ; playerIdx++ ) {
                int offset = alreadyDealtCards + (playerIdx * nbCards);
                result.get(playerIdx).addAll(cards.subList(offset, offset + nbCards));
            }
            alreadyDealtCards += nbPlayers * nbCards;
        }

        return result;

    }

}
