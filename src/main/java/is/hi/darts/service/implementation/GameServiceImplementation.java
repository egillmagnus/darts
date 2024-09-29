package is.hi.darts.service.implementation;

import is.hi.darts.model.Game;
import is.hi.darts.model.GameStatus;
import is.hi.darts.model.Player;
import is.hi.darts.model.User;
import is.hi.darts.repository.GameRepository;
import is.hi.darts.repository.UserRepository;
import is.hi.darts.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GameServiceImplementation implements GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Game startNewGame(Game game) {
        // Save the new game in the repository
        return gameRepository.save(game);
    }

    @Override
    public Game getGameSetup(Long gameId) throws Exception {
        // Retrieve the game setup by its ID
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));
    }

    public void inviteFriendToGame(Long gameId, Long friendId) throws Exception {
        // Find the game by ID
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));

        // Find the user by their ID
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new Exception("Friend not found"));

        // Add the friend as a player to the game
        game.addPlayer(friend);

        // Save the updated game to the database
        gameRepository.save(game);
    }


    @Override
    public void pauseGame(Long gameId) throws Exception {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));

        // Pause logic: You could set a status field in the Game entity (like "PAUSED")
        game.setStatus(GameStatus.PAUSED);
        gameRepository.save(game);
    }

    @Override
    public void resumeGame(Long gameId) throws Exception {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));

        // Resume logic: You could set a status field in the Game entity (like "ACTIVE")
        game.setStatus(GameStatus.ONGOING);
        gameRepository.save(game);
    }

    @Override
    public Game updateGameSetup(Long gameId, Game updatedGame) throws Exception {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));

        // Update the game's settings (game type, participants, etc.)
        game.setPlayers(updatedGame.getPlayers());
        game.setRounds(updatedGame.getRounds());
        game.setGameType(updatedGame.getGameType());

        return gameRepository.save(game);
    }

    @Override
    public void joinMultiplayerGame(Long gameId, Long userId) throws Exception {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // Add the user to the game
        game.addPlayer(user);
        gameRepository.save(game);
    }

    @Override
    public List<Game> getAllGames(String sort) {
        // If a sorting option is provided, handle it
        if ("date".equalsIgnoreCase(sort)) {
            return gameRepository.findAllByOrderByDateAsc();
        }
        // Default to all games with no specific order
        return gameRepository.findAll();
    }


    @Override
    public List<Game> getPaginatedGames(int page, int limit) {
        // Implement pagination logic (use Pageable in GameRepository for pagination)
        // Assuming Pageable support in GameRepository for pagination
        return gameRepository.findAll(PageRequest.of(page, limit)).getContent();
    }

    @Override
    public void submitThrow(Long gameId, int score) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Submit the throw logic (e.g., update the current player's score, advance round)
        // Assuming Game and Round classes handle player scores and current round
        game.submitThrow(score);
        gameRepository.save(game);
    }

    @Override
    public void undoLastThrow(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Undo the last throw (reverse the last action)
        game.undoLastThrow();
        gameRepository.save(game);
    }

    @Override
    public void saveGameState(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Save the current state of the game (no further logic if game state is automatically saved)
        gameRepository.save(game);
    }

    @Override
    public Game getGameWithRounds(Long gameId) {
        // Retrieve the game and its associated rounds (eagerly fetched or loaded as needed)
        return gameRepository.findById(gameId).orElse(null);
    }

    @Override
    public List<User> getGameParticipants(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        // Get the list of players from the game
        List<Player> players = game.getPlayers();

        // Extract player IDs from the Player objects
        List<Long> playerIds = players.stream()
                .map(Player::getId)
                .collect(Collectors.toList());

        // Fetch the corresponding User objects from the UserRepository
        List<User> participants = userRepository.findAllById(playerIds);

        return participants;
    }

    public List<Game> getUserGames(Long userId) {
        return gameRepository.findByPlayersId(userId);
    }

    public void deleteGame(Long gameId) throws Exception {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));
        gameRepository.delete(game);
    }
}
