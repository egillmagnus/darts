package is.hi.darts.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Round {

    private int roundNumber;
    private int playerScore;
    private Long playerId;

    public Round() {
    }

    public Round(int roundNumber, int playerScore, Long playerId) {
        this.roundNumber = roundNumber;
        this.playerScore = playerScore;
        this.playerId = playerId;
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
    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
}
