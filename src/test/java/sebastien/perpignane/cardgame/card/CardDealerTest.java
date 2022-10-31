package sebastien.perpignane.cardgame.card;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardDealerTest {

    @DisplayName("Standard card dealing, according to Fédération Française de Belote")
    @Test
    public void testDealBy3then3then2() {

        final List<Integer> distribConfig = List.of(3,3,2);
        final int nbPlayers = 4;
        final int nbCardsPerPlayer = distribConfig.stream().mapToInt(Integer::intValue).sum();

        CardDealer cardDealer = new CardDealer(
            distribConfig
        );

        var hands = cardDealer.dealCards(CardSet.GAME_32.getGameCards().stream().toList(), nbPlayers);

        assertEquals(nbPlayers, hands.size());
        assertEquals(nbCardsPerPlayer, hands.get(0).size());
        assertEquals(nbCardsPerPlayer, hands.get(1).size());
        assertEquals(nbCardsPerPlayer, hands.get(2).size());
        assertEquals(nbCardsPerPlayer, hands.get(3).size());

        checkAllHandIntersections(hands);

    }

    @DisplayName("Invalid arguments -> cannot equally deal 9 cards to 4 players with a 32 card set")
    @Test
    public void invalidDistribConfig() {

        // 9 (3 then 3 then 3) cards per player x 4 players != 32 cards -> invalid config
        final List<Integer> distribConfig = List.of(3,3,3);
        final int nbPlayers = 4;

        CardDealer cardDealer = new CardDealer(distribConfig);

        assertThrows(
            RuntimeException.class,
            () -> cardDealer.dealCards(CardSet.GAME_32.getGameCards().stream().toList(), nbPlayers)
        );

    }

    @DisplayName("32 cards cannot equally be dealt to 3 players -> exception")
    @Test
    public void invalidNbPlayersForNumberOfCards() {

        // 9 (3 then 3 then 3) cards per player x 4 players != 32 cards -> invalid config
        final List<Integer> distribConfig = List.of(3,3,2);
        final int nbPlayers = 3;

        CardDealer cardDealer = new CardDealer(distribConfig);

        assertThrows(
            RuntimeException.class,
            () -> cardDealer.dealCards(CardSet.GAME_32.getGameCards().stream().toList(), nbPlayers)
        );

    }

    private void checkAllHandIntersections(List<List<ClassicalCard>> hands) {
        assertFalse(hasIntersection(hands.get(0), hands.get(1)));
        assertFalse(hasIntersection(hands.get(0), hands.get(2)));
        assertFalse(hasIntersection(hands.get(0), hands.get(3)));

        assertFalse(hasIntersection(hands.get(1), hands.get(2)));
        assertFalse(hasIntersection(hands.get(1), hands.get(3)));

        assertFalse(hasIntersection(hands.get(2), hands.get(3)));
    }

    @DisplayName("Testing a method only used for testing :D")
    @Test
    public void testIntersection() {

        assertFalse(
                hasIntersection(
                        List.of(ClassicalCard.ACE_SPADE, ClassicalCard.TEN_HEART),
                        List.of(ClassicalCard.ACE_HEART, ClassicalCard.TEN_SPADE)
                )
        );

        assertTrue(
                hasIntersection(
                        List.of(ClassicalCard.ACE_SPADE, ClassicalCard.TEN_HEART, ClassicalCard.NINE_CLUB),
                        List.of(ClassicalCard.NINE_CLUB, ClassicalCard.ACE_HEART, ClassicalCard.TEN_SPADE)
                )
        );

    }

    boolean hasIntersection(final Collection<ClassicalCard> cards1, Collection<ClassicalCard> cards2) {
        return cards2.stream().anyMatch(cards1::contains);
    }

}
