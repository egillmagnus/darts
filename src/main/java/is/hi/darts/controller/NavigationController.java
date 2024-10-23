package is.hi.darts.controller;

import is.hi.darts.model.FriendRequest;
import is.hi.darts.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        // Get the logged-in user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Assuming you have a method in your UserService to get the user by email or username
        User user = userService.getByEmail(userDetails.getUsername()); // or userDetails.getUsername(

        Long userId = user.getId();
        // Fetch incoming, outgoing friend requests, and friends list
        List<FriendRequest> incomingRequests = userService.getIncomingRequests(userId);
        model.addAttribute("incomingRequests", incomingRequests);

        List<FriendRequest> outgoingRequests = userService.getOutgoingRequests(userId);
        model.addAttribute("outgoingRequests", outgoingRequests);

        List<User> friendsList = userService.getFriendsList(userId);
        model.addAttribute("friendsList", friendsList);

        return "profile"; // Return the Thymeleaf template
    }

}