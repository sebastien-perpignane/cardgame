package sebastien.perpignane.cardgame;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sebastien.perpignane.cardgame.card.CardSet;

// TODO write integration tests

@SpringBootTest()
class CardgameApplicationTests {

	@Autowired
	private CardSet cardSet;

	@Test
	@DisplayName("Spring app context loading")
	void contextLoads() {

		Assertions.assertNotNull(cardSet);

	}

}
