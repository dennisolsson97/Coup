package se.iths.dennis.coup;

import se.iths.dennis.coup.game.Game;
import se.iths.dennis.coup.io.GameMenu;
import se.iths.dennis.coup.io.Preparer;
import se.iths.dennis.coup.io.Viewer;

//@SpringBootApplication
public class CoupApplication {

    public static void main(String[] args) {
        //SpringApplication.run(CoupApplication.class, args);
        Game game = new Game();
        game.createGameBoard();
        Preparer preparer = new Preparer(game);
        Viewer viewer = new Viewer(game);
        GameMenu gameMenu = new GameMenu(game, preparer, viewer);
        System.out.println("Welcome to a new round of Coup! How many will play? You can be 2-6 players");
        gameMenu.doPreparations();
    }

}
