package sebastien.perpignane.cardgame.game.contree;

import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;

import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MockTrickBuilder {

    private final ContreeTrick trick;

    private MockTrickBuilder() {
        trick = mock(ContreeTrick.class);
    }

    public static MockTrickBuilder builder() {
        return new MockTrickBuilder();
    }

    public MockTrickBuilder withTrumpSuit(CardSuit trumpSuit) {
        when(trick.getTrumpSuit()).thenReturn(trumpSuit); return this;
    }

    public MockTrickBuilder withPlayedCards(List<ContreePlayedCard> playedCards) {
        when(trick.getPlayedCards()).thenReturn(playedCards); return this;
    }

    public MockTrickBuilder withWinningPlayer(ContreePlayer winningPlayer) {
        when(trick.winningPlayer()).thenReturn(winningPlayer); return this;
    }

    public MockTrickBuilder withIsTrumpTrick(boolean isTrumpTrick) {
        when(trick.isTrumpTrick()).thenReturn(isTrumpTrick); return this;
    }

    public MockTrickBuilder withPlayerHand(ContreePlayer player, CardSuit trickSuit) {
        var playerHandAsSet = new HashSet<>(player.getHand());
        when(trick.getPlayerHand(player)).thenReturn(ContreeCard.of(trickSuit, playerHandAsSet)); return this;
    }

    public ContreeTrick build() {
        return trick;
    }
}