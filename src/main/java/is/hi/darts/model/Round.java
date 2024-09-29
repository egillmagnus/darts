package is.hi.darts.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Round {

    private int roundNumber;
    private int playerScore;

    public Round() {
    }

    public Round(int roundNumber, int playerScore) {
        this.roundNumber = roundNumber;
        this.playerScore = playerScore;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }
}
