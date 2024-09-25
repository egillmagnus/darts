package is.hi.darts.model;

import java.util.List;

public class Player {
    private String id;

    private int score;

    private List<Round> rounds;

    public Player(String id, int score, List<Round> rounds){
        this.id = id;
        this.score = score;
        this.rounds = rounds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }
}
