package is.hi.darts.repository;

import is.hi.darts.model.Friendship;
import is.hi.darts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // Custom query to find all users who are friends with the given user
    @Query("SELECT f.user2 FROM Friendship f WHERE f.user1 = :user " +
            "UNION " +
            "SELECT f.user1 FROM Friendship f WHERE f.user2 = :user")
    List<User> findFriendsByUser(@Param("user") User user);

    // Find a friendship between two users
    @Query("SELECT f FROM Friendship f WHERE (f.user1 = :user1 AND f.user2 = :user2) OR (f.user1 = :user2 AND f.user2 = :user1)")
    Optional<Friendship> findByUsers(@Param("user1") User user1, @Param("user2") User user2);
}

