package is.hi.darts.repository;

import is.hi.darts.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository  // Marks this interface as a Spring Data repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Finds a user by their email
    User findByEmail(String email);

    // Checks if a user with the given email exists
    boolean existsByEmail(String email);
}
