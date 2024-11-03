package is.hi.darts.controller;

import is.hi.darts.model.Game;
import is.hi.darts.model.User;
import is.hi.darts.service.GameService;
import is.hi.darts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @GetMapping("/{gameId}/setup")
    public String getGameSetup(@PathVariable Long gameId, Model model) {
        try {
            Game gameSetup = gameService.getGameSetup(gameId);
            model.addAttribute("gameId", gameSetup.getId());
            model.addAttribute("gameType", gameSetup.getGameType());
            model.addAttribute("players", gameSetup.getPlayers());
            return "games";
        } catch (Exception e) {
            return "error";
        }
    }

    // Start a New Game
    @PostMapping("/")
    public ResponseEntity<Void> createNewGame() {
        try {
            System.out.println("Creating a game");
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User currentUser = userService.getByEmail(userDetails.getUsername());

            Long gameId = gameService.createNewGame(currentUser);

            return ResponseEntity.status(302).header("Location", "/games/" + gameId + "/setup").build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    // Invite Friends to a Game
    @PostMapping("/{gameId}/invite")
    public ResponseEntity<String> inviteFriend(@PathVariable Long gameId, @RequestBody Long friendId) {
        try {
            gameService.inviteFriendToGame(gameId, friendId);
            return ResponseEntity.ok("Friend invited to the game.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to invite friend: " + e.getMessage());
        }
    }

    // Join a Multiplayer Game
    @PostMapping("/{gameId}/join")
    public ResponseEntity<String> joinMultiplayerGame(@PathVariable Long gameId, @RequestParam Long userId) {
        try {
            gameService.joinMultiplayerGame(gameId, userId);
            return ResponseEntity.ok("User successfully joined the multiplayer game.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to join the game: " + e.getMessage());
        }
    }

    // Pause a Game
    @PostMapping("/{gameId}/pause")
    public ResponseEntity<String> pauseGame(@PathVariable Long gameId) {
        try {
            gameService.pauseGame(gameId);
            return ResponseEntity.ok("Game paused.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to pause the game: " + e.getMessage());
        }
    }

    // Resume a Game
    @PostMapping("/{gameId}/resume")
    public ResponseEntity<String> resumeGame(@PathVariable Long gameId) {
        try {
            gameService.resumeGame(gameId);
            return ResponseEntity.ok("Game resumed.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to resume the game: " + e.getMessage());
        }
    }

    // Update Game Setup
    @PutMapping("/{gameId}/setup")
    public ResponseEntity<Game> updateGameSetup(@PathVariable Long gameId, @RequestBody Game updatedGame) {
        try {
            Game game = gameService.updateGameSetup(gameId, updatedGame);
            return ResponseEntity.ok(game);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Retrieve Dart Games with Sorting
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames(@RequestParam(required = false) String sort) {
        try {
            List<Game> games = gameService.getAllGames(sort);
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }


    // Retrieve Paginated List of Dart Games
    @GetMapping("/paginated")
    public ResponseEntity<List<Game>> getPaginatedGames(@RequestParam int page, @RequestParam int limit) {
        try {
            List<Game> games = gameService.getPaginatedGames(page, limit);
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Submit a Player’s Throw in a Multiplayer Game
    @PostMapping("/{gameId}/throws")
    public ResponseEntity<String> submitThrow(@PathVariable Long gameId, @RequestBody int score) {
        try {
            gameService.submitThrow(gameId, score);
            return ResponseEntity.ok("Score submitted.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to submit throw: " + e.getMessage());
        }
    }

    // Undo Last Throw in a Game
    @PostMapping("/{gameId}/undo")
    public ResponseEntity<String> undoLastThrow(@PathVariable Long gameId) {
        try {
            gameService.undoLastThrow(gameId);
            return ResponseEntity.ok("Last throw undone.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to undo last throw: " + e.getMessage());
        }
    }

    // Retrieve Game with Dependent Data (rounds and scores)
    @GetMapping("/{gameId}/rounds")
    public ResponseEntity<Game> getGameWithRounds(@PathVariable Long gameId) {
        try {
            Game gameWithRounds = gameService.getGameWithRounds(gameId);
            return ResponseEntity.ok(gameWithRounds);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Retrieve Multiplayer Game Participants
    @GetMapping("/{gameId}/players")
    public ResponseEntity<List<User>> getGameParticipants(@PathVariable Long gameId) {
        try {
            List<User> participants = gameService.getGameParticipants(gameId);
            return ResponseEntity.ok(participants);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @GetMapping("/{gameId}")
    public String gamePage(@PathVariable Long gameId, Model model) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User currentUser = userService.getByEmail(userDetails.getUsername());

            Game game = gameService.getGameSetup(gameId);

            model.addAttribute("currentUser", currentUser);
            model.addAttribute("game", game);
            model.addAttribute("gameType", game.getGameType());
            model.addAttribute("friends", userService.getFriendsList(currentUser.getId()));

            List<User> players = gameService.getGameParticipants(gameId);
            model.addAttribute("players", players);

            System.out.println("Loaded game page for game ID: " + gameId + " and user: " + currentUser.getUsername());

            return "game";
        } catch (Exception e) {
            System.err.println("Error loading game page for game ID " + gameId + ": " + e.getMessage());
            model.addAttribute("errorMessage", "Unable to load game page. Please try again later.");
            return "error";
        }
    }

    @GetMapping("/average-score")
    public ResponseEntity<String> getThreeDartAverageScore() {
        double threeDartAverage = gameService.calculateThreeDartAverage();
        return ResponseEntity.ok("{\"threeDartAverage\": " + threeDartAverage + "}");
    }

}
