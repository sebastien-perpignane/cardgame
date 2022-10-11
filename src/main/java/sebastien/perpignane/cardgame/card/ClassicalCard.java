package sebastien.perpignane.cardgame.card;

public enum ClassicalCard implements Card {

    ACE_DIAMOND(    CardRank.ACE,      CardSuit.DIAMONDS),
    TWO_DIAMOND(    CardRank.TWO,      CardSuit.DIAMONDS),
    THREE_DIAMOND(  CardRank.THREE,    CardSuit.DIAMONDS),
    FOUR_DIAMOND(   CardRank.FOUR,     CardSuit.DIAMONDS),
    FIVE_DIAMOND(   CardRank.FIVE,     CardSuit.DIAMONDS),
    SIX_DIAMOND(    CardRank.SIX,      CardSuit.DIAMONDS),
    SEVEN_DIAMOND(  CardRank.SEVEN,    CardSuit.DIAMONDS),
    EIGHT_DIAMOND(  CardRank.EIGHT,    CardSuit.DIAMONDS),
    NINE_DIAMOND(   CardRank.NINE,     CardSuit.DIAMONDS),
    TEN_DIAMOND(    CardRank.TEN,      CardSuit.DIAMONDS),
    JACK_DIAMOND(   CardRank.JACK,     CardSuit.DIAMONDS),
    QUEEN_DIAMOND(  CardRank.QUEEN,    CardSuit.DIAMONDS),
    KING_DIAMOND(   CardRank.KING,     CardSuit.DIAMONDS),

    ACE_HEART(      CardRank.ACE,      CardSuit.HEARTS),
    TWO_HEART(      CardRank.TWO,      CardSuit.HEARTS),
    THREE_HEART(    CardRank.THREE,    CardSuit.HEARTS),
    FOUR_HEART(     CardRank.FOUR,     CardSuit.HEARTS),
    FIVE_HEART(     CardRank.FIVE,     CardSuit.HEARTS),
    SIX_HEART(      CardRank.SIX,      CardSuit.HEARTS),
    SEVEN_HEART(    CardRank.SEVEN,    CardSuit.HEARTS),
    EIGHT_HEART(    CardRank.EIGHT,    CardSuit.HEARTS),
    NINE_HEART(     CardRank.NINE,     CardSuit.HEARTS),
    TEN_HEART(      CardRank.TEN,      CardSuit.HEARTS),
    JACK_HEART(     CardRank.JACK,     CardSuit.HEARTS),
    QUEEN_HEART(    CardRank.QUEEN,    CardSuit.HEARTS),
    KING_HEART(     CardRank.KING,     CardSuit.HEARTS),

    ACE_SPADE(      CardRank.ACE,      CardSuit.SPADES),
    TWO_SPADE(      CardRank.TWO,      CardSuit.SPADES),
    THREE_SPADE(    CardRank.THREE,    CardSuit.SPADES),
    FOUR_SPADE(     CardRank.FOUR,     CardSuit.SPADES),
    FIVE_SPADE(     CardRank.FIVE,     CardSuit.SPADES),
    SIX_SPADE(      CardRank.SIX,      CardSuit.SPADES),
    SEVEN_SPADE(    CardRank.SEVEN,    CardSuit.SPADES),
    EIGHT_SPADE(    CardRank.EIGHT,    CardSuit.SPADES),
    NINE_SPADE(     CardRank.NINE,     CardSuit.SPADES),
    TEN_SPADE(      CardRank.TEN,      CardSuit.SPADES),
    JACK_SPADE(     CardRank.JACK,     CardSuit.SPADES),
    QUEEN_SPADE(    CardRank.QUEEN,    CardSuit.SPADES),
    KING_SPADE(     CardRank.KING,     CardSuit.SPADES),

    ACE_CLUB(       CardRank.ACE,      CardSuit.CLUBS),
    TWO_CLUB(       CardRank.TWO,      CardSuit.CLUBS),
    THREE_CLUB(     CardRank.THREE,    CardSuit.CLUBS),
    FOUR_CLUB(      CardRank.FOUR,     CardSuit.CLUBS),
    FIVE_CLUB(      CardRank.FIVE,     CardSuit.CLUBS),
    SIX_CLUB(       CardRank.SIX,      CardSuit.CLUBS),
    SEVEN_CLUB(     CardRank.SEVEN,    CardSuit.CLUBS),
    EIGHT_CLUB(     CardRank.EIGHT,    CardSuit.CLUBS),
    NINE_CLUB(      CardRank.NINE,     CardSuit.CLUBS),
    TEN_CLUB(       CardRank.TEN,      CardSuit.CLUBS),
    JACK_CLUB(      CardRank.JACK,     CardSuit.CLUBS),
    QUEEN_CLUB(     CardRank.QUEEN,    CardSuit.CLUBS),
    KING_CLUB(      CardRank.KING,     CardSuit.CLUBS),

    JOKER1(         CardRank.JOKER,    CardSuit.NONE),
    JOKER2(         CardRank.JOKER,    CardSuit.NONE)
    
    ;

    private final CardRank value;
    private final CardSuit suit;

    ClassicalCard(CardRank value, CardSuit suit) {
        this.value = value;
        this.suit = suit;
    }

    @Override
    public CardRank getRank() {
        return value;
    }

    @Override
    public CardSuit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return "Card{" +
                value + suit +
                '}';
    }

}
