package is.hi.darts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Display login page
    @GetMapping("/login")
    public String login(Model model) {
        return "login";  // This will render login.html in the templates folder
    }

    // Optional: Handle login failure
    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login";
    }
}
