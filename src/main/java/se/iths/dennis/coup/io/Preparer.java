package se.iths.dennis.coup.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import se.iths.dennis.coup.game.Game;
import se.iths.dennis.coup.game.Player;

public class Preparer {
    Game game;
    Scanner sc = new Scanner(System.in);

    public Preparer(Game game) {
        this.game = game;
    }

    public void createNewPlayers() {
        while (true) {
            System.out.print("number of players:");
            int numberOfPlayers = sc.nextInt();
            sc.nextLine();

            if (2 <= numberOfPlayers && numberOfPlayers <= 6) {
                List<Player> allPlayers = new ArrayList<>();
                System.out.println("Now let all the players type their names!");

                for (int i = 0; i < numberOfPlayers; i++) {
                    System.out.print("name of player number " + (i + 1) + ":");
                    String name = sc.nextLine();
                    allPlayers.add(new Player(i + 1, name));
                }

                game.setAllPlayers(allPlayers);
                break;
            } else {
                System.out.println("Wrong, you must be 2-6 players! Type again!");
            }
        }
    }

    public void prepareNormalGame() {
        for (Player p : game.getAllPlayers()) {
            p.setCoins(2);
            game.getGameBoard().setTreasury(-2);
        }

        dealCharacters(2);
    }

    public void dealCharacters(int numberOfCharacters) {
        for (int i = 0; i < numberOfCharacters; i++) {
            for (Player p : game.getAllPlayers()) {
                p.getCharacters().add(game.getGameBoard().getCourtDeck().get(0));
                game.getGameBoard().getCourtDeck().remove(0);
            }
        }
    }
}