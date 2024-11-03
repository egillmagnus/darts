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
}