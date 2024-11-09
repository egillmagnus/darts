package is.hi.darts.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.aspectj.runtime.internal.Conversions.longValue;

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

    @ElementCollection
    @CollectionTable(name = "game_legs", joinColumns = @JoinColumn(name = "game_id"))
    private List<Leg> legs = new ArrayList<>();

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
        this.rounds = new ArrayList<>();
        this.legs = new ArrayList<>();
        Leg firstLeg = new Leg(0);
        legs.add(firstLeg);
        this.currentRound = 0;
        this.gameType = "501";
        this.status = GameStatus.SETUP;
        this.date = LocalDateTime.now();
        this.currentPlayerIndex = 0;
        this.totalLegs = 1L;
        startNewLeg();
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
    public void setTotalLegs(long totalLegs){
        this.totalLegs = totalLegs;
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
        Set<Integer> impossibleFinishes = Set.of(168, 165, 162, 159);

        Set<Integer> impossibleScores = Set.of(179, 178, 176, 175, 173, 172, 169, 166, 163);

        int currentScore = currentPlayer.getScore();
        int updatedScore = currentScore - score;

        if (score < 0 || score > 180 || impossibleScores.contains(score) || ( impossibleFinishes.contains(currentScore) && score == currentScore )) {
            throw new IllegalArgumentException(score + " is invalid");
        }


        if (updatedScore == 0) {
            Round newRound = new Round(currentRound, score, currentPlayer.getId());
            rounds.add(newRound);
            finishLeg();
            currentPlayer.incrementLegsWon();
            startNewLeg(); // Start a new leg
        } else if (updatedScore < 0 || updatedScore == 1) {
            Round newRound = new Round(currentRound, 0, currentPlayer.getId());
            rounds.add(newRound);
            currentRound++;
            nextPlayer();
        } else {
            currentPlayer.setScore(updatedScore);
            Round newRound = new Round(currentRound, score, currentPlayer.getId());
            rounds.add(newRound);
            currentRound++;
            nextPlayer();
        }
    }

    private void finishLeg() {
        legs.getLast().setEndIndex(rounds.size() - 1);
        legs.getLast().setWinnerPlayerId(rounds.getLast().getPlayerId());
        legs.add(new Leg(rounds.size()));
    }


    public Long getCurrentLeg(){
        return longValue(legs.size());
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
        if (currentRound == 0 || rounds.isEmpty()) {
            throw new RuntimeException("No round to undo");
        }

        Round lastRound = rounds.get(rounds.size() - 1);
        Player lastPlayer = players.get((currentPlayerIndex - 1 + players.size()) % players.size());

        if (lastPlayer == null) {
            throw new RuntimeException("Player not found for the last round");
        }

        int lastScore = lastRound.getPlayerScore();
        if (lastScore > 0) {
            lastPlayer.setScore(lastPlayer.getScore() + lastScore);
        }

        rounds.remove(lastRound);
        previousPlayer();
        if (currentRound > 0) {
            currentRound--;
        }
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
        if(getCurrentLeg() > totalLegs){
            this.status = GameStatus.COMPLETED;

        }
        else{
            for (Player player : players) {
                player.resetScoreForNewLeg(Integer.parseInt(gameType));
            }
            currentRound = 0;
            currentPlayerIndex = 0;
        }
        System.out.println("max legs are " + totalLegs + "started new leg " + getCurrentLeg() + "and game state is " + status);
    }


}
