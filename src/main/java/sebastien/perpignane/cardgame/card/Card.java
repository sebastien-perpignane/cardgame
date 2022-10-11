package sebastien.perpignane.cardgame.card;

public interface Card {
    CardRank getRank();

    CardSuit getSuit();

    @Override
    String toString();
}
