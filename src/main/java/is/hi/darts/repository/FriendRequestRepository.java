package is.hi.darts.repository;

import is.hi.darts.model.FriendRequest;
import is.hi.darts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    // Check if a friend request exists between two users
    boolean existsByRequesterAndRequestedUser(User requester, User requestedUser);

    // Retrieve a friend request between two users
    Optional<FriendRequest> findByRequesterAndRequestedUser(User requester, User requestedUser);
}
