package se.iths.dennis.coup;

import se.iths.dennis.coup.game.Game;
import se.iths.dennis.coup.io.GameMenu;

//@SpringBootApplication
public class CoupApplication {

    public static void main(String[] args) {
        //SpringApplication.run(CoupApplication.class, args);
        Game game = new Game();
        game.createGameBoard();
        GameMenu gameMenu = new GameMenu(game);
        System.out.println("Welcome to a new round of Coup! How many will play? You can be between 2-6 players");
        gameMenu.setUpThePlayers();
    }

}
