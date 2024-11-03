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
import java.util.stream.Collectors;

@Service
public class GameServiceImplementation implements GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Long createNewGame(User user) throws Exception {
        try {
            Game newGame = new Game( user );

            Game savedGame = gameRepository.save(newGame);

            return savedGame.getId();
        } catch (Exception e) {
            throw new Exception("Error creating new game", e);
        }
    }

    @Override
    public Game getGameSetup(Long gameId) throws Exception {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));
    }

    public void inviteFriendToGame(Long gameId, Long friendId) throws Exception {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new Exception("Friend not found"));

        game.addPlayer(friend);

        gameRepository.save(game);
    }


    @Override
    public void pauseGame(Long gameId) throws Exception {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));

        game.setStatus(GameStatus.PAUSED);
        gameRepository.save(game);
    }

    @Override
    public void resumeGame(Long gameId) throws Exception {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));

        game.setStatus(GameStatus.ONGOING);
        gameRepository.save(game);
    }

    @Override
    public Game updateGameSetup(Long gameId, Game updatedGame) throws Exception {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));

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

        game.addPlayer(user);
        gameRepository.save(game);
    }

    @Override
    public List<Game> getAllGames(String sort) {
        if ("date".equalsIgnoreCase(sort)) {
            return gameRepository.findAllByOrderByDateAsc();
        }
        return gameRepository.findAll();
    }


    @Override
    public List<Game> getPaginatedGames(int page, int limit) {
        return gameRepository.findAll(PageRequest.of(page, limit)).getContent();
    }

    @Override
    public void submitThrow(Long gameId, int score) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        game.submitThrow(score);
        gameRepository.save(game);
    }

    @Override
    public void undoLastThrow(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        game.undoLastThrow();
        gameRepository.save(game);
    }

    @Override
    public void saveGameState(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        gameRepository.save(game);
    }

    @Override
    public Game getGameWithRounds(Long gameId) {
        return gameRepository.findById(gameId).orElse(null);
    }

    @Override
    public List<User> getGameParticipants(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        List<Player> players = game.getPlayers();

        List<Long> playerIds = players.stream()
                .map(Player::getId)
                .collect(Collectors.toList());

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
