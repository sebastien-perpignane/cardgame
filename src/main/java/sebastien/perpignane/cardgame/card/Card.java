package sebastien.perpignane.cardgame.card;

public enum Card {

    ACE_DIAMOND(    CardValue.ACE,      CardSuit.DIAMONDS),
    TWO_DIAMOND(    CardValue.TWO,      CardSuit.DIAMONDS),
    THREE_DIAMOND(  CardValue.THREE,    CardSuit.DIAMONDS),
    FOUR_DIAMOND(   CardValue.FOUR,     CardSuit.DIAMONDS),
    FIVE_DIAMOND(   CardValue.FIVE,     CardSuit.DIAMONDS),
    SIX_DIAMOND(    CardValue.SIX,      CardSuit.DIAMONDS),
    SEVEN_DIAMOND(  CardValue.SEVEN,    CardSuit.DIAMONDS),
    EIGHT_DIAMOND(  CardValue.EIGHT,    CardSuit.DIAMONDS),
    NINE_DIAMOND(   CardValue.NINE,     CardSuit.DIAMONDS),
    TEN_DIAMOND(    CardValue.TEN,      CardSuit.DIAMONDS),
    JACK_DIAMOND(   CardValue.JACK,     CardSuit.DIAMONDS),
    QUEEN_DIAMOND(  CardValue.QUEEN,    CardSuit.DIAMONDS),
    KING_DIAMOND(   CardValue.KING,     CardSuit.DIAMONDS),

    ACE_HEART(      CardValue.ACE,      CardSuit.HEARTS),
    TWO_HEART(      CardValue.TWO,      CardSuit.HEARTS),
    THREE_HEART(    CardValue.THREE,    CardSuit.HEARTS),
    FOUR_HEART(     CardValue.FOUR,     CardSuit.HEARTS),
    FIVE_HEART(     CardValue.FIVE,     CardSuit.HEARTS),
    SIX_HEART(      CardValue.SIX,      CardSuit.HEARTS),
    SEVEN_HEART(    CardValue.SEVEN,    CardSuit.HEARTS),
    EIGHT_HEART(    CardValue.EIGHT,    CardSuit.HEARTS),
    NINE_HEART(     CardValue.NINE,     CardSuit.HEARTS),
    TEN_HEART(      CardValue.TEN,      CardSuit.HEARTS),
    JACK_HEART(     CardValue.JACK,     CardSuit.HEARTS),
    QUEEN_HEART(    CardValue.QUEEN,    CardSuit.HEARTS),
    KING_HEART(     CardValue.KING,     CardSuit.HEARTS),

    ACE_SPADE(      CardValue.ACE,      CardSuit.SPADES),
    TWO_SPADE(      CardValue.TWO,      CardSuit.SPADES),
    THREE_SPADE(    CardValue.THREE,    CardSuit.SPADES),
    FOUR_SPADE(     CardValue.FOUR,     CardSuit.SPADES),
    FIVE_SPADE(     CardValue.FIVE,     CardSuit.SPADES),
    SIX_SPADE(      CardValue.SIX,      CardSuit.SPADES),
    SEVEN_SPADE(    CardValue.SEVEN,    CardSuit.SPADES),
    EIGHT_SPADE(    CardValue.EIGHT,    CardSuit.SPADES),
    NINE_SPADE(     CardValue.NINE,     CardSuit.SPADES),
    TEN_SPADE(      CardValue.TEN,      CardSuit.SPADES),
    JACK_SPADE(     CardValue.JACK,     CardSuit.SPADES),
    QUEEN_SPADE(    CardValue.QUEEN,    CardSuit.SPADES),
    KING_SPADE(     CardValue.KING,     CardSuit.SPADES),

    ACE_CLUB(       CardValue.ACE,      CardSuit.CLUBS),
    TWO_CLUB(       CardValue.TWO,      CardSuit.CLUBS),
    THREE_CLUB(     CardValue.THREE,    CardSuit.CLUBS),
    FOUR_CLUB(      CardValue.FOUR,     CardSuit.CLUBS),
    FIVE_CLUB(      CardValue.FIVE,     CardSuit.CLUBS),
    SIX_CLUB(       CardValue.SIX,      CardSuit.CLUBS),
    SEVEN_CLUB(     CardValue.SEVEN,    CardSuit.CLUBS),
    EIGHT_CLUB(     CardValue.EIGHT,    CardSuit.CLUBS),
    NINE_CLUB(      CardValue.NINE,     CardSuit.CLUBS),
    TEN_CLUB(       CardValue.TEN,      CardSuit.CLUBS),
    JACK_CLUB(      CardValue.JACK,     CardSuit.CLUBS),
    QUEEN_CLUB(     CardValue.QUEEN,    CardSuit.CLUBS),
    KING_CLUB(      CardValue.KING,     CardSuit.CLUBS),

    JOKER1(         CardValue.JOKER,    CardSuit.NONE),
    JOKER2(         CardValue.JOKER,    CardSuit.NONE)
    
    ;

    private final CardValue value;
    private final CardSuit suit;

    Card(CardValue value, CardSuit suit) {
        this.value = value;
        this.suit = suit;
    }

    public CardValue getValue() {
        return value;
    }

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
