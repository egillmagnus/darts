package is.hi.darts.service;

import is.hi.darts.model.Game;
import is.hi.darts.model.User;

import java.util.List;

public interface GameService {

    // Get the setup for a specific game
    Game getGameSetup(Long gameId) throws Exception;

    // Invite friends to a game
    void inviteFriendToGame(Long gameId, Long friendId) throws Exception;

    // Pause a game
    void pauseGame(Long gameId) throws Exception;

    // Resume a paused game
    void resumeGame(Long gameId) throws Exception;

    // Update game setup before starting
    Game updateGameSetup(Long gameId, Game updatedGame) throws Exception;

    Long createNewGame(User user) throws Exception;

    // Join a multiplayer game
    void joinMultiplayerGame(Long gameId, Long userId) throws Exception;

    // Retrieve all games with sorting
    List<Game> getAllGames(String sort);


    // Retrieve paginated list of games
    List<Game> getPaginatedGames(int page, int limit);

    // Submit a player’s throw in a game
    void submitThrow(Long gameId, int score);

    // Undo last throw in a game
    void undoLastThrow(Long gameId);

    // Save game state
    void saveGameState(Long gameId);

    // Retrieve game with rounds and scores
    Game getGameWithRounds(Long gameId);

    // Retrieve game participants
    List<User> getGameParticipants(Long gameId);
    //
    double calculateThreeDartAverage();
}
