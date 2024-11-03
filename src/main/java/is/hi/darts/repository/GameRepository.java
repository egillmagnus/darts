package is.hi.darts.repository;

import is.hi.darts.model.Game;
import is.hi.darts.model.GameStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    // Retrieve all games ordered by date (ascending)
    List<Game> findAllByOrderByDateAsc();

    // Retrieve all games ordered by date (descending)
    List<Game> findAllByOrderByDateDesc();

    // Find games by a specific player's ID
    List<Game> findByPlayersId(Long userId);

    // Retrieve recent games, limiting to the top 10 most recent
    List<Game> findTop10ByOrderByDateDesc();

    // Retrieve games by their status (e.g., ACTIVE, PAUSED)
    List<Game> findByStatus(GameStatus status);

}
