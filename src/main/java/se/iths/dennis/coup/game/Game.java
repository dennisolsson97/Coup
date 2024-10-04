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

    public void executeCharacter(Player p, CoupCharacter characterToExecute) {
        getLivingCharacters(p).stream().filter(c -> c.equals(characterToExecute)).forEach(c -> c.setDead(true));
    }

    public String verifyStatement(Player p, String statement) {
        List<CoupCharacter> livingCharacters = getLivingCharacters(p);

        if(livingCharacters.stream().filter(c -> c.getName().equals(statement)).count() > 0){
            return "truth";
        }

        return "bluff";
    }

    public List<CoupCharacter> getLivingCharacters(Player p) {
        return p.getCharacters().stream().filter(c -> !c.isDead()).collect(Collectors.toList());
    }

    public List<Player> getOpponentsWithCoins(Player p) {
        return getActiveOpponents(p).stream().filter(o -> o.getCoins() > 0).collect(Collectors.toList());
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

    public List<String> getOtherCharacters(Player p) {
        List<String> ownCharacters = getCharacterNames(p);
        return characterNames.stream().filter(n -> !ownCharacters.contains(n)).collect(Collectors.toList());
    }

    public List<String> getAvailableStatements(Player p, List<String> characterNames) {
        List<String> availableStatements = new ArrayList<>();

        for (String name:characterNames) {
            if(name.equals("Ambassador")){
                availableStatements.add(name);
            }

            else if(name.equals("Duke") && gameBoard.getTreasury() >= 3){
                availableStatements.add(name);
            }

            else if(name.equals("Captain") && getOpponentsWithCoins(p).size() > 0){
                availableStatements.add(name);
            }

            else if(name.equals("Assassin") && p.getCoins() >= 3){
                availableStatements.add(name);
            }
        }

        return availableStatements;
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

    public int getNextPlayer(int i) {
        while (allPlayers.get(i).isOut()) {
            i++;
            if (i == allPlayers.size()) {
                i = 0;
            }
        }
        return i;
    }

    public List<String> getCharacterNames(Player p) {
        return getLivingCharacters(p).stream().map(c -> c.getName()).collect(Collectors.toList());
    }

    public void executeEveryCharacter(Player p) {
        getLivingCharacters(p).forEach(c -> c.setDead(true));
    }
}