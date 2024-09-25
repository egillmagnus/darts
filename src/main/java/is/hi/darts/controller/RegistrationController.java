package is.hi.darts.controller;

import is.hi.darts.model.User;
import is.hi.darts.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        System.out.println("");
        System.out.println(user.getUsername());
        System.out.println(user.getEmail());
        System.out.println(user.getPassword());
        try {
            // Here we are assuming that your UserService has a method to register the user
            userService.registerUser(user.getEmail(), user.getUsername(), user.getPassword());
            model.addAttribute("message", "User registered successfully!");
            return "login"; // Redirect to the login page after registration
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register"; // Stay on registration page and show error message
        }
    }
}
