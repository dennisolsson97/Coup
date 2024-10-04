package se.iths.dennis.coup.io;
import se.iths.dennis.coup.game.CoupCharacter;
import se.iths.dennis.coup.game.Game;
import se.iths.dennis.coup.game.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GameMenu {
    Game game;
    Preparer preparer;
    Viewer viewer;
    Scanner sc = new Scanner(System.in);

    public GameMenu(Game game, Preparer preparer, Viewer viewer) {
        this.game = game;
        this.preparer = preparer;
        this.viewer = viewer;
    }

    public void doPreparations(){
        preparer.createNewPlayers();
        decideGameType();
        playGame();
    }

    private void decideGameType() {
        if (game.getAllPlayers().size() == 2) {
            prepare2PlayersGame();
        }

        else {
            preparer.prepareNormalGame();
        }
        viewer.viewCharacters();
    }

    private List<CoupCharacter> selectCharacters(int numberOfCharacters, List<CoupCharacter> characters) {
        List<CoupCharacter> selectedCharacters = new ArrayList<>();

        while (selectedCharacters.size() < numberOfCharacters) {
            System.out.println("Choose 1 of the following characters by typing it's characternumber:");
            characters.forEach(System.out::println);

            System.out.print("choice:");
            int choice = sc.nextInt();
            sc.nextLine();

            if (characters.stream().filter(c -> c.getCharacterNumber() == choice).count() == 1) {
                CoupCharacter selectedCharacter = characters
                        .stream()
                        .filter(c -> c.getCharacterNumber() == choice)
                        .collect(Collectors.toList())
                        .get(0);
                selectedCharacters.add(selectedCharacter);
                characters.remove(selectedCharacter);
            }

            else {
                System.out.println("Wrong, type again!");
                gameContinue();
            }
        }
        return selectedCharacters;
    }

    private void prepare2PlayersGame() {
        game.getAllPlayers().get(0).setCoins(1);
        game.getAllPlayers().get(1).setCoins(2);
        game.getGameBoard().setTreasury(-3);
        preparer.dealCharacters(5);

        for (Player p : game.getAllPlayers()) {
            System.out.println(p.getName() + " it's your turn to choose one of five characters. " +
                    "Make sure that your opponent doesn't look at the screen and then press Enter!");
            sc.nextLine();

            List<CoupCharacter> selectedCharacters = selectCharacters(1, p.getCharacters());
            p.getCharacters().add(selectedCharacters.get(0));
            List<CoupCharacter> noneSelectedCharacters = p.getCharacters()
                    .stream()
                    .filter(c -> c.getCharacterNumber() != selectedCharacters.get(0).getCharacterNumber())
                    .collect(Collectors.toList());

            noneSelectedCharacters.forEach(c -> game.getGameBoard().getDiscardPile().add(c));
            noneSelectedCharacters.forEach(c -> p.getCharacters().remove(c));

            List<CoupCharacter> randomCharacters = game.getRandomCharacters(1);
            p.getCharacters().add(randomCharacters.get(0));
            System.out.println("Finally you got this random character: " + randomCharacters.get(0).getName());
            System.out.println("press Enter!");
            sc.nextLine();
            makeSpace();
        }
    }

    private void playGame() {
        boolean loop = true;
        while (loop) {
            for (int i = 0; i < game.getAllPlayers().size(); i++) {
                Player p = game.getAllPlayers().get(i = game.getNextPlayer(i));

                if (isGameOver(p)) {
                    loop = false;
                    break;
                }

                else {
                    startTurn(p);
                }

                makeSpace();
            }
        }
        startNewGame();
    }

    private void startTurn(Player p) {
        System.out.println("It's the turn of " + p.getName() + ", make sure the right person has the " +
                "computer and then press Enter!");
        sc.nextLine();
        makeSpace();

        if (p.getCoins() >= 10) {
            System.out.println("Alright " + p.getName() + " since you have "
                    + p.getCoins() + " coins you " +
                    "have to launch a coup!");
            launchCoup(p);
        }

        else {
            showMainMenu(p);
        }
    }

    private boolean isGameOver(Player p) {
        if (game.getRemainingPlayers().size() == 1) {
            p.setLatestWinner(true);
            System.out.println("The game is over and the winner is " + p.getName());
            gameContinue();
            return true;
        }
        return false;
    }

    private void startNewGame() {
        System.out.println("Do you want to start a new game?");
        String answer = answerQuestion();

        if (answer.equals("Yes")) {
            game.resetGameBoard();
            System.out.println("Do you want to play with the same players again? If not you will register number of" +
                    " players and their names once again.");
            answer = answerQuestion();

            if (answer.equals("Yes")) {
                if (!game.getAllPlayers().get(0).isLatestWinner()) {
                    game.rearrangePlayers();
                }

                game.resetPlayers();
                decideGameType();
                playGame();
            }

            else if (answer.equals("No")) {
                game.setAllPlayers(new ArrayList<>());
                preparer.createNewPlayers();
                decideGameType();
                playGame();
            }
        }

        else if (answer.equals("No")) {
            System.out.println("Alright see you next time!");
        }
    }

    private void showMainMenu(Player p) {
        boolean loop = true;

        while (loop) {
            List<String> availableCharacters = game.getAvailableStatements(p, game.getCharacterNames(p));
            List<String> availableBluffs = game.getAvailableStatements(p, game.getOtherCharacters(p));
            viewer.viewInformation(p, availableCharacters, availableBluffs);
            System.out.println("It's your turn " + p.getName() + ", choose one of the above options!");
            System.out.print("choice:");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    if(isIncomeAvailable(p)) {
                        p.setCoins(game.income());
                        loop = false;
                    } 
                    break;

                case 2:
                    if (isForeignAidAvailable()) {
                        attemptForeignAid(p);
                        loop = false;
                    }
                    break;

                case 3:
                    if (isCoupAvailable(p)) {
                        launchCoup(p);
                        loop = false;
                    }
                    break;

                case 4:
                    if (isOwnCharacterAvailable(availableCharacters)) {
                        useCharacter(p, getStatement(availableCharacters));
                        loop = false;
                    }
                    break;

                case 5:                
                    if (isBluffAvailable(availableBluffs)) {
                        useCharacter(p, getStatement(availableBluffs));
                        loop = false;
                    }
                    break;

                default:
                    System.out.println("Wrong, type again!");
            }
        }
    }

    private boolean isBluffAvailable(List<String> otherCharacters) {
        if(otherCharacters.size() > 0) return true;
        System.out.println("You can't bluff at the moment.");
        gameContinue();
        makeSpace();
        return false;
    }

    private String getStatement(List<String> statements) {
        if(statements.size() == 1 || statements.size() == 2 && 
        statements.get(0).equals(statements.get(1))) return statements.get(0);
        return selectStatement(statements);
    }

    private String selectStatement(List<String> statements) {
        while (true) {
            viewer.viewStatements(statements);
            System.out.println("Choose statement by typing number in front of it!");
            System.out.print("choice:");
            int choice = sc.nextInt();
            sc.nextLine();

            if ((choice - 1) >= statements.size()) {
                System.out.println("Wrong, type again!");
                gameContinue();
            } 
            
            else return statements.get(choice - 1);   
        }
    }

    private boolean isOwnCharacterAvailable(List<String> ownCharacters) {
        if(ownCharacters.size() > 0) return true;
        System.out.println("You can't use an own character at the moment.");
        gameContinue();
        makeSpace();
        return false;
    }

    private boolean isCoupAvailable(Player p) {
        if(p.getCoins() >= 7) return true;
        System.out.println("You need at least 7 coins to launch a Coup!");
        gameContinue();
        makeSpace();
        return false;
    }

    private boolean isForeignAidAvailable() {
        if(game.getGameBoard().getTreasury() >= 2) return true;
        System.out.println("The treasury has less than 2 coins so you can't do Foreign Aid!");
        gameContinue();
        makeSpace();
        return false;
    }

    private boolean isIncomeAvailable(Player p) {
        if (game.getGameBoard().getTreasury() > 0) return true;
        System.out.println("The treasury is empty so you can't do Income!");
        gameContinue();
        makeSpace();
        return false;
    }

    private void useCharacter(Player p, String statement) {
        if (statement.equals("Duke")) dukeMenu(p);
        
        else if (statement.equals("Assassin")) assassinMenu(p);
        
        else if (statement.equals("Ambassador")) ambassadorMenu(p);
        
        else if (statement.equals("Captain")) captainMenu(p);
    }

    private void assassinMenu(Player p) {
        p.setCoins(game.assassinate());
        Player opponent = getActiveOpponent(p);
        System.out.println("Now claim Assassin and that you want to Assassinate " + opponent.getName());
        System.out.println("Does " + opponent.getName() +
                " challenge your statement/claims Contessa to counteract? " +
                "Please be honest, no cheating!");
        String answer = answerQuestion();

        if (answer.equals("Yes")) {
            answer = challengeOrCounteraction();

            if (answer.equals("Challenge")) {
                System.out.println("Alright, let's verify your statement " + p.getName() + ":");

                if (game.verifyStatement(p, "Assassin").equals("truth")) {
                    loseInfluence(opponent, 2);
                    System.out.println("Well, since you actually had Assassin " + p.getName() + ", " +
                            opponent.getName() + " is now out of the game because your Assassinate went through and " +
                            opponent.getName() + " did lose the challenge!");
                    gameContinue();

                    if (game.getRemainingPlayers().size() > 1) {

                        System.out.println("Alright " + p.getName() + " you will" +
                                " now hand in your Assassin and get a new random character from" +
                                " the Court deck.");

                        gameContinue();
                        game.getNewCharacter(p, "Assassin");
                        gameContinue();
                        makeSpace();
                    }
                }

                else if (game.verifyStatement(p, "Assassin").equals("bluff")) {
                    System.out.println("Well, since you were bluffing " + p.getName()
                            + " you will now lose a character and" + " your Assassinate won't go through!");
                    gameContinue();
                    loseInfluence(p, 1);
                }
            }

            else if (answer.equals("Counteraction")) {
                System.out.println("Alright " + p.getName() + " do you want to challenge the statement of " +
                        opponent.getName() + "? If you don't your Assassinate won't go through " +
                        "but if you do you might lose a character. Choose wisely!");

                answer = answerQuestion();

                if (answer.equals("Yes")) {

                    System.out.println("Alright, let's verify the statement of "
                            + opponent.getName() + ":");

                    if (game.verifyStatement(opponent, "Contessa").equals("truth")) {
                        System.out.println("Well, " + opponent.getName() + " actually had Contessa " + p.getName() +
                                " so you will now lose a character and" +
                                " your Assassinate got blocked!");

                        gameContinue();
                        loseInfluence(p, 1);

                        if (game.getRemainingPlayers().size() > 1) {
                            System.out.println("Now hand over the computer to "
                                    + opponent.getName());

                            System.out.println("Alright " + opponent.getName() + " you will" +
                                    " now hand in your Contessa " + "and get a new random character from" +
                                    " the Court deck.");

                            gameContinue();
                            game.getNewCharacter(opponent, "Contessa");
                            gameContinue();
                            makeSpace();
                        }

                    }

                    else if (game.verifyStatement(opponent, "Contessa").equals("bluff")) {
                        loseInfluence(opponent, 2);
                        System.out.println("Well, since " + opponent.getName() + " was bluffing " +
                                p.getName() + " your Assassinate went through and you won the challenge. " +
                                "Therefore " + opponent.getName() + " is now out of the game!");
                        gameContinue();
                    }
                }

                else if (answer.equals("No")) {
                    System.out.println("Your Assassinate didn't go through " + p.getName());
                    gameContinue();
                }
            }
        }

        else if (answer.equals("No")) {
            System.out.println("Now hand over the computer to "
                    + opponent.getName());
            System.out.println("Hello " + opponent.getName() + " you will now" +
                    " lose a character");

            gameContinue();
            loseInfluence(opponent,1);
        }
    }

    private void captainMenu(Player p) {
        Player opponent = getOpponentWithCoins(p);
        System.out.println("Now claim Captain and that you want to steal from " + opponent.getName());
        System.out.println("Does " + opponent.getName() +
                " challenge your statement/claims Captain or Ambassador to counteract? " +
                "Please be honest, no cheating!");
        String answer = answerQuestion();

        if (answer.equals("Yes")) {
            answer = challengeOrCounteraction();

            if (answer.equals("Challenge")) {
                System.out.println("Alright, let's verify your statement " + p.getName() + ":");

                if (game.verifyStatement(p, "Captain").equals("truth")) {
                    System.out.println("Well, since you actually had Captain " + p.getName() + ", " +
                            opponent.getName() + " will now lose a character and your Steal will go through!");

                    game.steal(p, opponent);
                    System.out.println("Now hand over the computer to " + opponent.getName());
                    System.out.println("Hello " + opponent.getName() + " you will now lose a character!");
                    gameContinue();
                    loseInfluence(opponent,1);

                    if (game.getRemainingPlayers().size() > 1) {
                        System.out.println("Now hand over the computer to "
                                + p.getName());

                        System.out.println("Alright " + p.getName() + " you will" +
                                " now hand in your Captain and get a new random character from" +
                                " the Court deck.");

                        gameContinue();
                        game.getNewCharacter(p, "Captain");
                    }
                }

                else if (game.verifyStatement(p, "Captain").equals("bluff")) {
                    System.out.println("Well, since you were bluffing " + p.getName()
                            + " you will now lose a character and" + " your Steal won't go through!");
                    gameContinue();
                    loseInfluence(p,1);
                }
            }

            else if (answer.equals("Counteraction")) {
                System.out.println("Alright " + p.getName() + " do you want to challenge the statement of " +
                        opponent.getName() + "? If you don't your Steal won't to through but if you do you might lose" +
                        " a character. Chose wisely!");

                answer = answerQuestion();

                if (answer.equals("Yes")) {
                    System.out.println("So does " + opponent.getName() + " claim Captain or Ambassador?");
                    answer = captainOrAmbassador();

                    System.out.println("Alright, let's verify the statement of "
                            + opponent.getName() + ":");

                    game.verifyStatement(opponent, answer);

                    if (game.verifyStatement(opponent, answer).equals("truth")) {

                        System.out.println("Well, " + opponent.getName() + " actually had " +
                                answer + " " + p.getName() + " so you will now lose a character and" +
                                " your Steal got blocked!");

                        gameContinue();
                        loseInfluence(p,1);

                        if (game.getRemainingPlayers().size() > 1) {
                            System.out.println("Now hand over the computer to "
                                    + opponent.getName());

                            System.out.println("Alright " + opponent.getName() + " you will" +
                                    " now hand in your " + answer + " and get a new random character from" +
                                    " the Court deck.");

                            gameContinue();
                            game.getNewCharacter(opponent, answer);
                            gameContinue();
                            makeSpace();
                        }

                    } else if (game.verifyStatement(opponent, answer).equals("bluff")) {
                        game.steal(p, opponent);
                        System.out.println("Your Steal went through " + p.getName()
                                + " because " + opponent.getName() + " was bluffing");

                        System.out.println("Now hand over the computer to "
                                + opponent.getName());

                        System.out.println("Hello " + opponent.getName() + " you will now" +
                                " lose a character");

                        gameContinue();

                        loseInfluence(opponent,1);
                    }
                }

                else if (answer.equals("No")) {
                    System.out.println("Your Steal didn't go through " + p.getName());
                    gameContinue();
                }
            }
        }

        else if (answer.equals("No")) {
            game.steal(p, opponent);
            System.out.println("Your steal went through " + p.getName());
            gameContinue();
        }
    }

    private Player getOpponentWithCoins(Player p) {
        List<Player> opponentsWithCoins = game.getOpponentsWithCoins(p);
        
        if (opponentsWithCoins.size() == 1) {
            return opponentsWithCoins.get(0);
        }
        
        return selectOpponent(opponentsWithCoins);    
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

    private void ambassadorMenu(Player p) {
        System.out.println("Now tell your opponents/opponent that you claim Ambassador and want to make Exchange!");
        System.out.println("Does any opponent challenge your statement? Please be honest, no cheating!");
        String answer = answerQuestion();

        if (answer.equals("Yes")) {
            System.out.println("Alright, let's verify your statement " + p.getName() + ":");

            if (game.verifyStatement(p, "Ambassador").equals("truth")) {
                System.out.println("Well, since you actually had Ambassador " + p.getName() + " the opponent who " +
                        "challenged you will now lose a character and your Exchange will go through!");
                Player opponent = getActiveOpponent(p);
                System.out.println("Now hand over the computer to " + opponent.getName());
                System.out.println("Hello " + opponent.getName() + " you will now lose a character!");
                gameContinue();
                loseInfluence(opponent,1);

                if (game.getRemainingPlayers().size() > 1) {
                    System.out.println("Now hand over the computer to "
                            + p.getName());
                    System.out.println("Alright " + p.getName() + " you will" +
                            " now hand in your Ambassador and get a new random character from" +
                            " the Court deck.");
                    gameContinue();
                    game.getNewCharacter(p, "Ambassador");
                    System.out.println("And now you will make your Exchange!");
                    gameContinue();
                    exchange(p);
                }
            }

            else if (game.verifyStatement(p, "Ambassador").equals("bluff")) {
                System.out.println("Well since you were bluffing " + p.getName() + " you will now lose a character and"
                        + " your Exchange won't go through!");
                gameContinue();
                loseInfluence(p,1);
            }
        }

        else if (answer.equals("No")) {
            exchange(p);
        }
    }

    private void exchange(Player p) {
        List<CoupCharacter> randomCharacters = game.getRandomCharacters(2);
        randomCharacters.forEach(c -> p.getCharacters().add(c));

        System.out.println("Alright " + p.getName() + " you now have these 2 random characters added to your hand: "
                + randomCharacters.get(0).getName() + ", " + randomCharacters.get(1).getName());

        System.out.println("Choose which 2 of your living characters you want to hand in to Court deck by typing"
                + " their characternumbers");

        List<CoupCharacter> selectedCharacters = selectCharacters(2, game.getLivingCharacters(p));
        selectedCharacters.forEach(c -> p.getCharacters().remove(c));
        selectedCharacters.forEach(c -> game.getGameBoard().getCourtDeck().add(c));
    }

    private void dukeMenu(Player p) {
        System.out.println("Now tell your opponents/opponent that you claim Duke and want to make Tax!");
        System.out.println("Does any opponent challenge your statement? Please be honest, no cheating!");
        String answer = answerQuestion();

        if (answer.equals("Yes")) {
            System.out.println("Alright, let's verify your statement " + p.getName() + ":");

            if (game.verifyStatement(p, "Duke").equals("truth")) {
                System.out.println("Well, since you actually had Duke " + p.getName() + " the opponent who " +
                        "challenged you will now lose a character and your Tax will go through!");
                p.setCoins(game.tax());
                Player opponent = getActiveOpponent(p);
                System.out.println("Now hand over the computer to " + opponent.getName());
                System.out.println("Hello " + opponent.getName() + " you will now lose a character!");
                gameContinue();
                loseInfluence(opponent,1);

                if (game.getRemainingPlayers().size() > 1) {
                    makeSpace();
                    System.out.println("Now hand over the computer to "
                            + p.getName());
                    System.out.println("Alright " + p.getName() + " you will" +
                            " now hand in your Duke and get a new random character from" +
                            " the Court deck.");
                    gameContinue();
                    game.getNewCharacter(p, "Duke");
                    gameContinue();
                    makeSpace();
                }
            }

            else if (game.verifyStatement(p, "Duke").equals("bluff")) {
                System.out.println("Well since you were bluffing " + p.getName() + " you will now lose a character!");
                gameContinue();
                loseInfluence(p,1);
            }
        }

        else if (answer.equals("No")) {
            p.setCoins(game.tax());
            System.out.println("Your Tax went through " + p.getName());
            gameContinue();
        }
    }

    private void attemptForeignAid(Player p) {
        System.out.println("Now tell your opponents/opponent that you want to make Foreign Aid!");
        System.out.println("Does any opponent claim Duke to block your Foreign Aid? Please be honest, no cheating!");
        String answer = answerQuestion();

        if (answer.equals("Yes")) {
            System.out.println("Do you want to challenge that opponent? If you don't your" +
                    " Foreign Aid will be blocked and if you do you might lose a character." +
                    " Choose wisely!");
            answer = answerQuestion();

            if (answer.equals("Yes")) {
                Player opponent = getActiveOpponent(p);
                System.out.println("Alright, let's verify the statement of "
                        + opponent.getName() + ":");
                String result = game.verifyStatement(opponent, "Duke");

                if (result.equals("truth")) {
                    System.out.println("Well, " + opponent.getName() + " actually had" +
                            " Duke " + p.getName() + " so you will now lose a character and" +
                            " your Foreign Aid got blocked!");
                    gameContinue();
                    loseInfluence(p,1);

                    if (game.getRemainingPlayers().size() > 1) {
                        System.out.println("Now hand over the computer to "
                                + opponent.getName());
                        System.out.println("Alright " + opponent.getName() + " you will" +
                                " now hand in your Duke and get a new random character from" +
                                " the Court deck.");
                        gameContinue();
                        game.getNewCharacter(opponent, "Duke");
                        gameContinue();
                        makeSpace();
                    }

                } else if (result.equals("bluff")) {
                    p.setCoins(game.foreignAid());
                    System.out.println("Your Foreign Aid went through " + p.getName()
                            + " because " + opponent.getName() + " was bluffing");
                    System.out.println("Now hand over the computer to "
                            + opponent.getName());
                    System.out.println("Hello " + opponent.getName() + " you will now" +
                            " lose a character");
                    gameContinue();
                    loseInfluence(opponent,1);
                }
            }

            else if (answer.equals("No")) {
                System.out.println("Your Foreign Aid got blocked " + p.getName());
                gameContinue();
            }
        }

        else if (answer.equals("No")) {
            p.setCoins(game.foreignAid());
            System.out.println("Your Foreign Aid went through " + p.getName());
            gameContinue();
        }
    }

    private String answerQuestion() {
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

    private void launchCoup(Player p) {
        p.setCoins(game.coup());
        Player opponent = getActiveOpponent(p);
        System.out.println("Now " + p.getName() + " you can hand over the computer to " + opponent.getName());
        System.out.println("Hello " + opponent.getName() + " make sure that your opponents/opponent"
                + " don't look at the screen and then press Enter");
        sc.nextLine();
        loseInfluence(opponent,1);
    }

    private void gameContinue() {
        System.out.println("Press Enter!");
        sc.nextLine();
    }

    private Player getActiveOpponent(Player p) {
        List<Player> activeOpponents = game.getActiveOpponents(p);
        
        if (activeOpponents.size() == 1) return activeOpponents.get(0);

        return selectOpponent(activeOpponents);
    }

    private void loseInfluence(Player p, int executions) {
        if(executions == 1) {
            List<CoupCharacter> livingCharacters = game.getLivingCharacters(p);
            
            if (livingCharacters.size() == 1 || (livingCharacters.size() == 2 &&
                livingCharacters.get(0).getName().equals(livingCharacters.get(1).getName()))) {
            game.executeCharacter(p, livingCharacters.get(0));
        }
        
        else selectSacrifice(p, livingCharacters);
    }

    else game.executeEveryCharacter(p);

        if (game.getLivingCharacters(p).isEmpty()) {
            p.setOut(true);
            game.getGameBoard().setTreasury(p.getCoins());
            p.setCoins(-p.getCoins());
            System.out.println("You have lost both your characters " + p.getName()
                    + " and are now out of the game!");
            gameContinue();
        }
    }

    private void selectSacrifice(Player p, List<CoupCharacter> livingCharacters) {
        System.out.println("Since you got 2 different characters that are alive you can choose wich one of them " +
                "you want to sacrifice.");
        CoupCharacter selectedCharacter = selectCharacters(1, livingCharacters).get(0);
        game.executeCharacter(p, selectedCharacter);
    }

    private Player selectOpponent(List<Player> selectableOpponents) {
        while (true) {
            viewer.viewOpponents(selectableOpponents);
            System.out.println("Choose one opponent by typing his/her player-number:");
            System.out.print("choice:");
            int choice = sc.nextInt();
            sc.nextLine();

            if (selectableOpponents.stream().filter(o -> o.getPlayerNumber() == choice).count() == 0) {
                System.out.println("Wrong, type again!");
                gameContinue();
            } 
            
            else {
                return selectableOpponents
                .stream()
                .filter(o -> o.getPlayerNumber() == choice)
                .collect(Collectors.toList())
                .get(0); 
            }
        }
    }

    private void makeSpace() {
        for (int i = 0; i < 40; i++) {
            System.out.println();
        }
    }
}