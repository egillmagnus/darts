package is.hi.darts.repository;

import is.hi.darts.model.GameInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameInviteRepository extends JpaRepository<GameInvite, Long> {
    // Custom methods, if needed, can be added here
    List<GameInvite> findByUserId(Long userId);

    Optional<GameInvite> findByGameIdAndUserId(Long gameId, Long userId);
}