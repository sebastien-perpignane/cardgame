package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ContreePlayedCardTest {

    @DisplayName("contree PlayedCard cannot be built with null player")
    @Test
    public void testInvalidConstructorArgument_nullPlayer() {

        assertThrows(
            RuntimeException.class,
            () -> new ContreePlayedCard(null, new ContreeCard(ClassicalCard.JACK_DIAMOND, CardSuit.HEARTS))
        );

    }

    @DisplayName("contree PlayedCard cannot be built if the player is not part of a team")
    @Test
    public void testInvalidConstructorArgument_noTeamPlayer() {

        var noTeamPlayer = mock(ContreePlayer.class);
        when(noTeamPlayer.getTeam()).thenReturn(Optional.empty());

        assertThrows(
            RuntimeException.class,
            () -> new ContreePlayedCard(noTeamPlayer, new ContreeCard(ClassicalCard.JACK_DIAMOND, CardSuit.HEARTS))
        );

    }

    @DisplayName("contree PlayedCard cannot be built with null card")
    @Test
    public void testInvalidConstructorArgument_nullCard() {

        var player = mock(ContreePlayer.class);
        when(player.getTeam()).thenReturn(Optional.of(ContreeTeam.TEAM1));

        assertThrows(
            RuntimeException.class,
            () -> new ContreePlayedCard(player, null)
        );

    }

}
