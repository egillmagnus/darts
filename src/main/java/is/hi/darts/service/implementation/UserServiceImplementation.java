package is.hi.darts.service.implementation;

import is.hi.darts.model.FriendRequest;
import is.hi.darts.model.Friendship;
import is.hi.darts.model.User;
import is.hi.darts.repository.FriendRequestRepository;
import is.hi.darts.repository.FriendshipRepository;
import is.hi.darts.repository.UserRepository;
import is.hi.darts.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;

    public UserServiceImplementation(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder,
                                     FriendRequestRepository friendRequestRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.friendRequestRepository = friendRequestRepository;
        this.friendshipRepository = friendshipRepository;
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

        // Check if friend request already exists
        if (!friendRequestRepository.existsByRequesterAndRequestedUser(user, friend)) {
            FriendRequest friendRequest = new FriendRequest(user, friend);
            friendRequestRepository.save(friendRequest);
        } else {
            throw new RuntimeException("Friend request already sent.");
        }
    }

    @Override
    public List<User> getFriendsList(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return friendshipRepository.findFriendsByUser(user);
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
}
