package is.hi.darts.repository;

import is.hi.darts.model.FriendRequest;
import is.hi.darts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    // Check if a friend request exists between two users
    boolean existsByRequesterAndRequestedUser(User requester, User requestedUser);

    // Retrieve a friend request between two users
    Optional<FriendRequest> findByRequesterAndRequestedUser(User requester, User requestedUser);

    List<FriendRequest> findByRequestedUser_Id(Long userId);

    // Retrieve all outgoing friend requests for a user (requests where the user is the requester)
    List<FriendRequest> findByRequester_Id(Long userId);
}
