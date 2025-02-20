package is.hi.darts.controller;

import is.hi.darts.model.FriendRequest;
import is.hi.darts.model.Game;
import is.hi.darts.model.MessageResponse;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;

    // Add a Friend
    @PostMapping("/friends/add")
    public ResponseEntity<MessageResponse> addFriend(@RequestParam String identifier) {
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
            return ResponseEntity.ok(new MessageResponse("Friend request sent."));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new MessageResponse("Failed to add friend: " + e.getMessage()));
        }
    }


    @PostMapping("/friends/incoming")
    public ResponseEntity<List<FriendRequest>> getIncomingFriendRequests() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getByEmail(userDetails.getUsername());
        List<FriendRequest> incomingRequests = userService.getIncomingRequests(user.getId());
        return ResponseEntity.ok(incomingRequests);
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
    @PostMapping("/friends/requests/accept")
    public ResponseEntity<MessageResponse> acceptFriendRequest(@RequestParam Long requestId) {
        try {
            userService.respondToFriendRequest(requestId, true);
            return ResponseEntity.ok(new MessageResponse("Friend request accepted"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new MessageResponse("Failed to accept friend request: " + e.getMessage()));
        }
    }
    @PostMapping("/friends/requests/decline")
    public ResponseEntity<MessageResponse> declineFriendRequest(@RequestParam Long requestId) {
        try {
            userService.respondToFriendRequest(requestId, false);
            return ResponseEntity.ok(new MessageResponse("Friend request declined"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new MessageResponse("Failed to decline friend request: " + e.getMessage()));
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

    @GetMapping("/addfriend")
    public String addFriendPage(Model model) {
        return "addfriend";
    }

    @GetMapping("/game")
    public String gamePage(Model model) {
        return "game";
    }


    @GetMapping("/users/{userId}/stats")
    public ResponseEntity<Double> getUserThreeDartAverage(@PathVariable Long userId) {
        try {
            User user = userService.getById(userId);
            double threeDartAverage = userService.calculateThreeDartAverage(user);
            return ResponseEntity.ok(threeDartAverage);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @GetMapping("/users/{userId}/games")
    public ResponseEntity<List<Game>> getUserCompletedGames(User user) {
        try {
            List<Game> games = userService.getUserCompletedGames(user.getId());
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @GetMapping("/leaderboard")
    public String getLeaderboard(Model model) {
        List<Map<String, Object>> leaderboardStats = userService.getLeaderboardStats();
        model.addAttribute("leaderboardStats", leaderboardStats);
        return "leaderboard";
    }


}
