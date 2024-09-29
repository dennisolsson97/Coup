package se.iths.dennis.coup.io;
import java.util.List;
import java.util.Scanner;

import se.iths.dennis.coup.game.CoupCharacter;
import se.iths.dennis.coup.game.Game;
import se.iths.dennis.coup.game.Player;

public class Viewer {
    Game game;
    Scanner sc = new Scanner(System.in);

    public Viewer(Game game) {
        this.game = game;
    }

    public void viewCharacters() {
        for (int i = 0; i < game.getAllPlayers().size(); i++) {
            System.out.println("Alright " + game.getAllPlayers().get(i).getName() +
                    " make sure that your opponents/opponent"
                    + " don't look at the screen and then press Enter");
            sc.nextLine();

            System.out.println("Your characters are: " +
                    game.getAllPlayers().get(i).getCharacters().get(0).getName()
                    + ", "
                    + game.getAllPlayers().get(i).getCharacters().get(1).getName());

            System.out.println("Now press Enter!");
            sc.nextLine();
            makeSpace();
        }
    }

    private void makeSpace() {
        for (int i = 0; i < 40; i++) {
            System.out.println();
        }
    }

    public void viewInformation(Player p) {
        System.out.println("======================== Opponents ========================================");
        viewOpponents(p);
        System.out.println("======================== Game Board =======================================");
        viewGameBoard();
        System.out.println("======================== Your Resources ===================================");
        viewResources(p);
        viewOptions();
    }

    private void viewOptions() {
        System.out.println("=================== Main Menu =============================================");
        System.out.println("1.Income");
        System.out.println("2.Foreign Aid");
        System.out.println("3.Coup");
        System.out.println("4.Use your own character");
        System.out.println("5.Bluff");
    }

    public void viewResources(Player p) {
        System.out.println("Your coins: " + p.getCoins());

        if(p.getCharacters().stream().filter(c -> c.isDead()).count() == 1){
            for (CoupCharacter c: p.getCharacters()) {
                if(c.isDead()){
                    System.out.println("Your dead character: " + c.getName());
                }

                else {
                    System.out.println("Your living character: " + c.getName());
                }
            }

        }

        else {
            System.out.println("Your characters: " + p.getCharacters().get(0).getName()
                    + ", " + p.getCharacters().get(1).getName());
        }
    }

    public void viewOpponents(Player p) {
        List<Player> opponents = game.getAllOpponents(p);

        for (Player o:opponents) {
            System.out.println(o + " characters: " + o.getCharacters().get(0).getStatus()
                    + ", " + o.getCharacters().get(1).getStatus());
        }
    }

    private void viewGameBoard() {
        System.out.println("Treasury: " + game.getGameBoard().getTreasury());
        System.out.println("Characters in Court deck: " + game.getGameBoard().getCourtDeck().size());
    }
}