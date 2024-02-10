package se.iths.dennis.coup.game;

import org.springframework.web.context.annotation.SessionScope;
import java.util.ArrayList;
import java.util.List;

@SessionScope
public class Player {

    private Integer playerNumber = 0;
    private String name;
    private Integer coins = 0;
    private List<CoupCharacter> characters = new ArrayList<>();
    private boolean isOut;
    private boolean isLatestWinner;

    public Player() {
    }

    public Player(Integer playerNumber, String name) {
        this.playerNumber = playerNumber;
        this.name = name;
    }

    public Integer getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(Integer playerNumber) {
        this.playerNumber = playerNumber;
    }

    public Integer getCoins() {
        return coins;
    }

    public void setCoins(Integer coins) {
        this.coins = this.coins + coins;
    }

    public List<CoupCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(List<CoupCharacter> characters) {
        this.characters = characters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOut() {
        return isOut;
    }

    public void setOut(boolean out) {
        isOut = out;
    }

    public boolean isLatestWinner() {
        return isLatestWinner;
    }

    public void setLatestWinner(boolean latestWinner) {
        isLatestWinner = latestWinner;
    }

    @Override
    public String toString() {
        return "Player{" +
                "playerNumber=" + playerNumber +
                ", name='" + name + '\'' +
                ", coins=" + coins +
                '}';
    }
}
