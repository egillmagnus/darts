package is.hi.darts.service.implementation;

import is.hi.darts.model.*;
import is.hi.darts.repository.GameInviteRepository;
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
    private GameInviteRepository gameInviteRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Long createNewGame(User user) throws Exception {
        try {
            Game newGame = new Game(user);

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

    public void inviteFriendToGame(Long gameId, Long friendId, Long inviterId) throws Exception {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));
        if (game.getPlayers().size() != 1) {
            throw new Exception("Game is full");
        }

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new Exception("Friend not found"));

        User inviter = userRepository.findById(inviterId)
                .orElseThrow(() -> new Exception("Inviter not found"));

        Long player1Id = game.getPlayers().get(0).getId();

        if (player1Id.equals(friendId)) {
            throw new Exception("Invitation failed: Cannot invite the existing player to the same game.");
        }

        if (!player1Id.equals(inviterId)) {
            throw new Exception("You cant invite to another players game");
        }

        Optional<GameInvite> existingInvite = gameInviteRepository.findByGameIdAndUserId(gameId, friendId);
        if (existingInvite.isPresent()) {
            throw new Exception("Invitation failed: An invitation for this game and friend already exists.");
        }
        GameInvite invite = new GameInvite(gameId, friendId, inviterId);
        gameInviteRepository.save(invite);
    }


    @Override
    public Long acceptInvitation(Long inviteId, Long userId) throws Exception {
        GameInvite invite = gameInviteRepository.findById(inviteId)
                .orElseThrow(() -> new Exception("Invitation not found"));
        Game game = gameRepository.findById(invite.getGameId())
                .orElseThrow(() -> new Exception("Game not found"));
        User user = userRepository.findById(invite.getUserId())
                .orElseThrow(() -> new Exception("User not found"));

        if (!invite.getUserId().equals(userId)) {
            throw new Exception("Unauthorized: Only the invitee can accept this invitation.");
        }

        game.addPlayer(user);
        gameRepository.save(game);

        gameInviteRepository.delete(invite);

        return game.getId();
    }

    @Override
    public void declineInvitation(Long inviteId) {
        gameInviteRepository.deleteById(inviteId);
    }

    @Override
    public List<GameInvite> getInvitationsForUser(Long userId) {
        return gameInviteRepository.findByUserId(userId);
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

        return userRepository.findAllById(playerIds);
    }

    public List<Game> getUserGames(Long userId) {
        return gameRepository.findByPlayersId(userId);
    }

    public void deleteGame(Long gameId) throws Exception {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));
        gameRepository.delete(game);
    }

    public double calculateThreeDartAverage() {
        List<Game> allGames = gameRepository.findAll();
        double totalScore = allGames.stream()
                .flatMap(game -> game.getRounds().stream())
                .mapToInt(Round::getPlayerScore)
                .sum();

        long totalDartsThrown = allGames.stream()
                .mapToLong(game -> game.getRounds().size() * 3L) // Each round represents 3 darts
                .sum();

        return totalDartsThrown == 0 ? 0 : totalScore / (double) totalDartsThrown;
    }
    public List<Game> getSetupGames() {
        // Call the repository method to get games with "ONGOING" status
        return gameRepository.findByStatus(GameStatus.SETUP);
    }

    public List<Game> getOngoingGames() {
    // Call the repository method to get games with "ONGOING" status
    return gameRepository.findByStatus(GameStatus.ONGOING);
}

}
