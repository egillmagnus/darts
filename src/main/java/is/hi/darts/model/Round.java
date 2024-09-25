package is.hi.darts.model;

public class Round {
    private int roundNumber;
    private int score;

    public Round(int roundNumber, int score){
        this.roundNumber = roundNumber;
        this.score = score;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
