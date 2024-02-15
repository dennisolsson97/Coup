package se.iths.dennis.coup.game;

import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    private Integer treasury = 50;
    private List<CoupCharacter> courtDeck = new ArrayList<>();

    public Integer getTreasury() {
        return treasury;
    }

    public void setTreasury(Integer transaction) {
        this.treasury += transaction;
    }

    public List<CoupCharacter> getCourtDeck() {
        return courtDeck;
    }

    public void setCourtDeck(List<CoupCharacter> courtDeck) {
        this.courtDeck = courtDeck;
    }

}
