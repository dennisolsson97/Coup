package se.iths.dennis.coup;

import se.iths.dennis.coup.game.Game;
import se.iths.dennis.coup.io.GameMenu;

//@SpringBootApplication
public class CoupApplication {

    public static void main(String[] args) {
        //SpringApplication.run(CoupApplication.class, args);
        Game game = new Game();
        game.prepareNewGame();
        GameMenu gameMenu = new GameMenu(game);
        gameMenu.setUpThePlayers();
    }

}
