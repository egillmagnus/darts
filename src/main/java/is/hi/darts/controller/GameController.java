package is.hi.darts.controller;

import is.hi.darts.model.Game;
import is.hi.darts.model.User;
import is.hi.darts.service.GameService;
import is.hi.darts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    // Add a Friend
    @PostMapping("/friends/add")
    public ResponseEntity<String> addFriend(@RequestParam String identifier) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = userService.getByEmail(userDetails.getUsername());

        try {
            User friend;
            if (identifier.matches("\\d+")) {
                friend = userService.getById(Long.parseLong(identifier));
            } else {
                friend = userService.getByEmail(identifier);
            }

            userService.addFriend(currentUser.getId(), friend.getId());
            return ResponseEntity.ok("Friend request sent.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to add friend: " + e.getMessage());
        }
    }

    // View Friends List
    @GetMapping("/friends")
    public ResponseEntity<List<User>> getFriendsList(@RequestParam Long userId) {
        try {
            List<User> friendsList = userService.getFriendsList(userId);
            return ResponseEntity.ok(friendsList);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Accept/Reject Friend Request
    @PostMapping("/friends/requests/respond")
    public ResponseEntity<String> respondToFriendRequest(@RequestParam Long requestId, @RequestParam boolean response) {
        try {
            userService.respondToFriendRequest(requestId, response);
            return ResponseEntity.ok("Friend request " + (response ? "accepted" : "rejected"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to respond to friend request: " + e.getMessage());
        }
    }

    // Remove a Friend
    @DeleteMapping("/friends/{friendId}/remove")
    public ResponseEntity<String> removeFriend(@PathVariable Long friendId, @RequestParam Long userId) {
        try {
            userService.removeFriend(userId, friendId);
            return ResponseEntity.ok("Friend removed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to remove friend: " + e.getMessage());
        }
    }

    // View Game Setup
    @GetMapping("/{gameId}/setup")
    public ResponseEntity<Game> getGameSetup(@PathVariable Long gameId) {
        try {
            Game gameSetup = gameService.getGameSetup(gameId);
            return ResponseEntity.ok(gameSetup);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    // Start a New Game
    @PostMapping
    public ResponseEntity<Game> startNewGame(@RequestBody Game game) {
        try {
            Game createdGame = gameService.startNewGame(game);
            return ResponseEntity.ok(createdGame);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
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

    // Save Game State
    @PostMapping("/{gameId}/save")
    public ResponseEntity<String> saveGameState(@PathVariable Long gameId) {
        try {
            gameService.saveGameState(gameId);
            return ResponseEntity.ok("Game state saved.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to save game state: " + e.getMessage());
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
}
