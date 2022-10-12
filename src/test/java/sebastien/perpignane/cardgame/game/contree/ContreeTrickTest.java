package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.player.Player;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static sebastien.perpignane.cardgame.game.contree.ContreeTestUtils.buildPlayers;

public class ContreeTrickTest {

    // TODO Test playerPlays when trick is over
    // TODO Test playerPlays when card played is not allowed

    @DisplayName("if winner is player 0, player list for next trick is same as players")
    @Test
    public void testBuildPlayerListFromWinner_Player1() {

        var players = buildPlayers();
        var winner = players.get(0);

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.HEARTS, new ContreeGameEventSender());

        var result = trick.buildNextTrickPlayerListFromPreviousWinner(players, winner);

        assertEquals(players, result);

    }

    @DisplayName("if winner is player 2, player list for next trick is : player 2, player 3, player 4, player 1")
    @Test
    public void testBuildPlayerListFromWinner_Player2() {

        var players = buildPlayers();
        var winner = players.get(1);

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.HEARTS, new ContreeGameEventSender());

        var result = trick.buildNextTrickPlayerListFromPreviousWinner(players, winner);

        assertEquals(
                List.of(
                        players.get(1),
                        players.get(2),
                        players.get(3),
                        players.get(0)
                ),
                result
        );

        assertNotEquals(
                List.of(
                        players.get(0),
                        players.get(1),
                        players.get(2),
                        players.get(3)
                ),
                result
        );

    }

    @DisplayName("if winner is player 3, player list for next trick is : player 3, player 4, player 1, player 2")
    @Test
    public void testBuildPlayerListFromWinner_Player3() {

        var players = buildPlayers();
        var winner = players.get(2);

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.HEARTS, new ContreeGameEventSender());

        var result = trick.buildNextTrickPlayerListFromPreviousWinner(players, winner);

        assertEquals(
                List.of(
                        players.get(2),
                        players.get(3),
                        players.get(0),
                        players.get(1)
                ),
                result
        );

    }

    @DisplayName("if winner is player 4, player list for next trick is : player 4, player 1, player 2, player 3")
    @Test
    public void testBuildPlayerListFromWinner_Player4() {

        var players = buildPlayers();
        var winner = players.get(3);

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.HEARTS, new ContreeGameEventSender());

        var result = trick.buildNextTrickPlayerListFromPreviousWinner(players, winner);

        assertEquals(
                List.of(
                        players.get(3),
                        players.get(0),
                        players.get(1),
                        players.get(2)
                ),
                result
        );

    }

    @Test
    @DisplayName("The first played card can be any card")
    public void testFirstPlayedCard() {

        List<ContreePlayer> players = buildPlayers();

        Player testedPlayer = players.get(0);
        List<ClassicalCard> playerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE);
        when(testedPlayer.getHand()).thenReturn(playerHand);

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.HEARTS, new ContreeGameEventSender());

        var playableCards = trick.playableCards(testedPlayer);

        assertSame(CardSuit.HEARTS, trick.getTrumpSuit());
        assertEquals(playerHand, playableCards);

    }

    @Test
    @DisplayName("When a fist card was played and the user has cards with same suit, he must play a card with same suit")
    public void testFirstPlayedCardAlreadyPlayed_compatibleCardAvailable_noTrump() {

        var players = buildPlayers();

        ContreePlayer opponent = players.get(0);
        when(opponent.getHand()).thenReturn(List.of(ClassicalCard.JACK_CLUB));

        ContreePlayer testedPlayer = players.get(1);
        List<ClassicalCard> playerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE);
        when(testedPlayer.getHand()).thenReturn(playerHand);

        CardSuit trumpSuit = CardSuit.HEARTS;
        ContreeTrick trick = new ContreeTrick("TEST", players, trumpSuit, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(opponent, ClassicalCard.JACK_CLUB);

        var playableCards = trick.playableCards(testedPlayer);

        assertEquals(playerHand.stream().filter(c -> c.getSuit() == CardSuit.CLUBS).collect(Collectors.toList()), playableCards);

    }

    @Test
    @DisplayName("When a first card was played by opponent and the user does not have cards with same suit nor trumps, he can play any card")
    public void testFirstCardIsPlayed_noCompatibleCardAvailable_noTrump() {

        var players = buildPlayers();

        ContreePlayer opponent = players.get(0);
        when(opponent.getHand()).thenReturn(List.of(ClassicalCard.JACK_DIAMOND));

        ContreePlayer testedPlayer = players.get(1);
        List<ClassicalCard> playerHand = List.of(
            ClassicalCard.SEVEN_CLUB,
            ClassicalCard.ACE_CLUB,
            ClassicalCard.ACE_SPADE
        );
        when(testedPlayer.getHand()).thenReturn(playerHand);

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.HEARTS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(opponent, ClassicalCard.JACK_DIAMOND);

        var playableCards = trick.playableCards(testedPlayer);

        assertEquals(playerHand, playableCards);
    }

    @Test
    @DisplayName("When a fist card was played and the user does not have cards with same suit but has trump, he must trump")
    public void testFirstCardIsPlayed_noCompatibleCardAvailable_hasTrump() {

        var players = buildPlayers();
        ContreePlayer opponent = players.get(0);
        when(opponent.getHand()).thenReturn(List.of(ClassicalCard.JACK_DIAMOND));

        ContreePlayer testedPlayer = players.get(1);
        List<ClassicalCard> playerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE, ClassicalCard.JACK_HEART);
        when(testedPlayer.getHand()).thenReturn(playerHand);

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.HEARTS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(opponent, ClassicalCard.JACK_DIAMOND);

        var playableCards = trick.playableCards(testedPlayer);

        assertEquals(List.of(ClassicalCard.JACK_HEART), playableCards);
    }

    @DisplayName("An opponent played trump against the player teammate, player must overtrump")
    @Test
    public void testFirstCardPlayed_notTrumpTrick_trumpCardPlayed_playerHasHigherCard() {

        var players = buildPlayers();

        ContreePlayer testedPlayer = players.get(2);
        List<ClassicalCard> playerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE, ClassicalCard.TEN_HEART, ClassicalCard.QUEEN_HEART);
        when(testedPlayer.getHand()).thenReturn(playerHand);

        ContreePlayer teamMate = players.get(0);
        when(teamMate.getHand()).thenReturn(List.of(ClassicalCard.JACK_DIAMOND));

        ContreePlayer opponent = players.get(1);
        when(opponent.getHand()).thenReturn(List.of(ClassicalCard.KING_HEART));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.HEARTS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(teamMate, ClassicalCard.JACK_DIAMOND);
        trick.playerPlays(opponent, ClassicalCard.KING_HEART);

        var playableCards = trick.playableCards(testedPlayer);
        assertEquals(List.of(ClassicalCard.TEN_HEART), playableCards);
    }

    @DisplayName("An opponent played trump against the player teammate, player must undertrump")
    @Test
    public void testFirstCardPlayed_notTrumpTrick_trumpCardPlayed_playerHasntHigherCard() {

        var players = buildPlayers();

        ContreePlayer testedPlayer = players.get(2);
        List<ClassicalCard> playerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE, ClassicalCard.EIGHT_HEART);
        when(testedPlayer.getHand()).thenReturn(playerHand);

        ContreePlayer opponent1 = players.get(1);
        when(opponent1.getHand()).thenReturn(List.of(ClassicalCard.TEN_HEART));

        ContreePlayer teamMate = players.get(0);
        when(teamMate.getHand()).thenReturn(List.of(ClassicalCard.JACK_DIAMOND));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.HEARTS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(teamMate, ClassicalCard.JACK_DIAMOND);
        trick.playerPlays(opponent1, ClassicalCard.TEN_HEART);

        var playableCards = trick.playableCards(testedPlayer);
        assertEquals(List.of(ClassicalCard.EIGHT_HEART), playableCards);
    }

    @DisplayName("An opponent played trump against the player teammate, player has no trump, he can play any card in his hand")
    @Test
    public void testNotTrumpTrick_trumpCardPlayed_playerHasntTrump() {

        var players = buildPlayers();

        ContreePlayer testedPlayer = players.get(2);
        List<ClassicalCard> playerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE);
        when(testedPlayer.getHand()).thenReturn(playerHand);

        ContreePlayer opponent1 = players.get(1);
        when(opponent1.getHand()).thenReturn(List.of(ClassicalCard.TEN_HEART));

        ContreePlayer teamMate = players.get(0);
        when(teamMate.getHand()).thenReturn(List.of(ClassicalCard.JACK_DIAMOND));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.HEARTS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(teamMate, ClassicalCard.JACK_DIAMOND);
        trick.playerPlays(opponent1,ClassicalCard.TEN_HEART);

        var playableCards = trick.playableCards(testedPlayer);
        assertEquals(playerHand, playableCards);
    }

    @DisplayName("The teammate is winning the trick, player has no card in the suit, any card can be played")
    @Test
    public void testnotTrumpTrick_teamMateIsWinningWithoutTrump() {

        var players = buildPlayers();

        ContreePlayer testedPlayer = players.get(3);
        List<ClassicalCard> playerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE, ClassicalCard.EIGHT_HEART);
        when(testedPlayer.getHand()).thenReturn(playerHand);

        ContreePlayer teamMate = players.get(1);
        when(teamMate.getHand()).thenReturn(List.of(ClassicalCard.ACE_DIAMOND));


        ContreePlayer opponent1 = players.get(0);
        when(opponent1.getHand()).thenReturn(List.of(ClassicalCard.JACK_DIAMOND));

        ContreePlayer opponent2 = players.get(2);
        when(opponent2.getHand()).thenReturn(List.of(ClassicalCard.EIGHT_DIAMOND));

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.HEARTS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(opponent1, ClassicalCard.JACK_DIAMOND);
        trick.playerPlays(teamMate, ClassicalCard.ACE_DIAMOND);
        trick.playerPlays(opponent2, ClassicalCard.EIGHT_DIAMOND);


        var playableCards = trick.playableCards(testedPlayer);
        assertEquals(playerHand, playableCards);
    }

    @DisplayName("The teammate trumped and is winning the trick, any card can be played")
    @Test
    public void testFirstCardPlayed_notTrumpTrick_teamMateIsWinning() {

        var players = buildPlayers();

        ContreePlayer testedPlayer = players.get(3);
        List<ClassicalCard> playerHand = List.of(ClassicalCard.SEVEN_CLUB, ClassicalCard.ACE_CLUB, ClassicalCard.ACE_SPADE, ClassicalCard.EIGHT_HEART);
        when(testedPlayer.getHand()).thenReturn(playerHand);


        ContreePlayer teamMate = players.get(1);
        when(teamMate.getHand()).thenReturn(List.of(ClassicalCard.TEN_HEART));


        when(testedPlayer.sameTeam(teamMate)).thenReturn(true);
        when(teamMate.sameTeam(testedPlayer)).thenReturn(true);


        ContreePlayer opponent1 = players.get(0);
        when(opponent1.getHand()).thenReturn(List.of(ClassicalCard.JACK_DIAMOND));

        ContreePlayer opponent2 = players.get(2);
        when(opponent2.getHand()).thenReturn(List.of(ClassicalCard.EIGHT_HEART));

        when(opponent1.sameTeam(opponent2)).thenReturn(true);
        when(opponent2.sameTeam(opponent1)).thenReturn(true);

        ContreeTrick trick = new ContreeTrick("TEST", players, CardSuit.HEARTS, new ContreeGameEventSender());
        trick.startTrick();

        trick.playerPlays(opponent1, ClassicalCard.JACK_DIAMOND);
        trick.playerPlays(teamMate, ClassicalCard.TEN_HEART);
        trick.playerPlays(opponent2, ClassicalCard.EIGHT_HEART);

        assertSame(testedPlayer.getTeam().orElseThrow(), teamMate.getTeam().orElseThrow());
        assertSame(teamMate, trick.winningPlayer());

        var playableCards = trick.playableCards(testedPlayer);
        assertEquals(playerHand, playableCards);

    }

}
