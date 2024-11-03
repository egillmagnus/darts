package is.hi.darts.controller;

import is.hi.darts.model.FriendRequest;
import is.hi.darts.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import is.hi.darts.service.*;

import java.util.List;

@Controller
public class NavigationController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model) {return "dashboard";}

    @GetMapping("/profile")
    public String profile(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userService.getByEmail(userDetails.getUsername());

        Long userId = user.getId();
        List<FriendRequest> incomingRequests = userService.getIncomingRequests(userId);
        model.addAttribute("incomingRequests", incomingRequests);

        List<FriendRequest> outgoingRequests = userService.getOutgoingRequests(userId);
        model.addAttribute("outgoingRequests", outgoingRequests);

        List<User> friendsList = userService.getFriendsList(userId);
        model.addAttribute("friendsList", friendsList);

        return "profile";
    }

    @GetMapping("/addfriend")
    public String addFriendPage() {
        return "addFriend";
    }

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

    @GetMapping("/friends")
    public ResponseEntity<List<User>> getFriendsList(@RequestParam Long userId) {
        try {
            List<User> friendsList = userService.getFriendsList(userId);
            return ResponseEntity.ok(friendsList);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @PostMapping("/friends/requests/respond")
    public ResponseEntity<String> respondToFriendRequest(@RequestParam Long requestId, @RequestParam boolean response) {
        try {
            userService.respondToFriendRequest(requestId, response);
            return ResponseEntity.ok("Friend request " + (response ? "accepted" : "rejected"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to respond to friend request: " + e.getMessage());
        }
    }

    @PostMapping("/friends/{friendId}/remove")
    public ResponseEntity<String> removeFriend(@PathVariable Long friendId ) {
        try {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            User user = userService.getByEmail(userDetails.getUsername());

            Long userId = user.getId();
            userService.removeFriend(userId, friendId);
            return ResponseEntity.ok("Friend removed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to remove friend: " + e.getMessage());
        }
    }

}