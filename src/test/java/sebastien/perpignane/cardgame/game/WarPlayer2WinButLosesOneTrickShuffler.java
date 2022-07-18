package sebastien.perpignane.cardgame.game;

import sebastien.perpignane.cardgame.card.Card;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSetShuffler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WarPlayer2WinButLosesOneTrickShuffler implements CardSetShuffler {

    @Override
    public List<Card> shuffle(CardSet cSet) {

        List<Card> player1Cards = new ArrayList<>(Arrays.asList(
                Card.SEVEN_DIAMOND,
                Card.SEVEN_CLUB,
                Card.SEVEN_HEART,
                Card.EIGHT_SPADE,
                Card.EIGHT_CLUB
        ));
        List<Card> player2Cards = Arrays.asList(
                Card.SIX_CLUB,
                Card.KING_CLUB,
                Card.KING_DIAMOND,
                Card.KING_HEART,
                Card.KING_SPADE
        );

        player1Cards.addAll(player2Cards);

        return player1Cards;
    }

}
