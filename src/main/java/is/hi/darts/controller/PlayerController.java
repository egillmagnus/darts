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
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    private UserService userService;
    @Autowired
    private GameService gameService;

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

}
