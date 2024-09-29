package is.hi.darts.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<Player> players;

    // Store rounds separately
    @ElementCollection
    @CollectionTable(name = "game_rounds", joinColumns = @JoinColumn(name = "game_id"))
    private List<Round> rounds;

    @Column(name = "current_round")
    private int currentRound;

    @Column(name = "game_type", nullable = false)
    private String gameType;

    @Column(name = "game_date")
    private LocalDateTime date;

    @Column(name = "max_rounds")
    private int maxRounds;

    @Column(name = "is_paused")
    private boolean isPaused;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameStatus status;

    // Store current player as an index instead of the Player object
    @Column(name = "current_player_index")
    private int currentPlayerIndex;

    // Constructors, Getters, and Setters

    public Game() {
    }

    public Game(Long id, List<Player> players, List<Round> rounds, int currentRound, String gameType, int maxRounds, boolean isPaused, GameStatus status) {
        this.id = id;
        this.players = players;
        this.rounds = rounds;
        this.currentRound = currentRound;
        this.gameType = gameType;
        this.maxRounds = maxRounds;
        this.isPaused = isPaused;
        this.status = status;
        this.date = LocalDateTime.now();
        if (players != null && !players.isEmpty()) {
            this.currentPlayerIndex = 0; // Initialize with the first player
        }
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

    public int getMaxRounds() {
        return maxRounds;
    }

    public void setMaxRounds(int maxRounds) {
        this.maxRounds = maxRounds;
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

    // Logic to submit a player's throw
    public void submitThrow(int score) {
        Player currentPlayer = getCurrentPlayer();
        if (currentPlayer == null) {
            throw new RuntimeException("No current player set");
        }

        // Update the current player's score
        currentPlayer.setScore(currentPlayer.getScore() + score);

        // Move to the next round if necessary
        if (currentRound < maxRounds) {
            currentRound++;
            nextPlayer();  // Move to the next player after the current round
        } else {
            // End game or handle final round logic
            status = GameStatus.COMPLETED;
        }
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

        // Move to the previous round and previous player
        currentRound--;
        previousPlayer();
    }

    // Move to the next player
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
