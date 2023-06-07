package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sebastien.perpignane.cardgame.card.CardSuit;
import sebastien.perpignane.cardgame.card.ClassicalCard;
import sebastien.perpignane.cardgame.card.contree.ContreeCard;
import sebastien.perpignane.cardgame.player.contree.ContreePlayer;
import sebastien.perpignane.cardgame.player.contree.ContreeTeam;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContreePlayedCardTest {

    @DisplayName("contree PlayedCard cannot be built with null player")
    @Test
    void testInvalidConstructorArgument_nullPlayer() {

        var card = new ContreeCard(ClassicalCard.JACK_DIAMOND, CardSuit.HEARTS);
        assertThatExceptionOfType(NullPointerException.class)
            .isThrownBy(
                () -> new ContreePlayedCard(null, card)
            );

    }

    @DisplayName("contree PlayedCard cannot be built if the player is not part of a team")
    @Test
    void testInvalidConstructorArgument_noTeamPlayer() {

        var noTeamPlayer = mock(ContreePlayer.class);
        when(noTeamPlayer.getTeam()).thenReturn(Optional.empty());

        var card = new ContreeCard(ClassicalCard.JACK_DIAMOND, CardSuit.HEARTS);
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(
                () -> new ContreePlayedCard(noTeamPlayer, card)
            );

    }

    @DisplayName("contree PlayedCard cannot be built with null card")
    @Test
    void testInvalidConstructorArgument_nullCard() {

        var player = mock(ContreePlayer.class);
        when(player.getTeam()).thenReturn(Optional.of(ContreeTeam.TEAM1));

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(
                    () -> new ContreePlayedCard(player, null)
                );

    }

}
