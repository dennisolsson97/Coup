package se.iths.dennis.coup.game;


public class CoupCharacter {
    private Integer characterNumber = 0;
    private String name;
    private boolean isDead;

    public CoupCharacter() {
    }

    public CoupCharacter(String name) {
        this.name = name;
    }

    public Integer getCharacterNumber() {
        return characterNumber;
    }

    public void setCharacterNumber(Integer characterNumber) {
        this.characterNumber = characterNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        if (isDead) {
            return name;
        }
            return "unknown";
        
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    @Override
    public String toString() {
        return "CoupCharacter{" +
                "characterNumber=" + characterNumber +
                ", name='" + name + '\'' +
                ", isDead=" + isDead +
                '}';
    }
}
