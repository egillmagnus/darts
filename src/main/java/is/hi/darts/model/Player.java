package is.hi.darts.model;

import jakarta.persistence.Embeddable;
import java.util.List;

@Embeddable
public class Player {

    private Long id;
    private int score;

    public Player() {
    }

    public Player(User user) {
        this.id = user.getId(); // Assuming User has an ID field
        this.score = 0; // Initial score is set to 0
    }

    public Player(Long id, int score, List<Round> rounds) {
        this.id = id;
        this.score = score;
    }

    public Long getId() {
        return id;
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
}
