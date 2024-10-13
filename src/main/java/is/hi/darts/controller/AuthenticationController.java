package is.hi.darts.controller;

import is.hi.darts.model.User;
import is.hi.darts.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm() {
        System.out.println("showLoginForm");
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request,
            Model model) {
        try {
            User user = userService.loginUser(email, password);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            System.out.println("Logged in with email: " + email);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("loginError", true);
            System.out.println("Login failed");
            return "login";
        }
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, @RequestParam("email") String email,
                               @RequestParam("username") String displayName,
                               @RequestParam("password") String password, Model model) {
        System.out.println(email);        // For debugging, ensure correct data is received
        System.out.println(displayName);  // Display Name (username)
        System.out.println(password);     // Password

        try {
            userService.registerUser(email, displayName, password);
            model.addAttribute("message", "User registered successfully!");
            System.out.println("User registered successfully!");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            System.out.println("Registration failed");
            return "register";
        }
    }


    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return "redirect:/";
    }

}
