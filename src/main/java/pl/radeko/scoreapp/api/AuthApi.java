package pl.radeko.scoreapp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import pl.radeko.scoreapp.manager.AuthManager;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/auth/")
public class AuthApi {

    private final AuthManager auths;

    @Autowired
    public AuthApi(AuthManager auths) {
        this.auths = auths;
    }


    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public RedirectView login(@RequestParam String username, @RequestParam String password, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        // checking whether the given data is correct
        if (auths.login(username, password, request)) {
            // redirection to main page after successful login
            return new RedirectView("/api/teams/index");
        } else {
            redirectAttributes.addFlashAttribute("message", "Wystąpił błąd podczas logowania");
            // displaying an error and leaving  the user on the login page
            return new RedirectView("/api/auth/login");
        }
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public RedirectView register(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes) {
        // checking, if username is not taken
        if (auths.register(username, password)) {
            // displaying a success info and redirection to login page after registration
            redirectAttributes.addFlashAttribute("message", "Rejestracja zakończona pomyślnie.");
            return new RedirectView("/api/auth/login");
        } else {
            // displaying an error and leaving the user on the registration page
            redirectAttributes.addFlashAttribute("message", "Wystąpił błąd podczas rejestracji.");
            return new RedirectView("/api/auth/register");
        }
    }

/*
    @PreAuthorize("hasAuthority(Role.ADMIN.name())")
    @GetMapping("/api/admin/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/api/users")
    public List<User> getAllUsers(Authentication authentication) {
        List<User> users = new ArrayList<>();
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Role.ADMIN.name()))) {
            users = userRepository.findAll();
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Role.USER.name()))) {
            Optional<User> user = userRepository.findByUsername(authentication.getName());
            if(user.isPresent()) {
                users.add(user.get());
            }
        }
        return users;
    }*/

}
