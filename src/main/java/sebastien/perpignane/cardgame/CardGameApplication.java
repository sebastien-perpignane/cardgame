package sebastien.perpignane.cardgame;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sebastien.perpignane.cardgame.card.CardSet;
import sebastien.perpignane.cardgame.game.GameEventSender;
import sebastien.perpignane.cardgame.game.GameLifecycleManager;
import sebastien.perpignane.cardgame.game.GameTextDisplayer;

@SpringBootApplication
public class CardGameApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(CardGameApplication.class, args);
	}

	@Override
	public void run(String... args) {
		getGameLifecycleManager().startGame();
	}

	@Bean
	@SuppressWarnings("unused")
	GameTextDisplayer gameTextDisplayer() {
		return new GameTextDisplayer();
	}

	@Bean
	@SuppressWarnings("unused")
	GameEventSender gameEventSender() {
		return new GameEventSender(gameTextDisplayer());
	}

	@Bean
	GameLifecycleManager getGameLifecycleManager() {
		return new GameLifecycleManager();
	}

}
