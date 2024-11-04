package is.hi.darts.model;

import jakarta.persistence.Embeddable;
import java.util.List;

@Embeddable
public class Player {

    private Long id;
    private int score;
    private String name;

    private Long legsWon;


    public Player() {
    }

    public Player(User user) {
        this.id = user.getId();
        this.name = user.getDisplayName();
        this.score = 501;
        this.legsWon = 0L;
    }

    public Player(Long id, int score, List<Round> rounds) {
        this.id = id;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Long getLegsWon() {
        return legsWon;
    }

    public void setLegsWon(long legsWon) {
        this.legsWon = legsWon;
    }

    public void incrementLegsWon() {
        this.legsWon++;
    }

    public void resetScoreForNewLeg(int maxScore) {
        this.score = maxScore;
    }
}
