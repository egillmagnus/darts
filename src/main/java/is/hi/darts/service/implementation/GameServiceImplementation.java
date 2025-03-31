package is.hi.darts.service.implementation;

import is.hi.darts.dto.GameUpdateMessage;
import is.hi.darts.model.*;
import is.hi.darts.repository.GameInviteRepository;
import is.hi.darts.repository.GameRepository;
import is.hi.darts.repository.UserRepository;
import is.hi.darts.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

    public GameServiceImplementation(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Long createNewGame(User user) throws Exception {
        try {
            Game newGame = new Game(user);
            System.out.println("Created game");

            Game savedGame = gameRepository.save(newGame);
            System.out.println("Saved game");
            return savedGame.getId();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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

        messagingTemplate.convertAndSend("/topic/invites", invite);
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
        Long id1 = game.getPlayers().get(0).getId();
        Long id2 = game.getPlayers().get(1).getId();

        sendGameUpdateWebsocketMessage(id1, id2);

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
    public Game updateGameSetup(Long gameId, Game updatedGame, Long userId) throws Exception {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new Exception("Game not found"));

        game.setPlayers(updatedGame.getPlayers());
        game.setRounds(updatedGame.getRounds());
        game.setGameType(updatedGame.getGameType());

        game = gameRepository.save(game);

        Long id1 = game.getPlayers().get(0).getId();
        Long id2 = game.getPlayers().get(1).getId();

        sendGameUpdateWebsocketMessage(id1, id2);

        return game;
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

        Long id1 = game.getPlayers().get(0).getId();
        Long id2 = game.getPlayers().get(1).getId();

        sendGameUpdateWebsocketMessage(id1, id2);
    }

    @Override
    public void undoLastThrow(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        game.undoLastThrow();
        gameRepository.save(game);
        List<GameUpdateMessage.PlayerScore> playerScores = game.getPlayers().stream()
                .map(player -> new GameUpdateMessage.PlayerScore(
                        player.getId(),
                        player.getScore(),
                        game.getGameThreeDartAverage(player.getId()),
                        game.getGameFirst9Average(player.getId()),
                        (int) game.getLastScore(player.getId()),
                        game.getDartsThrown(player.getId()),
                        player.getLegsWon()
                ))
                .collect(Collectors.toList());

        Long id1 = game.getPlayers().get(0).getId();
        Long id2 = game.getPlayers().get(1).getId();

        sendGameUpdateWebsocketMessage(id1, id2);
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
    public void setPlayerLocation(Long gameId, Long playerId, LatLon position) {
        Optional<Game> opGame = gameRepository.findById(gameId);
        if(opGame.isPresent()) {
            Game game = opGame.get();
            game.setPlayerLocationForPlayer(playerId, position);
            gameRepository.save(game);
        }

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
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new Exception("Game not found"));
        if(game.getStatus() != GameStatus.SETUP) {
            throw new Exception("Game not found");
        }
        gameRepository.delete(game);
    }

    public double calculateThreeDartAverage() {
        List<Game> allGames = gameRepository.findAll();
        double totalScore = allGames.stream()
                .flatMap(game -> game.getRounds().stream())
                .mapToInt(Round::getPlayerScore)
                .sum();

        long totalDartsThrown = allGames.stream()
                .mapToLong(game -> game.getRounds().size() * 3L)
                .sum();

        return totalDartsThrown == 0 ? 0 : totalScore / (double) totalDartsThrown;
    }

    private void sendGameUpdateWebsocketMessage(Long userId1, Long userId2) {
        System.out.println("Sending ws message to " + "/topic/game-updates/" + userId1);
        messagingTemplate.convertAndSend("/topic/game-updates/" + userId1, "GAME_UPDATED");
        System.out.println("Sending ws message to " + "/topic/game-updates/" + userId2);
        messagingTemplate.convertAndSend("/topic/game-updates/" + userId2, "GAME_UPDATED");

    }

    public List<Game> getSetupGames(Long userId) {
        return gameRepository.findByStatusAndPlayersId(GameStatus.SETUP, userId);
    }

    public List<Game> getOngoingGames(Long userId) {

    return gameRepository.findByStatusAndPlayersId(GameStatus.ONGOING, userId);
}
    public Map<String, Object> getPlayerStats(Game game, Player player) {
        Map<String, Object> stats = new HashMap<>();

        Long playerId = player.getId();


        stats.put("threeDartAverage", game.getGameThreeDartAverage(playerId));
        stats.put("first9Average", game.getGameFirst9Average(playerId));
        stats.put("bestLeg", game.getBestLegForPlayer(playerId));
        stats.put("worstLeg", game.getWorstLegForPlayer(playerId));
        stats.put("legsWon", player.getLegsWon());
        stats.put("highScore", game.getHighestScoreForPlayer(playerId));
        System.out.println("3dart " + stats.get("threeDartAverage") + "first9 "+ stats.get("first9Average"));

        return stats;
    }
}
