package is.hi.darts.dto;

import java.util.List;

public class GameUpdateMessage {
    private Long gameId;
    private List<PlayerScore> playerScores;

    public GameUpdateMessage() {}

    public GameUpdateMessage(Long gameId, List<PlayerScore> playerScores) {
        this.gameId = gameId;
        this.playerScores = playerScores;
    }

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

    public static class PlayerScore {
        private Long playerId;
        private int score;
        private double threeDartAverage;
        private double first9Average;
        private int lastScore;
        private long dartsThrown;
        private long legsWon;

        public PlayerScore() {}

        public PlayerScore(Long playerId, int score, double threeDartAverage, double first9Average, int lastScore, long dartsThrown, long legsWon) {
            this.playerId = playerId;
            this.score = score;
            this.threeDartAverage = threeDartAverage;
            this.first9Average = first9Average;
            this.lastScore = lastScore;
            this.dartsThrown = dartsThrown;
            this.legsWon = legsWon;
        }

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

        public double getThreeDartAverage() {
            return threeDartAverage;
        }

        public void setThreeDartAverage(double threeDartAverage) {
            this.threeDartAverage = threeDartAverage;
        }

        public double getFirst9Average() {
            return first9Average;
        }

        public void setFirst9Average(double first9Average) {
            this.first9Average = first9Average;
        }

        public int getLastScore() {
            return lastScore;
        }

        public void setLastScore(int lastScore) {
            this.lastScore = lastScore;
        }

        public long getDartsThrown() {
            return dartsThrown;
        }

        public void setDartsThrown(long dartsThrown) {
            this.dartsThrown = dartsThrown;
        }

        public long getLegsWon() {
            return legsWon;
        }

        public void setLegsWon(int legsWon) {
            this.legsWon = legsWon;
        }
    }
}
