package is.hi.darts.service.implementation;

import is.hi.darts.model.*;
import is.hi.darts.repository.FriendRequestRepository;
import is.hi.darts.repository.FriendshipRepository;
import is.hi.darts.repository.GameRepository;
import is.hi.darts.repository.UserRepository;
import is.hi.darts.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final GameRepository gameRepository;

    public UserServiceImplementation(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                                     FriendRequestRepository friendRequestRepository, FriendshipRepository friendshipRepository, GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    public User registerUser(String email, String displayName, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email is already registered.");
        }
        User user = new User(null, email, passwordEncoder.encode(password), displayName);
        return userRepository.save(user);
    }

    @Override
    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials.");
        }
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    // Friend management
    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));

        if (!friendRequestRepository.existsByRequesterAndRequestedUser(user, friend)) {
            FriendRequest friendRequest = new FriendRequest(user, friend);
            friendRequestRepository.save(friendRequest);
        } else {
            throw new RuntimeException("Friend request already sent.");
        }
    }

    // calculates the all-time three dart average for a player
    public double calculateThreeDartAverage(User user) {
        // Retrieve all games the user participated in
        List<Game> userGames = gameRepository.findByPlayersId(user.getId());

        // Accumulators for total scores and total rounds
        double totalScore = 0;
        long totalRounds = 0;

        // Process each game
        for (Game game : userGames) {
            if (game.getStatus() == GameStatus.COMPLETED) { // Only consider completed games
                // Sum the scores for this user in this game
                double gameTotalScore = game.getRounds().stream()
                        .filter(round -> round.getPlayerId().equals(user.getId()))
                        .mapToDouble(Round::getPlayerScore)
                        .sum();

                // Count the rounds for this user in this game
                long gameTotalRounds = game.getRounds().stream()
                        .filter(round -> round.getPlayerId().equals(user.getId()))
                        .count();

                totalScore += gameTotalScore;
                totalRounds += gameTotalRounds;
            }
        }

        // Calculate and return the overall 3 Dart Average
        // Since each round typically consists of 3 darts, the average per 3 darts is:
        // (totalScore / totalRounds)
        return totalRounds > 0 ? (totalScore / totalRounds) : 0;
    }


    public List<Game> getUserCompletedGames(Long userId) {
        return gameRepository.findByStatusAndPlayersId(GameStatus.COMPLETED, userId);
    }

    public List<Map<String, Object>> getLeaderboardStats() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> {
            Map<String, Object> stats = new HashMap<>();
            stats.put("name", user.getDisplayName());
            stats.put("threeDartAverage", calculateThreeDartAverage(user));
            stats.put("first9Average", calculateFirst9Average(user));
            stats.put("winPercentage", calculateWinPercentage(user));
            System.out.println(stats.get("first9Average") + " " + stats.get("name"));
            System.out.println(stats.get("winPercentage") + " " + stats.get("name"));
            System.out.println(stats.get("threeDartAverage") + " " + stats.get("name"));
            return stats;
        }).collect(Collectors.toList());


    }

    public double calculateFirst9Average(User user) {
        // Retrieve all games the user participated in
        List<Game> userGames = gameRepository.findByPlayersId(user.getId());

        // Accumulator for the sum of per-game first 9 averages
        double totalFirst9Averages = 0;
        int completedGamesCount = 0;

        // Process each game
        for (Game game : userGames) {
            if (game.getStatus() == GameStatus.COMPLETED) { // Only consider completed games
                // Get the first 3 rounds (9 darts) for this user in this game
                List<Round> first3Rounds = game.getRounds().stream()
                        .filter(round -> round.getPlayerId().equals(user.getId()))
                        .limit(3)
                        .collect(Collectors.toList());

                // Ensure the player has at least 3 rounds in this game
                if (first3Rounds.size() == 3) {
                    // Sum the scores for the first 3 rounds
                    double gameFirst9Score = first3Rounds.stream()
                            .mapToDouble(Round::getPlayerScore)
                            .sum();

                    // Calculate the game's first 9 average
                    double gameFirst9Average = gameFirst9Score / 3;

                    // Accumulate the game's first 9 average
                    totalFirst9Averages += gameFirst9Average;
                    completedGamesCount++;
                }
            }
        }

        // Calculate and return the overall average of per-game first 9 averages
        return completedGamesCount > 0 ? totalFirst9Averages / completedGamesCount : 0;
    }


    public double calculateWinPercentage(User user) {
        // Retrieve all games the user participated in
        List<Game> userGames = gameRepository.findByPlayersId(user.getId());

        int totalGames = 0;
        int totalWins = 0;

        for (Game game : userGames) {
            if (game.getStatus() == GameStatus.COMPLETED) { // Only consider completed games
                List<Player> players = game.getPlayers();

                // Find the player object for the given user
                Player currentPlayer = players.stream()
                        .filter(player -> player.getId().equals(user.getId()))
                        .findFirst()
                        .orElse(null);

                if (currentPlayer != null) {
                    // Check if the player's legsWon is greater than the other player(s)
                    boolean isWinner = players.stream()
                            .filter(player -> !player.getId().equals(user.getId())) // Exclude the current player
                            .allMatch(opponent -> currentPlayer.getLegsWon() > opponent.getLegsWon());

                    if (isWinner) {
                        totalWins++;
                    }

                    totalGames++;
                }
            }
        }

        // Calculate the win percentage
        return totalGames > 0 ? ((double) totalWins / totalGames) * 100 : 0;
    }








    @Override
    public List<User> getFriendsList(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return friendshipRepository.findFriendsByUser(user);
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    @Override
    public void respondToFriendRequest(Long requestId, boolean accept) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        if (accept) {
            Friendship friendship = new Friendship(request.getRequester(), request.getRequestedUser());
            friendshipRepository.save(friendship);
        }
        friendRequestRepository.delete(request);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findById(friendId).orElseThrow(() -> new RuntimeException("Friend not found"));

        Friendship friendship = friendshipRepository.findByUsers(user, friend)
                .orElseThrow(() -> new RuntimeException("Friendship not found"));
        friendshipRepository.delete(friendship);
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<FriendRequest> getIncomingRequests(Long userId) {
        return friendRequestRepository.findByRequestedUser_Id(userId);
    }

    @Override
    public List<FriendRequest> getOutgoingRequests(Long userId) {
        return friendRequestRepository.findByRequester_Id(userId);
    }


}
