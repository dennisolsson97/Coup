package se.iths.dennis.coup.game;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class Game {

private GameBoard gameBoard;
private List<Player> allPlayers = new ArrayList<>();
private List<String> characterNames = new ArrayList<>();
private String errorMessage;
private List<String> reasons = new ArrayList<>();

    public List<Player> getAllPlayers() {
        return allPlayers;
    }

    public void setAllPlayers(List<Player> allPlayers) {
        this.allPlayers = allPlayers;
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public void createGameBoard() {
        gameBoard = new GameBoard();
        characterNames = Arrays.asList("Duke", "Assassin", "Ambassador", "Captain", "Contessa");
        List<CoupCharacter> characters = new ArrayList<>();
        int number = 0;

        while (characters.size() < 15) {
            int randomIndex = ThreadLocalRandom.current().nextInt(characterNames.size());
            String randomName = characterNames.get(randomIndex);
            
            if(characters.stream().filter(c -> c.getName().equals(randomName)).count() < 3){
                number++;
                characters.add(new CoupCharacter(number, randomName));
            }
        }

        gameBoard.setCourtDeck(characters);
    }

    public Integer income(){
        gameBoard.setTreasury(-1);
        return 1;
    }

    public Integer foreignAid(){
        gameBoard.setTreasury(-2);
        return 2;
    }

    public Integer coup(){
        gameBoard.setTreasury(7);
        return -7;
    }

    public Integer tax(){
        gameBoard.setTreasury(-3);
        return 3;
    }

    public Integer assassinate(){
        gameBoard.setTreasury(3);
        return -3;
    }

    public List<CoupCharacter> getRandomCharacters(Integer numberOfRandomCharacters) {
        List<CoupCharacter> randomCharacters = new ArrayList<>();

        while (randomCharacters.size() < numberOfRandomCharacters) {
            int randomIndex = ThreadLocalRandom.current().nextInt(gameBoard.getCourtDeck().size());
            randomCharacters.add(gameBoard.getCourtDeck().get(randomIndex));
            gameBoard.getCourtDeck().remove(randomIndex);
        }

        return randomCharacters;
    }

    public List<Player> getActiveOpponents(Player p) {
        return allPlayers.stream().filter(o -> !o.equals(p) && !o.isOut()).collect(Collectors.toList());
    }

    public List<Player> getAllOpponents(Player p) {
        return allPlayers.stream().filter(o -> !o.equals(p)).collect(Collectors.toList());
    }

    public void executeCharacter(Player p, CoupCharacter characterToBeExecuted) {
        for (CoupCharacter c:p.getCharacters()) {
            if(c.getCharacterNumber() == characterToBeExecuted.getCharacterNumber()){
                c.setDead(true);
            }
                }
    }

    public String verifyStatement(Player p, String characterName) {
        List<CoupCharacter> livingCharacters = getLivingCharacters(p);

        if(livingCharacters.stream().filter(c -> c.getName().equals(characterName)).count() > 0){
            return "truth";
        }

        return "bluff";
        
    }

    public List<CoupCharacter> getLivingCharacters(Player p) {
        return p.getCharacters().stream().filter(c -> !c.isDead()).collect(Collectors.toList());
    }

    public String checkOwnCharacterAble(Player p) {
        List<CoupCharacter> livingCharacters = getLivingCharacters(p);

        if (livingCharacters.size() == 1 || (livingCharacters.size() == 2 &&
                livingCharacters.get(0).getName().equals(livingCharacters.get(1).getName()))) {

            if (livingCharacters.get(0).getName().equals("Contessa")) {
                errorMessage = "Since Contessa doesn't have an action you can't use it!";
                return "unable";
            }
            else if (livingCharacters.get(0).getName().equals("Assassin") && p.getCoins() < 3) {
                errorMessage = "Since you have less than 3 coins you can't assassinate!";
                return "unable";
            }
            else if (livingCharacters.get(0).getName().equals("Captain") && getOpponentsToStealFrom(p).isEmpty()) {
                errorMessage = "Since none of your opponents have coins you can't steal!";
                return "unable";
            }
            else if (livingCharacters.get(0).getName().equals("Duke") && gameBoard.getTreasury() < 3) {
                errorMessage = "Since the treasury has less than 3 coins you can't tax!";
                return "unable";
            }
            else {
                return "able";
            }
        }

        else {

            if (livingCharacters.stream().filter(c -> c.getName().equals("Contessa")).count() == 1) {

                if (livingCharacters.stream().filter(c -> c.getName().equals("Assassin")).count() == 1 &&
                        p.getCoins() < 3) {
                    errorMessage = "Since Contessa doesn't have an action and you have less than 3 coins you can't use"
                            + " any of your characters!";
                    return "unable";
                }

                else if(livingCharacters.stream().filter(c -> c.getName().equals("Captain")).count() == 1 &&
                        getOpponentsToStealFrom(p).isEmpty()){
                    errorMessage = "Since Contessa doesn't have an action and none opponent has coins " +
                            "you can't use any of your characters!";
                    return "unable";
                }

                else if(livingCharacters.stream().filter(c -> c.getName().equals("Duke")).count() == 1 &&
                        gameBoard.getTreasury() < 3){
                    errorMessage = "Since Contessa doesn't have an action and the treasury has less than 3 coins " +
                            "you can't use any of your characters!";
                    return "unable";
                }

                else {
                    return "able";
                }
            }

            else if(livingCharacters.stream().filter(c -> c.getName().equals("Duke") || c.getName().equals("Assassin")).
                    count() == 2){

                if(gameBoard.getTreasury() < 3 && p.getCoins() < 3){
                    errorMessage = "Since both you and the treasury have less than 3 coins " +
                            "you can't use any of your characters!";

                    return "unable";
                }

                else {
                    return "able";
                }
            }

            else if(livingCharacters.stream().filter(c -> c.getName().equals("Captain") ||
                    c.getName().equals("Assassin")).count() == 2){

                if(p.getCoins() < 3 && getOpponentsToStealFrom(p).isEmpty()){
                    errorMessage = "Since you have less than 3 coins and none opponent has coins " +
                            "you can't use any of your characters!";

                    return "unable";
                }

                else {
                    return "able";
                }
            }

            else {
                return "able";
            }
        }
    }

    public List<Player> getOpponentsToStealFrom(Player p) {
        return getActiveOpponents(p).stream().filter(o -> o.getCoins() > 0).collect(Collectors.toList());
    }

    public void viewCoinsAndCharacters(Player p) {
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

        System.out.println("Press Enter to get back to main menu");
    }

    public void viewOpponentsAndTreasury(Player p) {
        List<Player> opponents = getAllOpponents(p);

        for (Player o:opponents) {
            System.out.println(o + " characters: " + o.getCharacters().get(0).getStatus()
                    + ", " + o.getCharacters().get(1).getStatus());
        }
        System.out.println("Treasury: " + getGameBoard().getTreasury());
        System.out.println("Press Enter to get back to main menu");

    }

    public void getNewCharacter(Player p, String characterName) {
        List<CoupCharacter> livingCharacters = getLivingCharacters(p);

        for (int i = 0; i < livingCharacters.size(); i++) {
            if(livingCharacters.get(i).getName().equals(characterName)){
                gameBoard.getCourtDeck().add(livingCharacters.get(i));
                p.getCharacters().remove(livingCharacters.get(i));
                break;
            }
        }

        List<CoupCharacter> randomCharacters = getRandomCharacters(1);
        p.getCharacters().add(randomCharacters.get(0));
        System.out.println("Alright " + p.getName() + " your new character is: " +
                randomCharacters.get(0).getName());      
    }

    public String checkBluffAble(Player p) {
        List<String> otherCharactersNames = getOtherCharactersNames(p);
        int availableBluffs = 0;
        reasons = new ArrayList<>();

        for (String name:otherCharactersNames) {

            if(name.equals("Contessa")){
                reasons.add("You can't pretend to have Contessa since it doesn't have an action.");
            }

            else if(name.equals("Duke") && gameBoard.getTreasury() < 3){
                reasons.add("You can't pretend to have Duke since the treasury has less than 3 coins.");
            }

            else if(name.equals("Captain") && getOpponentsToStealFrom(p).isEmpty()){
                reasons.add("You can't pretend to have Captain since your opponents are out of coins.");
            }

            else if(name.equals("Assassin") && p.getCoins() < 3){
                reasons.add("You can't pretend to have Assassin since you have less than 3 coins.");
            }

            else {
                availableBluffs++;
            }
        }

        if(availableBluffs > 0){
            return "able";
        }

        else {
            return "unable";
        }
    }

    private List<String> getOtherCharactersNames(Player p) {
        List<String> namesOfLivingCharacters = getLivingCharacters(p)
        .stream()
        .map(c -> c.getName())
        .collect(Collectors.toList());

        if(namesOfLivingCharacters.size() == 1 || namesOfLivingCharacters.size() == 2 &&
                namesOfLivingCharacters.get(0).equals(namesOfLivingCharacters.get(1))){

            return characterNames
            .stream()
            .filter(n -> !n.equals(namesOfLivingCharacters.get(0)))
            .collect(Collectors.toList());
        }

        else {
            return characterNames
            .stream()
            .filter(n -> !n.equals(namesOfLivingCharacters.get(0)) && !n.equals(namesOfLivingCharacters.get(1)))
            .collect(Collectors.toList());
        }
    }

    public List<String> getAvailableBluffs(Player p) {
        List<String> availableBluffs = new ArrayList<>();

        for (String name:getOtherCharactersNames(p)) {

            if(name.equals("Ambassador")){
                availableBluffs.add(name);
            }

            else if(name.equals("Duke") && gameBoard.getTreasury() >= 3){
                availableBluffs.add(name);
            }

            else if(name.equals("Captain") && getOpponentsToStealFrom(p).size() > 0){
                availableBluffs.add(name);
            }

            else if(name.equals("Assassin") && p.getCoins() >= 3){
                availableBluffs.add(name);
            }
        }

        return availableBluffs;
    }

    public List<Player> getRemainingPlayers() {
        return allPlayers.stream().filter(p -> !p.isOut()).collect(Collectors.toList());
    }

    public void steal(Player p, Player opponent) {
        if(opponent.getCoins() == 1){
            p.setCoins(1);
            opponent.setCoins(-1);
        }

        else {
            p.setCoins(2);
            opponent.setCoins(-2);
        }
    }

    public void rearrangePlayers() {
        List<Player> newPlayerOrder = new ArrayList<>();
        List<Player> playersBeforeWinner = new ArrayList<>();

        for (int i = 0; i < allPlayers.size(); i++) {

            do {
                playersBeforeWinner.add(allPlayers.get(i));
                i++;

            } while (!allPlayers.get(i).isLatestWinner());

            while (i < allPlayers.size()){
                newPlayerOrder.add(allPlayers.get(i));
                i++;
            }

            for (Player p:playersBeforeWinner) {
                newPlayerOrder.add(p);
            }
        }

        allPlayers = newPlayerOrder;
    }

    public void resetGameBoard() {
        if(allPlayers.size() == 2){
            gameBoard.getDiscardPile().forEach(c -> gameBoard.getCourtDeck().add(c));
            gameBoard.setDiscardPile(new ArrayList<>());
        }

        for (Player p:allPlayers) {
            if(p.getCoins() > 0){
                gameBoard.setTreasury(p.getCoins());
                p.setCoins(-p.getCoins());
            }

            p.getCharacters().stream().filter(c -> c.isDead()).forEach(c -> c.setDead(false));
            p.getCharacters().stream().forEach(c -> gameBoard.getCourtDeck().add(c));
            p.setCharacters(new ArrayList<>());
        }
    }

    public void resetPlayers() {
        for (Player p:allPlayers) {
            p.setLatestWinner(false);
            p.setOut(false);
        }

    }

    public Player getNextPlayer(int i) {
        while (allPlayers.get(i).isOut()) {
            i++;
            if (i == allPlayers.size()) {
                i = 0;
            }
        }
        return allPlayers.get(i);
    }
}
