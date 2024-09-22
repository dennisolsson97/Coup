package se.iths.dennis.coup;

import se.iths.dennis.coup.game.Game;
import se.iths.dennis.coup.io.GameMenu;
import se.iths.dennis.coup.io.Preparer;

//@SpringBootApplication
public class CoupApplication {

    public static void main(String[] args) {
        //SpringApplication.run(CoupApplication.class, args);
        Game game = new Game();
        game.createGameBoard();
        Preparer preparer = new Preparer(game);
        GameMenu gameMenu = new GameMenu(game, preparer);
        System.out.println("Welcome to a new round of Coup! How many will play? You can be 2-6 players");
        gameMenu.doPreparations();
        //gameMenu.createNewPlayers();
    }

}
