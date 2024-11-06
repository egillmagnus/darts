package is.hi.darts.controller;

import is.hi.darts.model.Game;
import is.hi.darts.model.GameStatus;
import is.hi.darts.model.Player;
import is.hi.darts.model.User;
import is.hi.darts.repository.GameInviteRepository;
import is.hi.darts.service.GameService;
import is.hi.darts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/games")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;
    @Autowired
    private GameInviteRepository gameInviteRepository;

    @GetMapping("/{gameId}/setup")
    public String getGameSetup(@PathVariable Long gameId, Model model) {
        try {
            Game gameSetup = gameService.getGameSetup(gameId);
            if(gameSetup.getStatus() == GameStatus.ONGOING) {
                return "redirect:/games/" + gameId;
            } else if (gameSetup.getStatus() == GameStatus.COMPLETED) {
                return "redirect:/games/" + gameId +"/stats";
            }
            model.addAttribute("gameId", gameSetup.getId());
            model.addAttribute("gameType", gameSetup.getGameType());
            model.addAttribute("players", gameSetup.getPlayers());
            model.addAttribute("legs", gameSetup.getTotalLegs());
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.getByEmail(userDetails.getUsername());
            Long userId = user.getId();
            List<User> friendsList = userService.getFriendsList(userId);
            model.addAttribute("friendsList", friendsList);
            return "gamesetup";
        } catch (Exception e) {
            return "error";
        }
    }

    // Create a New Game
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
            System.out.println(friendId);
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.getByEmail(userDetails.getUsername());
            Long userId = user.getId();
            gameService.inviteFriendToGame(gameId, friendId, userId);
            return ResponseEntity.ok("Friend invited to the game.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to invite friend: " + e.getMessage());
        }
    }

    @PostMapping("/invitations/decline")
    public ResponseEntity<String> declineInvitation(@RequestBody Long invitationId) {
        try {
            gameService.declineInvitation(invitationId);
            return ResponseEntity.ok("Invitation declined.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to decline invitation: " + e.getMessage());
        }
    }

    // Join a Multiplayer Game
    @PostMapping("/{inviteId}/accept")
    public ResponseEntity<String> joinMultiplayerGame(@PathVariable Long inviteId) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.getByEmail(userDetails.getUsername());
            Long userId = user.getId();
            Long gameId = gameService.acceptInvitation(inviteId, userId);
            return ResponseEntity.status(302).header("Location", "/games/" + gameId).build();
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to join the game: " + e.getMessage());
        }
    }

    @PostMapping("/{gameId}/start")
    public ResponseEntity<String> startGame(@PathVariable Long gameId, @RequestBody Map<String, Object> gameSettings) {
        try {
            String gameMode = (String) gameSettings.get("gameMode");
            Integer numLegs = Integer.valueOf(gameSettings.get("numLegs").toString());

            Game game = gameService.getGameSetup(gameId);

            game.setGameType(gameMode);

            game.setStatus(GameStatus.ONGOING);

            gameService.updateGameSetup(gameId, game);

            return ResponseEntity.ok("Game started successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to start the game: " + e.getMessage());
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
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User currentUser = userService.getByEmail(userDetails.getUsername());

            Game game = gameService.getGameSetup(gameId);
            Player currentPlayer = game.getCurrentPlayer();

            // Check if it's the current user's turn
            if (!currentPlayer.getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body("It's not your turn.");
            }
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

            if(game.getStatus() == GameStatus.SETUP) {
                return "redirect:/games/" + gameId + "/setup";
            } else if (game.getStatus() == GameStatus.COMPLETED) {
                return "redirect:/games/" + gameId +"/stats";
            }

            model.addAttribute("currentUser", currentUser);
            model.addAttribute("game", game);

            List<Player> players = game.getPlayers();
            model.addAttribute("players", players);

            int currentUserIndex = 0;

            for (int i = 0; i < players.size(); i++) {
                if (Objects.equals(players.get(i).getId(), currentUser.getId())) {
                    currentUserIndex = i;
                    break;
                }
            }
            List<Double> threeDartAverages = new ArrayList<>();
            List<Double> first9Averages = new ArrayList<>();
            List<Double> lastScores = new ArrayList<>();
            List<Long> dartsThrown = new ArrayList<>();
            List<Long> legsWon = new ArrayList<>();
            List<String> userNames = new ArrayList<>();

            for (Player player : players) {
                Long playerId = player.getId();

                // Populate each list with values for the current player
                threeDartAverages.add(game.getGameThreeDartAverage(playerId));
                first9Averages.add(game.getGameFirst9Average(playerId));
                lastScores.add(game.getLastScore(playerId));
                dartsThrown.add(game.getDartsThrown(playerId));
                legsWon.add(player.getLegsWon());
                userNames.add(player.getName());
            }



            model.addAttribute("userIndex", currentUserIndex);
            model.addAttribute("threeDartAverages", threeDartAverages);
            model.addAttribute("first9Averages", first9Averages);
            model.addAttribute("lastScores", lastScores);
            model.addAttribute("dartsThrown", dartsThrown);
            model.addAttribute("legsWon", legsWon);
            model.addAttribute("userNames", userNames);

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

    @DeleteMapping("/{gameId}")
    public ResponseEntity<String> deleteGame(@PathVariable Long gameId) throws Exception {
        try {
            gameService.deleteGame(gameId);
            return ResponseEntity.ok("Game deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only games in SETUP state can be deleted.");
        }
    }


}
