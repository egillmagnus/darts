package is.hi.darts.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavigationController {

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model) {return "dashboard";}
}