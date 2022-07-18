package sebastien.perpignane.cardgame;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.game.GameLifecycleManager;
import sebastien.perpignane.cardgame.game.GameObserver;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;

@SpringBootApplication
public class CardgameApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CardgameApplication.class, args);
	}

	@Override
	public void run(String... args) {
		getGameLifecycleManager().startGame();
	}

	@Bean
	CardSet cardSet() {
		return CardSet.GAME_52;
	}

	@Bean
	GameTextDisplayer gameTextDisplayer() {
		return new GameTextDisplayer();
	}

	@Bean
	GameLifecycleManager getGameLifecycleManager() {
		return new GameLifecycleManager();
	}

}
