package is.hi.darts.dto;

import java.util.List;

public class GameUpdateMessage {
    private Long gameId;
    private List<PlayerScore> playerScores;

    // Constructors
    public GameUpdateMessage() {}

    public GameUpdateMessage(Long gameId, List<PlayerScore> playerScores) {
        this.gameId = gameId;
        this.playerScores = playerScores;
    }

    // Getters and Setters
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public List<PlayerScore> getPlayerScores() {
        return playerScores;
    }

    public void setPlayerScores(List<PlayerScore> playerScores) {
        this.playerScores = playerScores;
    }

    // Nested PlayerScore class
    public static class PlayerScore {
        private Long playerId;
        private int score;

        // Constructors
        public PlayerScore() {}

        public PlayerScore(Long playerId, int score) {
            this.playerId = playerId;
            this.score = score;
        }

        // Getters and Setters
        public Long getPlayerId() {
            return playerId;
        }

        public void setPlayerId(Long playerId) {
            this.playerId = playerId;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }
    }
}
