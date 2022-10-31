package sebastien.perpignane.cardgame.card;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CardDealer {

    private final List<Integer> distribConfiguration;

    public CardDealer(List<Integer> distribConfiguration) {
        this.distribConfiguration = distribConfiguration;
    }

    public List<List<ClassicalCard>> dealCards(List<ClassicalCard> cards, int nbPlayers) {

        int nbDistributedCardsPerPlayer = distribConfiguration.stream().mapToInt(Integer::intValue).sum();

        if (cards.size() % nbPlayers != 0) {
            throw new IllegalArgumentException(String.format("%d cards cannot be equally distributed to %d players", nbDistributedCardsPerPlayer, nbPlayers));
        }

        if (nbDistributedCardsPerPlayer * nbPlayers != cards.size()) {
            throw new IllegalArgumentException("All cards will not be distributed to players");
        }

        final List<List<ClassicalCard>> dealCardsByPlayer = new ArrayList<>(nbPlayers);
        IntStream.range(0, nbPlayers).forEach(playerIdx ->  dealCardsByPlayer.add(new ArrayList<>(nbDistributedCardsPerPlayer)));

        int alreadyDealtCards = 0;
        for(Integer nbCards : distribConfiguration) {
            for (int playerIdx = 0 ; playerIdx < nbPlayers ; playerIdx++ ) {
                int offset = alreadyDealtCards + (playerIdx * nbCards);
                dealCardsByPlayer.get(playerIdx).addAll(cards.subList(offset, offset + nbCards));
            }
            alreadyDealtCards += nbPlayers * nbCards;
        }

        return dealCardsByPlayer;

    }

}
