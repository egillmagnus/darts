package is.hi.darts.service;

import is.hi.darts.model.Game;
import is.hi.darts.model.GameInvite;
import is.hi.darts.model.Player;
import is.hi.darts.model.User;

import java.util.List;
import java.util.Map;

public interface GameService {

    // Get the setup for a specific game
    Game getGameSetup(Long gameId) throws Exception;

    // Invite friends to a game
    void inviteFriendToGame(Long gameId, Long friendId, Long userId) throws Exception;

    // Accept an invitation to join a game
    Long acceptInvitation(Long inviteId, Long userId) throws Exception;

    public void deleteGame(Long gameId) throws Exception;

    public Map<String, Object> getPlayerStats(Game game, Player player);

    // Decline an invitation (delete it)
    void declineInvitation(Long inviteId) throws Exception;

    // Retrieve all invitations for a given user
    List<GameInvite> getInvitationsForUser(Long userId);

    // Pause a game
    void pauseGame(Long gameId) throws Exception;

    // Resume a paused game
    void resumeGame(Long gameId) throws Exception;

    // Update game setup before starting
    Game updateGameSetup(Long gameId, Game updatedGame) throws Exception;

    Long createNewGame(User user) throws Exception;

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

    List<Game> getOngoingGames(Long userId);
    List<Game> getSetupGames(Long userId);
    List<Game> getUserCompletedGames(Long userId);
}

