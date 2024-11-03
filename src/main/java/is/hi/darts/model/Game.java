package is.hi.darts.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<Player> players;

    @ElementCollection
    @CollectionTable(name = "game_rounds", joinColumns = @JoinColumn(name = "game_id"))
    private List<Round> rounds;

    @Column(name = "current_round")
    private int currentRound;

    @Column(name = "game_type", nullable = false)
    private String gameType;

    @Column(name = "game_date")
    private LocalDateTime date;

    @Column(name = "is_paused")
    private boolean isPaused;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameStatus status;

    @Column(name = "current_player_index")
    private int currentPlayerIndex;


    public Game() {
    }

    public Game(User user) {
        this.id = null;
        this.players = new ArrayList<>();
        this.players.add(new Player(user));
        this.rounds = new ArrayList<>();;
        this.currentRound = 0;
        this.gameType = "501";
        this.status = GameStatus.SETUP;
        this.date = LocalDateTime.now();
        this.currentPlayerIndex = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(User user) {
        this.players.add(new Player(user));
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public Player getCurrentPlayer() {
        if (players != null && !players.isEmpty() && currentPlayerIndex >= 0 && currentPlayerIndex < players.size()) {
            return players.get(currentPlayerIndex);
        }
        return null;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        if (currentPlayerIndex >= 0 && currentPlayerIndex < players.size()) {
            this.currentPlayerIndex = currentPlayerIndex;
        } else {
            throw new IndexOutOfBoundsException("Invalid player index.");
        }
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void submitThrow(int score) {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null) {
            throw new RuntimeException("No current player set");
        }

        currentPlayer.setScore(currentPlayer.getScore() + score);

        currentRound++;
        nextPlayer();
    }

    // Logic to undo the last throw
    public void undoLastThrow() {
        if (currentPlayerIndex == -1 || currentRound == 0) {
            throw new RuntimeException("No round to undo");
        }

        // Undo the score from the current player
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer != null) {
            int lastScore = rounds.get(currentRound - 1).getPlayerScore();
            currentPlayer.setScore(currentPlayer.getScore() - lastScore);
        }

        currentRound--;
        previousPlayer();
    }

    private void nextPlayer() {
        if (players != null && !players.isEmpty()) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }

    // Move to the previous player
    private void previousPlayer() {
        if (players != null && !players.isEmpty()) {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
        }
    }
}
