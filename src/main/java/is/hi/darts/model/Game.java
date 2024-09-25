package is.hi.darts.model;

import java.util.List;

public class Game {
    private Long id;
    private List<Player> players;
    private List<Round> rounds;
    private int currentRound;

    public Game(Long id, List<Player> players, List<Round> rounds, int currentRound) {
        this.id = id;
        this.players = players;
        this.rounds = rounds;
        this.currentRound = currentRound;
    }
}
