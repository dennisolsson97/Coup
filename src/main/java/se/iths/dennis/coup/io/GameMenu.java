package se.iths.dennis.coup.io;

import se.iths.dennis.coup.game.CoupCharacter;
import se.iths.dennis.coup.game.Game;
import se.iths.dennis.coup.game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GameMenu {
    Game game;
    Scanner sc = new Scanner(System.in);

    public GameMenu(Game game) {
        this.game = game;
    }

    public void createNewPlayers() {

        while (true) {
            System.out.print("number of players:");
            int numberOfPlayers = sc.nextInt();
            sc.nextLine();

            if (1 < numberOfPlayers && numberOfPlayers < 7) {

                List<Player> allPlayers = new ArrayList<>();

                System.out.println("Now let all the players type their names!");

                for (int i = 0; i < numberOfPlayers; i++) {
                    System.out.println("name of player number " + (i + 1) + ":");
                    String name = sc.nextLine();

                    allPlayers.add(new Player(i + 1, name));
                }

                game.setAllPlayers(allPlayers);
                break;
            } else {
                System.out.println("Wrong, you must be 2-6 players! Type again!");
            }
        }

        if (game.getAllPlayers().size() == 2) {
            prepare2PlayersGame();
        }

        else {
            prepareNormalGame();
        }


    }

    private void prepareNormalGame() {
        for (Player p : game.getAllPlayers()) {

            p.setCoins(2);
            game.getGameBoard().setTreasury(-2);

            List<Integer> selectedPositionsOfCharacters = game.generateRandomCharactersPositions(2);

            List<CoupCharacter> selectedCharacters = game.findCharactersByPosition(selectedPositionsOfCharacters);

            p.setCharacters(selectedCharacters);

            selectedCharacters.forEach(c -> game.getGameBoard().getCourtDeck().remove(c));
        }

        viewCharacters();
    }

    private void prepare2PlayersGame() {

        for (Player p : game.getAllPlayers()) {

            if (p.getPlayerNumber() == game.getAllPlayers().get(0).getPlayerNumber()) {
                p.setCoins(1);
                game.getGameBoard().setTreasury(-1);
            }

            else {
                p.setCoins(2);
                game.getGameBoard().setTreasury(-2);
            }

            List<Integer> selectedPositionsOfCharacters = game.generateRandomCharactersPositions(5);

            List<CoupCharacter> fiveRandomCharacters = game.findCharactersByPosition(selectedPositionsOfCharacters);

            fiveRandomCharacters.forEach(c -> game.getGameBoard().getCourtDeck().remove(c));

            System.out.println(p.getName() + " it's your turn to choose one of five random characters. " +
                    "Make sure that your opponent doesn't look at the screen and then press Enter!");
            sc.nextLine();

            boolean loop = true;

            while (loop) {

                for (int i = 0; i < 5; i++) {
                    System.out.println((i + 1) + ": " + fiveRandomCharacters.get(i).getName());
                }

                System.out.print("number of the character you want to choose:");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        p.getCharacters().add(fiveRandomCharacters.get(0));
                        loop = false;
                        game.makeSpace();
                        break;

                    case 2:
                        p.getCharacters().add(fiveRandomCharacters.get(1));
                        loop = false;
                        game.makeSpace();
                        break;

                    case 3:
                        p.getCharacters().add(fiveRandomCharacters.get(2));
                        loop = false;
                        game.makeSpace();
                        break;

                    case 4:
                        p.getCharacters().add(fiveRandomCharacters.get(3));
                        loop = false;
                        game.makeSpace();
                        break;

                    case 5:
                        p.getCharacters().add(fiveRandomCharacters.get(4));
                        loop = false;
                        game.makeSpace();
                        break;

                    default:
                        System.out.println("Wrong, type again!");
                }
            }

            fiveRandomCharacters.stream().
                    filter(c -> c.getCharacterNumber() != p.getCharacters().get(0).getCharacterNumber()).
                    forEach(c -> game.getDiscardPile().add(c));

        }

        for (Player p : game.getAllPlayers()) {
            List<Integer> selectedPositionOfCharacters = game.generateRandomCharactersPositions(1);

            List<CoupCharacter> selectedCharacters = game.findCharactersByPosition(selectedPositionOfCharacters);

            p.getCharacters().add(selectedCharacters.get(0));

            game.getGameBoard().getCourtDeck().remove(selectedCharacters.get(0));
        }

        viewCharacters();
    }

    private void viewCharacters() {
        for (int i = 0; i < game.getAllPlayers().size(); i++) {

            System.out.println("Alright " + game.getAllPlayers().get(i).getName() +
                    " make sure that your opponents/opponent"
                    + " don't look at the screen and then press Enter");

            sc.nextLine();

            System.out.println("Your characters are: " + game.getAllPlayers().get(i).getCharacters().get(0).getName()
                    + ", "
                    + game.getAllPlayers().get(i).getCharacters().get(1).getName());

            System.out.println("Now press Enter!");
            sc.nextLine();
            game.makeSpace();
        }

        playGame();
    }

    private void playGame() {

        boolean loop = true;
        List<Player> allPlayers = game.getAllPlayers();

        while (loop) {
            

            for (int i = 0; i < allPlayers.size(); i++) {

                while (allPlayers.get(i).isOut()) {
                    i++;

                    if (i == allPlayers.size()) {
                        i = 0;
                    }
                }

                List<Player> remainingPlayers = game.getRemainingPlayers();

                if (remainingPlayers.size() == 1) {
                    remainingPlayers.get(0).setLatestWinner(true);
                    System.out.println("The game is over and the winner is " + remainingPlayers.get(0).getName());
                    System.out.println("Press Enter!");
                    sc.nextLine();
                    loop = false;
                    break;
                }

                if (allPlayers.get(i).getCoins() >= 10) {
                    System.out.println("Alright " + allPlayers.get(i).getName() + " since you have " +
                            allPlayers.get(i).getCoins() + " coins you " +
                            "have to launch a coup!");

                    coupMenu(allPlayers.get(i));

                }

                else {
                    showMainMenu(allPlayers.get(i));
                }

                game.makeSpace();
            }
        }

        continueOrQuitMenu();
    }

    private void continueOrQuitMenu() {
        System.out.println("Do you want to start a new game?");
        String answer = yesOrNoMenu();

        if(answer.equals("Yes")){
            game.resetGameBoard();

            System.out.println("Do you want to play with the same players again? If not you will register number of" +
                    " players and their names once again.");

            answer = yesOrNoMenu();

            if(answer.equals("Yes")){

                if(!game.getAllPlayers().get(0).isLatestWinner()){
                    game.rearrangePlayers();
                }

                game.resetPlayers();

                if(game.getAllPlayers().size() == 2){
                    prepare2PlayersGame();
                }

                else {
                    prepareNormalGame();
                }
            }

            else if(answer.equals("No")){
                game.setAllPlayers(new ArrayList<>());
                createNewPlayers();
            }
        }

        else if(answer.equals("No")){
            System.out.println("Alright see you next time!");
        }
    }

    private void showMainMenu(Player p) {
        boolean loop = true;

        while (loop) {

            System.out.println("===================Main Menu===================");
            System.out.println("1.View your own coins and characters");
            System.out.println("2.View the treasury and your opponents coins and characters");
            System.out.println("3.Income");
            System.out.println("4.Foreign Aid");
            System.out.println("5.Coup");
            System.out.println("6.Use your own character");
            System.out.println("7.Bluff");

            System.out.println("It's your turn " + p.getName() + ", choose one of the above options!");

            System.out.print("choice:");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    game.viewCoinsAndCharacters(p);
                    sc.nextLine();
                    game.makeSpace();
                    break;

                case 2:
                    game.viewOpponentsAndTreasury(p);
                    sc.nextLine();
                    game.makeSpace();
                    break;

                case 3:
                    if (game.getGameBoard().getTreasury() > 0) {
                        p.setCoins(game.income());
                        loop = false;
                    }

                    else {
                        System.out.println("The treasury is empty so you can't do Income!");
                        System.out.println("Press Enter!");
                        sc.nextLine();
                        game.makeSpace();
                    }

                    break;

                case 4:
                    if (game.getGameBoard().getTreasury() >= 2) {
                        foreignAidMenu(p);
                        loop = false;
                    }

                    else {
                        System.out.println("The treasury has less than 2 coins so you can't do " +
                                "Foreign Aid!");
                        System.out.println("Press Enter!");
                        sc.nextLine();
                        game.makeSpace();
                    }

                    break;

                case 5:
                    if (p.getCoins() < 7) {
                        System.out.println("You need at least 7 coins to launch a Coup!");
                        System.out.println("Press Enter!");
                        sc.nextLine();
                        game.makeSpace();
                    }

                    else {
                        coupMenu(p);
                        loop = false;
                    }

                    break;

                case 6:
                    if (game.checkOwnCharacterAble(p).equals("able")) {
                        useOwnCharacterMenu(p);
                        loop = false;
                    }

                    else if (game.checkOwnCharacterAble(p).equals("unable")) {
                        System.out.println(game.getErrorMessage());
                        System.out.println();
                        System.out.println("You need to either bluff or do an action which is not " +
                                "bound to a specific character");
                        System.out.println("Press Enter!");
                        sc.nextLine();
                        game.makeSpace();
                    }

                    break;

                case 7:
                    if (game.checkBluffAble(p).equals("able")) {
                        bluffMenu(p);
                        loop = false;
                    }

                    else if (game.checkBluffAble(p).equals("unable")) {
                        System.out.println("You can't bluff because:");
                        game.getReasons().forEach(System.out::println);
                        System.out.println();
                        System.out.println("You need to either use your own character or do an action " +
                                "which is not bound to a specific character.");
                        System.out.println("Press Enter!");
                        sc.nextLine();
                        game.makeSpace();
                    }

                    break;

                default:
                    System.out.println("Wrong, type again!");
            }
        }
    }

    private void bluffMenu(Player p) {
        List<String> availableBluffs = game.getAvailableBluffs(p);

        if (availableBluffs.size() == 1) {
            System.out.println("Alright " + p.getName() + " you will pretend to have " + availableBluffs.get(0)
                    + " since it's the only available bluff.");
            System.out.println("Press Enter!");
            sc.nextLine();
            characterActionMenu(p, availableBluffs.get(0));
        }

        else {

            while (true) {
                System.out.println("Chose which of the following characters you will pretend to have " +
                        "by typing it's name!");
                availableBluffs.forEach(System.out::println);

                System.out.print("Name of the character:");
                String characterName = sc.nextLine();

                if (availableBluffs.contains(characterName)) {
                    characterActionMenu(p, characterName);
                    break;
                }

                else {
                    System.out.println("Wrong, perhaps you spelled wrong.");
                    System.out.println("Press Enter!");
                    sc.nextLine();
                }
            }
        }
    }

    private void useOwnCharacterMenu(Player p) {
        List<CoupCharacter> livingCharacters = game.getLivingCharacters(p);

        if (livingCharacters.size() == 1 || (livingCharacters.size() == 2 &&
                livingCharacters.get(0).getName().equals(livingCharacters.get(1).getName()))) {

            characterActionMenu(p, livingCharacters.get(0).getName());
        }

        else {
            List<CoupCharacter> availableCharacters = new ArrayList<>();

            for (CoupCharacter c : livingCharacters) {

                if (c.getName().equals("Contessa")) {
                    System.out.println("Since Contessa doesn't have an action you can't use it!");
                }

                else if (c.getName().equals("Assassin") && p.getCoins() < 3) {
                    System.out.println("Since you have less than 3 coins you can't use Assassin!");
                }

                else if (c.getName().equals("Captain") &&
                        game.getOpponentsToStealFrom(p).isEmpty()) {
                    System.out.println("Since your opponent/opponents are out of coins you can't use Captain!");
                }

                else if (c.getName().equals("Duke") && game.getGameBoard().getTreasury() < 3) {
                    System.out.println("Since the treasury has less than 3 coins you can't use Duke!");
                }

                else {
                    availableCharacters.add(c);
                }
            }

            if (availableCharacters.size() == 1) {
                System.out.println("You will use " + availableCharacters.get(0).getName() + " instead!");
                System.out.println("Press Enter!");
                sc.nextLine();
                game.makeSpace();
                characterActionMenu(p, availableCharacters.get(0).getName());
            }

            else {
                selectCharacterToUse(p, availableCharacters);
            }
        }
    }

    private void selectCharacterToUse(Player p, List<CoupCharacter> availableCharacters) {
        System.out.println("Since both your characters are available you can chose witch one of them " +
                "you want to use."
        );

        boolean loop = true;

        while (loop) {
            for (int i = 0; i < availableCharacters.size(); i++) {
                System.out.println((i + 1) + ". " + availableCharacters.get(i).getName());
            }

            System.out.print("choice:");
            int choice = sc.nextInt();
            sc.nextLine();
            game.makeSpace();

            switch (choice) {

                case 1:
                    characterActionMenu(p, availableCharacters.get(0).getName());
                    loop = false;
                    break;

                case 2:
                    characterActionMenu(p, availableCharacters.get(1).getName());
                    loop = false;
                    break;

                default:
                    System.out.println("Wrong, type again!");
                    System.out.println("Press Enter!");
                    sc.nextLine();
            }
        }
    }

    private void characterActionMenu(Player p, String characterName) {
        if (characterName.equals("Duke")) {
            taxMenu(p);
        }

        else if (characterName.equals("Assassin")) {
            p.setCoins(game.assassinate());
            assassinateMenu(p);
        }

        else if (characterName.equals("Ambassador")) {
            exchangeMenu(p);
        }

        else if (characterName.equals("Captain")) {
            stealMenu(p);
        }
    }

    private void assassinateMenu(Player p) {
        List<Player> activeOpponents = game.getActiveOpponents(p);

        Player opponent = new Player();

        if (activeOpponents.size() == 1) {
            opponent = activeOpponents.get(0);
        }

        else {
            System.out.println("Chose the opponent you want to Assassinate by typing his/her player-number");
            opponent = selectOpponent(activeOpponents);
        }

        System.out.println("Now claim Assassin and that you want to Assassinate " + opponent.getName());

        System.out.println("Does " + opponent.getName() +
                " challenge your statement/claims Contessa to counteract? " +
                "Please be honest, no cheating!");

        String answer = yesOrNoMenu();

        if(answer.equals("Yes")){
            answer = challengeOrCounteraction();

            if(answer.equals("Challenge")){
                System.out.println("Alright, let's verify your statement " + p.getName() + ":");

                if (game.verifyStatement(p, "Assassin").equals("truth")) {

                    if(game.getLivingCharacters(opponent).size() == 1){
                        game.executeCharacter(opponent, game.getLivingCharacters(opponent).get(0));
                    }

                    else {
                        game.executeCharacter(opponent, game.getLivingCharacters(opponent).get(0));
                        game.executeCharacter(opponent, game.getLivingCharacters(opponent).get(0));
                    }

                    System.out.println("Well, since you actually had Assassin " + p.getName() + ", " +
                            opponent.getName() + " is now out of the game because your Assassinate went through and " +
                            opponent.getName() + " did lose the challenge!");
                    System.out.println("Press Enter!");
                    sc.nextLine();

                    if (game.getRemainingPlayers().size() > 1) {

                        System.out.println("Alright " + p.getName() + " you will" +
                                " now hand in your Assassin and get a new random character from" +
                                " the Court deck.");

                        System.out.println("Press Enter!");
                        sc.nextLine();
                        game.getANewCharacter(p, "Assassin");
                        System.out.println("Press Enter!");
                        sc.nextLine();
                        game.makeSpace();
                    }
                }

                else if (game.verifyStatement(p, "Assassin").equals("bluff")) {

                    System.out.println("Well, since you were bluffing " + p.getName()
                            + " you will now lose a character and" + " your Assassinate won't go through!");
                    System.out.println("Press Enter!");
                    sc.nextLine();

                    loseInfluenceMenu(p);
                }
            }

            else if(answer.equals("Counteraction")){
                System.out.println("Alright " + p.getName() + " do you want to challenge the statement of " +
                        opponent.getName() + "? If you don't your Assassinate won't go through " +
                        "but if you do you might lose a character. Chose wisely!");

                answer = yesOrNoMenu();

                if(answer.equals("Yes")){

                    System.out.println("Alright, let's verify the statement of "
                            + opponent.getName() + ":");

                    game.verifyStatement(opponent, "Contessa");

                    if (game.verifyStatement(opponent, "Contessa").equals("truth")) {

                        System.out.println("Well, " + opponent.getName() + " actually had Contessa " + p.getName() +
                                " so you will now lose a character and" +
                                " your Assassinate got blocked!");

                        System.out.println("Press Enter!");
                        sc.nextLine();
                        loseInfluenceMenu(p);

                        if (game.getRemainingPlayers().size() > 1) {
                            System.out.println("Now hand over the computer to "
                                    + opponent.getName());

                            System.out.println("Alright " + opponent.getName() + " you will" +
                                    " now hand in your Contessa " + "and get a new random character from" +
                                    " the Court deck.");

                            System.out.println("Press Enter!");
                            sc.nextLine();
                            game.getANewCharacter(opponent, "Contessa");
                            System.out.println("Press Enter!");
                            sc.nextLine();
                            game.makeSpace();
                        }


                    }

                    else if (game.verifyStatement(opponent, "Contessa").equals("bluff")) {

                        if(game.getLivingCharacters(opponent).size() == 1){
                            game.executeCharacter(opponent, game.getLivingCharacters(opponent).get(0));
                        }

                        else {
                            game.executeCharacter(opponent, game.getLivingCharacters(opponent).get(0));
                            game.executeCharacter(opponent, game.getLivingCharacters(opponent).get(0));
                        }

                        System.out.println("Well, since " + opponent.getName() + " was bluffing " +
                                p.getName() + " your Assassinate went through and you won the challenge. " +
                                "Therefore " + opponent.getName() + " is now out of the game!");
                        System.out.println("Press Enter!");
                        sc.nextLine();
                    }
                }

                else if(answer.equals("No")){
                    System.out.println("Your Assassinate didn't go through " + p.getName());
                    System.out.println("Press Enter!");
                    sc.nextLine();
                }
            }
        }

        else if(answer.equals("No")){
            System.out.println("Now hand over the computer to "
                    + opponent.getName());

            System.out.println("Hello " + opponent.getName() + " you will now" +
                    " lose a character");

            System.out.println("Press Enter!");
            sc.nextLine();
            loseInfluenceMenu(opponent);
        }
    }

    private void stealMenu(Player p) {
        List<Player> opponentsToStealFrom = game.getOpponentsToStealFrom(p);

        Player opponent = new Player();

        if (opponentsToStealFrom.size() == 1) {
            opponent = opponentsToStealFrom.get(0);
        }

        else {
            System.out.println("Chose the opponent you want to steal from by typing his/her player-number");
            opponent = selectOpponent(opponentsToStealFrom);
        }

        System.out.println("Now claim Captain and that you want to steal from " + opponent.getName());

        System.out.println("Does " + opponent.getName() +
                " challenge your statement/claims Captain or Ambassador to counteract? " +
                "Please be honest, no cheating!");

        String answer = yesOrNoMenu();

        if(answer.equals("Yes")){
            answer = challengeOrCounteraction();

            if(answer.equals("Challenge")){
                System.out.println("Alright, let's verify your statement " + p.getName() + ":");

                if (game.verifyStatement(p, "Captain").equals("truth")) {
                    System.out.println("Well, since you actually had Captain " + p.getName() + ", " +
                            opponent.getName() + " will now lose a character and your Steal will go through!");

                    System.out.println("Now hand over the computer to " + opponent.getName());
                    System.out.println("Hello " + opponent.getName() + " you will now lose a character!");
                    System.out.println("Press Enter!");
                    sc.nextLine();

                    loseInfluenceMenu(opponent);

                    if (game.getRemainingPlayers().size() > 1) {
                        System.out.println("Now hand over the computer to "
                                + p.getName());

                        System.out.println("Alright " + p.getName() + " you will" +
                                " now hand in your Captain and get a new random character from" +
                                " the Court deck.");

                        System.out.println("Press Enter!");
                        sc.nextLine();
                        game.getANewCharacter(p, "Captain");

                        System.out.println("And now you will make your Steal!");
                        System.out.println("Press Enter!");
                        sc.nextLine();
                        game.steal(p, opponent);
                    }
                }

                else if (game.verifyStatement(p, "Captain").equals("bluff")) {

                    System.out.println("Well, since you were bluffing " + p.getName()
                            + " you will now lose a character and" + " your Steal won't go through!");
                    System.out.println("Press Enter!");
                    sc.nextLine();

                    loseInfluenceMenu(p);
                }
            }

            else if(answer.equals("Counteraction")){
                System.out.println("Alright " + p.getName() + " do you want to challenge the statement of " +
                        opponent.getName() + "? If you don't your Steal won't to through but if you do you might lose" +
                        " a character. Chose wisely!");

                answer = yesOrNoMenu();

                if(answer.equals("Yes")){
                    System.out.println("So does " + opponent.getName() + " claim Captain or Ambassador?");
                    answer = captainOrAmbassador();

                    System.out.println("Alright, let's verify the statement of "
                            + opponent.getName() + ":");

                    game.verifyStatement(opponent, answer);

                    if (game.verifyStatement(opponent, answer).equals("truth")) {

                        System.out.println("Well, " + opponent.getName() + " actually had " +
                                answer + " " + p.getName() + " so you will now lose a character and" +
                                " your Steal got blocked!");

                        System.out.println("Press Enter!");
                        sc.nextLine();
                        loseInfluenceMenu(p);

                        if (game.getRemainingPlayers().size() > 1) {
                            System.out.println("Now hand over the computer to "
                                    + opponent.getName());

                            System.out.println("Alright " + opponent.getName() + " you will" +
                                    " now hand in your " + answer + " and get a new random character from" +
                                    " the Court deck.");

                            System.out.println("Press Enter!");
                            sc.nextLine();
                            game.getANewCharacter(opponent, answer);
                            System.out.println("Press Enter!");
                            sc.nextLine();
                            game.makeSpace();
                        }


                    } else if (game.verifyStatement(opponent, answer).equals("bluff")) {
                        game.steal(p, opponent);
                        System.out.println("Your Steal went through " + p.getName()
                                + " because " + opponent.getName() + " was bluffing");

                        System.out.println("Now hand over the computer to "
                                + opponent.getName());

                        System.out.println("Hello " + opponent.getName() + " you will now" +
                                " lose a character");

                        System.out.println("Press Enter!");
                        sc.nextLine();

                        loseInfluenceMenu(opponent);
                    }
                }

                else if(answer.equals("No")){
                    System.out.println("Your Steal didn't go through " + p.getName());
                    System.out.println("Press Enter!");
                    sc.nextLine();
                }
            }
        }

        else if(answer.equals("No")){
            game.steal(p, opponent);
            System.out.println("Your steal went through " + p.getName());
            System.out.println("Press Enter!");
            sc.nextLine();
        }
    }

    private String captainOrAmbassador() {

        while (true) {

            System.out.println("1.Captain");
            System.out.println("2.Ambassador");

            System.out.print("answer:");
            int answer = sc.nextInt();
            sc.nextLine();

            switch (answer) {

                case 1:
                    return "Captain";

                case 2:
                    return "Ambassador";

                default:
                    System.out.println("Wrong, type again!");
            }
        }
    }

    private String challengeOrCounteraction() {
        System.out.println("So which one is it?");

        while (true) {

            System.out.println("1.Challenge");
            System.out.println("2.Counteraction");

            System.out.print("answer:");
            int answer = sc.nextInt();
            sc.nextLine();

            switch (answer) {

                case 1:
                    return "Challenge";

                case 2:
                    return "Counteraction";

                default:
                    System.out.println("Wrong, type again!");
            }
        }
    }

    private void exchangeMenu(Player p) {
        System.out.println("Now tell your opponents/opponent that you claim Ambassador and want to make Exchange!");

        System.out.println("Does any opponent challenge your statement? Please be honest, no cheating!");

        String answer = yesOrNoMenu();

        if (answer.equals("Yes")) {

            System.out.println("Alright, let's verify your statement " + p.getName() + ":");

            if (game.verifyStatement(p, "Ambassador").equals("truth")) {
                System.out.println("Well, since you actually had Ambassador " + p.getName() + " the opponent who " +
                        "challenged you will now lose a character and your Exchange will go through!");

                Player opponent = new Player();

                if (game.getActiveOpponents(p).size() == 1) {
                    opponent = game.getActiveOpponents(p).get(0);
                }

                else {
                    System.out.println("Which opponent did challenge you? Choose by typing his/her player-number");

                    opponent = selectOpponent(game.getActiveOpponents(p));
                }

                System.out.println("Now hand over the computer to " + opponent.getName());
                System.out.println("Hello " + opponent.getName() + " you will now lose a character!");
                System.out.println("Press Enter!");
                sc.nextLine();

                loseInfluenceMenu(opponent);

                if (game.getRemainingPlayers().size() > 1) {
                    System.out.println("Now hand over the computer to "
                            + p.getName());

                    System.out.println("Alright " + p.getName() + " you will" +
                            " now hand in your Ambassador and get a new random character from" +
                            " the Court deck.");

                    System.out.println("Press Enter!");
                    sc.nextLine();
                    game.getANewCharacter(p, "Ambassador");

                    System.out.println("And now you will make your Exchange!");
                    System.out.println("Press Enter!");
                    sc.nextLine();
                    exchange(p);
                }
            }

            else if (game.verifyStatement(p, "Ambassador").equals("bluff")) {

                System.out.println("Well since you were bluffing " + p.getName() + " you will now lose a character and"
                        + " your Exchange won't go through!");
                System.out.println("Press Enter!");
                sc.nextLine();

                loseInfluenceMenu(p);
            }
        }

        else if (answer.equals("No")) {
            exchange(p);
        }
    }

    private void exchange(Player p) {
        List<Integer> randomPositionsOfCharacters = game.generateRandomCharactersPositions(2);

        List<CoupCharacter> randomCharacters = game.findCharactersByPosition(randomPositionsOfCharacters);

        randomCharacters.forEach(c -> p.getCharacters().add(c));

        randomCharacters.forEach(c -> game.getGameBoard().getCourtDeck().remove(c));

        System.out.println("Alright " + p.getName() + " you now have these 2 random characters added to your hand: "
                + randomCharacters.get(0).getName() + ", " + randomCharacters.get(1).getName());

        while (true) {
            System.out.println("Choose which 2 of your living characters you want to hand back to Court deck by typing"
                    + " their character-numbers");

            game.getLivingCharacters(p).forEach(System.out::println);

            System.out.print("Number of character1:");
            int characterNumber1 = sc.nextInt();
            sc.nextLine();

            System.out.print("Number of character2:");
            int characterNumber2 = sc.nextInt();
            sc.nextLine();

            if (game.getLivingCharacters(p).stream().filter(c -> c.getCharacterNumber() == characterNumber1 ||
                    c.getCharacterNumber() == characterNumber2).count() == 2) {
                List<CoupCharacter> chosenCharacters = new ArrayList<>();

                for (CoupCharacter c : p.getCharacters()) {

                    if (c.getCharacterNumber() == characterNumber1 || c.getCharacterNumber() == characterNumber2) {
                        chosenCharacters.add(c);
                    }
                }

                chosenCharacters.forEach(c -> game.getGameBoard().getCourtDeck().add(c));
                chosenCharacters.forEach(c -> p.getCharacters().remove(c));
                break;
            }

            else {
                System.out.println("Wrong, type again!");
                System.out.println("Press Enter!");
                sc.nextLine();
            }
        }
    }

    private void taxMenu(Player p) {
        System.out.println("Now tell your opponents/opponent that you claim Duke and want to make Tax!");

        System.out.println("Does any opponent challenge your statement? Please be honest, no cheating!");

        String answer = yesOrNoMenu();

        if (answer.equals("Yes")) {

            System.out.println("Alright, let's verify your statement " + p.getName() + ":");

            if (game.verifyStatement(p, "Duke").equals("truth")) {
                System.out.println("Well, since you actually had Duke " + p.getName() + " the opponent who " +
                        "challenged you will now lose a character and your Tax will go through!");

                p.setCoins(game.tax());

                Player opponent = new Player();

                if (game.getActiveOpponents(p).size() == 1) {
                    opponent = game.getActiveOpponents(p).get(0);
                }

                else {
                    System.out.println("Which opponent did challenge you? Choose by typing his/her player-number");

                    opponent = selectOpponent(game.getActiveOpponents(p));
                }

                System.out.println("Now hand over the computer to " + opponent.getName());
                System.out.println("Hello " + opponent.getName() + " you will now lose a character!");
                System.out.println("Press Enter!");
                sc.nextLine();

                loseInfluenceMenu(opponent);

                if(game.getRemainingPlayers().size() > 1) {

                    game.makeSpace();

                    System.out.println("Now hand over the computer to "
                            + p.getName());

                    System.out.println("Alright " + p.getName() + " you will" +
                            " now hand in your Duke and get a new random character from" +
                            " the Court deck.");

                    System.out.println("Press Enter!");
                    sc.nextLine();
                    game.getANewCharacter(p, "Duke");
                    System.out.println("Press Enter!");
                    sc.nextLine();
                    game.makeSpace();
                }
            }

            else if (game.verifyStatement(p, "Duke").equals("bluff")) {

                System.out.println("Well since you were bluffing " + p.getName() + " you will now lose a character!");
                System.out.println("Press Enter!");
                sc.nextLine();

                loseInfluenceMenu(p);
            }
        }

        else if (answer.equals("No")) {
            p.setCoins(game.tax());

            System.out.println("Your Tax went through " + p.getName());
            System.out.println("Press Enter!");
            sc.nextLine();
        }
    }

    private void foreignAidMenu(Player p) {
        System.out.println("Now tell your opponents/opponent that you want to make" +
                " Foreign Aid!");

        System.out.println("Does any opponent claim to have Duke and wants to block your " +
                "Foreign Aid? Please be honest, no cheating!");

        String answer = yesOrNoMenu();

        if (answer.equals("Yes")) {

            System.out.println("Do you want to challenge that opponent? If you don't your" +
                    " Foreign Aid will be blocked and if you do you might lose one character." +
                    " Choose wisely!");

            answer = yesOrNoMenu();

            if (answer.equals("Yes")) {

                Player opponent = new Player();

                if (game.getActiveOpponents(p).size() == 1) {
                    opponent = game.getActiveOpponents(p).get(0);
                }

                else {
                    System.out.println("Which opponent is it? " +
                            "Choose by typing his/her player-number");

                    opponent = selectOpponent(game.getActiveOpponents(p));
                }

                System.out.println("Alright, let's verify the statement of "
                        + opponent.getName() + ":");

                String result = game.verifyStatement(opponent, "Duke");

                if (result.equals("truth")) {
                    System.out.println("Well, " + opponent.getName() + " actually had" +
                            " Duke " + p.getName() + " so you will now lose a character and" +
                            " your Foreign Aid got blocked!");

                    System.out.println("Press Enter!");
                    sc.nextLine();
                    loseInfluenceMenu(p);

                    if (game.getRemainingPlayers().size() > 1) {
                        System.out.println("Now hand over the computer to "
                                + opponent.getName());

                        System.out.println("Alright " + opponent.getName() + " you will" +
                                " now hand in your Duke and get a new random character from" +
                                " the Court deck.");

                        System.out.println("Press Enter!");
                        sc.nextLine();
                        game.getANewCharacter(opponent, "Duke");
                        System.out.println("Press Enter!");
                        sc.nextLine();
                        game.makeSpace();
                    }


                } else if (result.equals("bluff")) {
                    p.setCoins(game.foreignAid());
                    System.out.println("Your Foreign Aid went through " + p.getName()
                            + " because " + opponent.getName() + " was bluffing");

                    System.out.println("Now hand over the computer to "
                            + opponent.getName());

                    System.out.println("Hello " + opponent.getName() + " you will now" +
                            " lose a character");

                    System.out.println("Press Enter!");
                    sc.nextLine();

                    loseInfluenceMenu(opponent);
                }
            }

            else if (answer.equals("No")) {

                System.out.println("Your Foreign Aid got blocked " + p.getName());
                System.out.println("Press Enter!");
                sc.nextLine();
            }
        }

        else if (answer.equals("No")) {
            p.setCoins(game.foreignAid());

            System.out.println("Your Foreign Aid went through " + p.getName());
            System.out.println("Press Enter!");
            sc.nextLine();
        }
    }

    private String yesOrNoMenu() {

        while (true) {

            System.out.println("1.Yes");
            System.out.println("2.No");

            System.out.print("answer:");
            int answer = sc.nextInt();
            sc.nextLine();

            switch (answer) {

                case 1:
                    return "Yes";

                case 2:
                    return "No";

                default:
                    System.out.println("Wrong, type again!");
            }
        }

    }

    private void coupMenu(Player p) {

        p.setCoins(game.coup());

        List<Player> activeOpponents = game.getActiveOpponents(p);

        Player opponent = new Player();

        if (activeOpponents.size() == 1) {
            opponent = activeOpponents.get(0);
        }

        else {
            System.out.println("Chose one opponent to launch a coup at by typing his/her player-number");
            opponent = selectOpponent(activeOpponents);

        }

        System.out.println("Now " + p.getName() + " you can hand over the computer to " + opponent.getName());
        System.out.println("Hello " + opponent.getName() + " make sure that your opponents/opponent"
                + " don't look at the screen and then press Enter");
        sc.nextLine();
        loseInfluenceMenu(opponent);

    }

    private void loseInfluenceMenu(Player p) {

        List<CoupCharacter> livingCharacters = game.getLivingCharacters(p);

        if (livingCharacters.size() == 1 || (livingCharacters.size() == 2 &&
                livingCharacters.get(0).getName().equals(livingCharacters.get(1).getName()))) {

            game.executeCharacter(p, livingCharacters.get(0));

            if(p.isOut()){
                System.out.println("Press Enter!");
                sc.nextLine();
            }

        } else {
            selectCharacterToLoseInfluence(p);
        }

    }

    private void selectCharacterToLoseInfluence(Player p) {
        System.out.println("Since you got 2 different characters that are alive you can chose witch one of them " +
                "you want to sacrifice."
        );

        boolean loop = true;

        while (loop) {
            for (int i = 0; i < p.getCharacters().size(); i++) {
                System.out.println((i + 1) + ". " + p.getCharacters().get(i).getName());
            }

            System.out.print("choice:");
            int choice = sc.nextInt();
            sc.nextLine();
            game.makeSpace();

            switch (choice) {

                case 1:
                    game.executeCharacter(p, p.getCharacters().get(0));
                    loop = false;
                    break;

                case 2:
                    game.executeCharacter(p, p.getCharacters().get(1));
                    loop = false;
                    break;

                default:
                    System.out.println("Wrong, type again!");
                    System.out.println("Press Enter!");
                    sc.nextLine();
            }
        }

    }

    private Player selectOpponent(List<Player> opponents) {

        while (true) {

            for (Player o : opponents) {
                System.out.println(o + " characters: " +
                        o.getCharacters().get(0).getStatus() + ", " +
                        o.getCharacters().get(1).getStatus());
            }

            System.out.print("choice:");
            int choice = sc.nextInt();
            sc.nextLine();

            if (opponents.stream().filter(o -> o.getPlayerNumber() == choice).count() == 0) {
                System.out.println("Wrong, type again!");
                System.out.println("Press Enter");
                sc.nextLine();
            } else {
                for (Player o : opponents) {
                    if (o.getPlayerNumber() == choice) {
                        return o;
                    }
                }
            }

        }
    }
}

