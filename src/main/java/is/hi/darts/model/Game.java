package is.hi.darts.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @Column(name = "total_legs")
    private Long totalLegs;


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
        this.totalLegs = 1L;
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
        for (Player player : this.players ) {
            player.setScore(Integer.parseInt(gameType));
        }
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
    public Long getTotalLegs(){
        return totalLegs;
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
        Set<Integer> impossibleFinishes = Set.of(169, 168, 166, 165, 163, 162, 159);

        int currentScore = currentPlayer.getScore();
        int updatedScore = currentScore - score;

        if (score < 0 || score > 180 || impossibleFinishes.contains(currentScore) && score == currentScore) {
            throw new IllegalArgumentException(score + " is invalid");
        }
        // Check if attempting to finish with an impossible score


        if (updatedScore == 0) {
            // Perfect throw to finish at zero
            Round newRound = new Round(currentRound, score, currentPlayer.getId());
            rounds.add(newRound);
            currentPlayer.incrementLegsWon();
            startNewLeg(); // Start a new leg
        } else if (updatedScore < 0 || updatedScore == 1) {
            // Bust condition: do not update score, add a round with 0 score, and move to next player
            Round newRound = new Round(currentRound, 0, currentPlayer.getId());
            rounds.add(newRound);
            currentRound++;
            nextPlayer();
        } else {
            // Valid throw that decreases the score without busting
            currentPlayer.setScore(updatedScore);
            Round newRound = new Round(currentRound, score, currentPlayer.getId());
            rounds.add(newRound);
            currentRound++;
            nextPlayer();
        }
    }


    public Long getCurrentLeg(){
        return (Long) players.get(0).getLegsWon() + players.get(1).getLegsWon() + 1;
    }

    // returns the total score by a player in a game
    public int getTotalScoreForPlayer(Long playerId) {
        return rounds.stream()
                .filter(round -> round.getPlayerId().equals(playerId)) // Filter by playerId
                .mapToInt(Round::getPlayerScore)
                .sum();
    }

    public double getGameThreeDartAverage(Long playerId){
        double totalScore = rounds.stream()
                .filter(round -> round.getPlayerId().equals(playerId)) // Filter by playerId
                .mapToDouble(Round::getPlayerScore)
                .sum();

        long totalRounds = rounds.stream()
                .filter(round -> round.getPlayerId().equals(playerId)) // Filter by playerId
                .count();

        return totalScore / totalRounds;
    }

    public double getGameFirst9Average(Long playerId) {
        // Calculate the sum of scores for the first 3 rounds by the specified player
        double totalScoreFirst3Rounds = rounds.stream()
                .filter(round -> round.getPlayerId().equals(playerId)) // Filter by playerId
                .limit(3) // Limit to the first 3 rounds
                .mapToDouble(Round::getPlayerScore)
                .sum();

        // Count the number of rounds (up to 3) for this player
        long countFirst3Rounds = rounds.stream()
                .filter(round -> round.getPlayerId().equals(playerId)) // Filter by playerId
                .limit(3) // Limit to the first 3 rounds
                .count();

        // Calculate and return the average
        return countFirst3Rounds > 0 ? totalScoreFirst3Rounds / countFirst3Rounds : 0;
    }
    public double getLastScore(Long playerId) {
        for (int i = rounds.size() - 1; i >= 0; i--) {
            Round round = rounds.get(i);
            if (round.getPlayerId().equals(playerId)) {
                return round.getPlayerScore();
            }
        }
        return 0;
    }
    public Long getDartsThrown(Long playerId) {
        long totalRounds = rounds.stream()
                .filter(round -> round.getPlayerId().equals(playerId)) // Correctly filter rounds by playerId
                .count(); // Count the number of filtered rounds

        return totalRounds * 3; // Assuming each round involves 3 darts thrown
    }


    // Logic to undo the last throw
    public void undoLastThrow() {
        if (currentPlayerIndex == -1 || currentRound == 0) {
            throw new RuntimeException("No round to undo");
        }

        // Undo the score from the current player
        Player currentPlayer = getCurrentPlayer();

        Round lastRoundForPlayer = null;
        for (int i = rounds.size() - 1; i >= 0; i--) {
            Round round = rounds.get(i);
            if (round.getPlayerId().equals(currentPlayer.getId())) {
                lastRoundForPlayer = round;
                break;
            }
        }
        if (lastRoundForPlayer != null) {
            // Undo the score from the current player
            int lastScore = lastRoundForPlayer.getPlayerScore();
            currentPlayer.setScore(currentPlayer.getScore() + lastScore);

            // Remove the last round for this player from the rounds list
            rounds.remove(lastRoundForPlayer);
        } else {
            throw new RuntimeException("No round to undo for the current player");
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

    public void startNewLeg() {
        for (Player player : players) {
            player.resetScoreForNewLeg(Integer.parseInt(gameType));
        }
        currentRound = 0;
        currentPlayerIndex = 0;
        totalLegs++;
    }
}
