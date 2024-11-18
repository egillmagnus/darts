package is.hi.darts.service;

import is.hi.darts.model.FriendRequest;
import is.hi.darts.model.Game;
import is.hi.darts.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;

public interface UserService extends UserDetailsService {
    User registerUser(String email, String displayName, String password);
    User loginUser(String email, String password);
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    User getByEmail(String email);

    User getById(Long id);

    // Friend management methods
    void addFriend(Long userId, Long friendId);  // Add a friend by their user ID
    List<User> getFriendsList(Long userId);      // Retrieve the friends list for the user
    void respondToFriendRequest(Long requestId, boolean accept); // Accept/Reject a friend request
    void removeFriend(Long userId, Long friendId); // Remove a friend by their user ID
    List<FriendRequest> getIncomingRequests(Long userId);  // Get incoming friend requests
    List<FriendRequest> getOutgoingRequests(Long userId);
    double calculateThreeDartAverage(User user);

    public List<Game> getUserCompletedGames(Long userId);

    public double calculateWinPercentage(User user);
    public double calculateFirst9Average(User user);

    List<Map<String, Object>> getLeaderboardStats();
}
