package sebastien.perpignane.cardgame.game.contree;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.AdditionalAnswers;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.card.CardSuit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// TODO [Unit tests]
public class ContreeTrickInvalidPlayTest extends TestCasesManagingPlayers {

    @BeforeAll
    public static void globalSetUp() {
        initPlayers();
    }

    private ContreeTrick trickWithHeartTrump;

    @BeforeEach
    public void setUp() {
        ContreeTrickPlayers trickPlayers = mock(ContreeTrickPlayers.class);
        when(trickPlayers.getCurrentPlayer()).thenAnswer(AdditionalAnswers.returnsElementsOf(players));

        ContreeDeal deal = mock(ContreeDeal.class);
        when(deal.getTrumpSuit()).thenReturn(CardSuit.HEARTS);

        PlayableCardsFilter filter = mock(PlayableCardsFilter.class);
        when(filter.playableCards(any(), any())).thenReturn(CardSet.GAME_32.getGameCards());

        trickWithHeartTrump = new ContreeTrick(deal, "TEST", trickPlayers, filter);
    }



}
