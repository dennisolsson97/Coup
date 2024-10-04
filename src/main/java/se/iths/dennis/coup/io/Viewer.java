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

    public void viewInformation(Player p, List<String> availableCharacters, List<String> availableBluffs) {
        System.out.println("======================== Opponents ========================================");
        viewOpponents(game.getAllOpponents(p));
        System.out.println("======================== Game Board =======================================");
        viewGameBoard();
        System.out.println("======================== Your Resources ===================================");
        viewResources(p);
        System.out.println("======================== Possible Statements ===============================");
        viewAvailableCharacters(availableCharacters);
        viewAvailableBluffs(availableBluffs);
        System.out.println("=================== Main Menu ==============================================");
        viewOptions();
    }

    private void viewAvailableBluffs(List<String> availableBluffs) {
        if(availableBluffs.isEmpty()) System.out.println("You can't bluff at the moment");

        else {
            System.out.println("You can pretend to have:");
            availableBluffs.forEach(System.out::println);
        }
    }

    private void viewAvailableCharacters(List<String> availableCharacters) {
        if(availableCharacters.isEmpty()) System.out.println("You can't use an own character at the moment");
        
        else if(availableCharacters.size() == 1 || availableCharacters.size() == 2 && 
        availableCharacters.get(0).equals(availableCharacters.get(1))) {
            System.out.println("Of your own characters you can use: " + availableCharacters.get(0));
        }

        else System.out.println("Of your own characters you can use: " + availableCharacters.get(0) + ", " +
        availableCharacters.get(1));
    }

    private void viewOptions() {
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

    public void viewOpponents(List<Player> opponents) {
        for (Player o:opponents) {
            System.out.println(o + " characters: " + o.getCharacters().get(0).getStatus()
                    + ", " + o.getCharacters().get(1).getStatus());
        }
    }

    private void viewGameBoard() {
        System.out.println("Treasury: " + game.getGameBoard().getTreasury());
        System.out.println("Characters in Court deck: " + game.getGameBoard().getCourtDeck().size());
    }

    public void viewStatements(List<String> statements) {
        for (int i = 0; i < statements.size(); i++) {
            System.out.println((i + 1) + "." + statements.get(i));
        }
    }
}