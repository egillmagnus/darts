package is.hi.darts.controller;

import is.hi.darts.model.FriendRequest;
import is.hi.darts.model.Game;
import is.hi.darts.model.GameInvite;
import is.hi.darts.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    @Autowired
    private GameService gameService;

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String)) {
            return "redirect:/dashboard";
        }
        return "home";
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Game> setupGames = gameService.getSetupGames();
        List<Game> ongoingGames = gameService.getOngoingGames();
        model.addAttribute("ongoingGames", ongoingGames);
        model.addAttribute("setupGames", setupGames);


        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userService.getByEmail(userDetails.getUsername());

        Long userId = user.getId();
        List<GameInvite> incomingInvites = gameService.getInvitationsForUser(userId);

        System.out.println(incomingInvites.size());

        model.addAttribute("incomingInvites", incomingInvites);

        return "dashboard";
    }

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


}